package fr.gouv.agriculture.ift.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.gouv.agriculture.ift.dto.BilanCertifieDTO;
import fr.gouv.agriculture.ift.dto.ParcelleCultiveeDTO;
import fr.gouv.agriculture.ift.exception.NotFoundException;
import fr.gouv.agriculture.ift.exception.ServerException;
import fr.gouv.agriculture.ift.model.*;
import fr.gouv.agriculture.ift.repository.BilanRepository;
import fr.gouv.agriculture.ift.service.*;
import fr.gouv.agriculture.ift.util.DateUtils;
import fr.gouv.agriculture.ift.util.Views;
import fr.gouv.agriculture.ift.util.pdf.CustomFormatter;
import fr.gouv.agriculture.ift.util.pdf.PDFGeneratorUtil;
import fr.gouv.agriculture.ift.util.pdf.QrCodeGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static fr.gouv.agriculture.ift.Constants.*;

@Slf4j
@Service
public class BilanIftServiceImpl implements BilanIftService {

    @Autowired
    private IftService iftService;

    @Autowired
    IftTraitementService iftTraitementService;

    @Autowired
    private PDFGeneratorUtil pdfGeneratorUtil;

    @Autowired
    private ConfigurationService configurationService;

    @Autowired
    private CampagneService campagneService;

    @Autowired
    private CultureService cultureService;

    @Autowired
    private BilanRepository repository;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public BilanDTO getBilanById(UUID bilanId) {
        Bilan bilan = this.repository.findOne(bilanId);
        if (bilan == null) {
            throw new NotFoundException();
        } else {
            Campagne campagne = null;
            try {
                campagne = this.objectMapper.readValue(bilan.getCampagneJson(), Campagne.class);
            } catch (IOException e) {
                e.printStackTrace();
                throw new ServerException("Erreur lors de la récupération du bilan");
            }
            bilan.setCampagne(campagne);

            bilan.getParcelleCultivees().forEach(parcelleCultivee -> {
                Culture culture = null;
                try {
                    culture = this.objectMapper.readValue(parcelleCultivee.getCultureJson(), Culture.class);
                } catch (IOException e) {
                    e.printStackTrace();
                    throw new ServerException("Erreur lors de la récupération du bilan");
                }
                parcelleCultivee.setCulture(culture);

                parcelleCultivee.setTraitements(new ArrayList<>());
                parcelleCultivee.getSignatures().forEach(signature -> {
                    try {
                        parcelleCultivee.getTraitements().add(iftTraitementService.getIftTraitementFromSignature(signature));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            });

            BilanDTO bilanDTO = getBilan(bilan.getParcelleCultivees(), true);
            bilanDTO.setCampagne(campagne);
            return bilanDTO;
        }
    }

    @Override
    public BilanDTO getBilan(List<ParcelleCultiveeDTO> parcellesCultiveesDTO) {
        List<ParcelleCultivee> parcelleCultiveeList = new ArrayList<>();
        parcellesCultiveesDTO.forEach(parcelleCultiveeDTO -> {
            Culture culture = cultureService.findCultureByIdMetier(parcelleCultiveeDTO.getCulture().getIdMetier());
            ParcelleCultivee parcelleCultivee = ParcelleCultivee.builder()
                    .id(UUID.randomUUID())
                    .parcelle(parcelleCultiveeDTO.getParcelle())
                    .culture(culture)
                    .traitements(parcelleCultiveeDTO.getTraitements())
                    .build();
            parcelleCultiveeList.add(parcelleCultivee);
        });

        return getBilan(parcelleCultiveeList, false);
    }



    @Override
    public BilanCertifieDTO getBilanCertifie(List<ParcelleCultiveeDTO> parcellesCultiveesDTO, String campagneIdMetier) {
        BilanDTO bilanDTO = getBilan(parcellesCultiveesDTO);
        Campagne campagne = campagneService.findCampagneByIdMetier(campagneIdMetier);
        bilanDTO.setCampagne(campagne);

        List<ParcelleCultivee> parcellesCultivees = bilanDTO.getBilanParcellesCultivees().stream()
                .map(BilanParcelleCultivee::getParcelleCultivee)
                .collect(Collectors.toList());

        parcellesCultivees.forEach(parcelleCultivee -> {
            try {
                String cultureJson = this.objectMapper
                        .writerWithView(Views.Public.class)
                        .writeValueAsString(parcelleCultivee.getCulture());
                parcelleCultivee.setCultureJson(cultureJson);
            } catch (IOException e) {
                e.printStackTrace();
                throw new ServerException("Erreur lors de la signature de l'IFT");
            }

            parcelleCultivee.setSignatures(new ArrayList<>());
            parcelleCultivee.getTraitements().forEach(iftTraitement -> {
                Signature signature = iftTraitementService.signeIftTraitement(iftTraitement);
                iftTraitement.setQrCodeUrl(configurationService.getValue(CONF_FRONT_END_URL_VERIFIER_IFT) + "/" + signature.getId());
                parcelleCultivee.getSignatures().add(signature);
            });
        });

        try {
            String campagneJson = this.objectMapper
                    .writerWithView(Views.Public.class)
                    .writeValueAsString(campagne);

            Bilan bilan = Bilan.builder()
                    .id(UUID.randomUUID())
                    .parcelleCultivees(parcellesCultivees)
                    .campagneJson(campagneJson)
                    .build();

            repository.save(bilan);
            return BilanCertifieDTO.builder()
                    .bilanDTO(bilanDTO)
                    .verificationUrl(configurationService.getValue(CONF_FRONT_END_URL_VERIFIER_BILAN) + "/" + bilan.getId())
                    .build();
        } catch (IOException e) {
            e.printStackTrace();
            throw new ServerException("Erreur lors de la signature de l'IFT");
        }
    }

    @Override
    public BilanDTO getBilan(List<ParcelleCultivee> parcellesCultivees, boolean fromSignature) {
        BilanDTO bilanDTO = BilanDTO.builder()
                .bilanParcellesCultivees(new ArrayList<>())
                .bilanGroupesCultures(new ArrayList<>())
                .bilanParcelles(new ArrayList<>())
                .bilanParSegment(new BilanParSegment())
                .build();

        BigDecimal totalSurfaceToutesParcelles = parcellesCultivees.stream()
                .map(parcelleCultivees -> parcelleCultivees.getParcelle().getSurface())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BilanParSegment total = new BilanParSegment();

        for (ParcelleCultivee parcelleCultivee : parcellesCultivees) {

            BilanParcelleCultivee bilanParcelleCultivee = BilanParcelleCultivee.builder()
                    .bilanParSegment(new BilanParSegment())
                    .build();

            ArrayList<IftTraitement> traitements = new ArrayList<>();

            for (IftTraitement inputIftTraitement : parcelleCultivee.getTraitements()) {
                IftTraitement iftTraitement;
                if (!fromSignature) {
                    iftTraitement = iftService.computeIftTraitement(inputIftTraitement.getCampagne().getIdMetier(), inputIftTraitement.getNumeroAmm() != null ? inputIftTraitement.getNumeroAmm().getIdMetier() : null, inputIftTraitement.getCulture().getIdMetier(), inputIftTraitement.getCible() != null ? inputIftTraitement.getCible().getIdMetier() : null, inputIftTraitement.getTypeTraitement().getIdMetier(), inputIftTraitement.getUnite() != null ? inputIftTraitement.getUnite().getIdMetier() : null, inputIftTraitement.getDose(), inputIftTraitement.getVolumeDeBouillie(), inputIftTraitement.getFacteurDeCorrection());

                    if (!iftTraitement.getTypeTraitement().getAvantSemis()) {
                        iftTraitement.setProduitLibelle(inputIftTraitement.getProduitLibelle());
                    }
                    iftTraitement.setDateTraitement(inputIftTraitement.getDateTraitement());
                    iftTraitement.setCommentaire(inputIftTraitement.getCommentaire());
                } else {
                    iftTraitement = inputIftTraitement;
                }

                switch (iftTraitement.getSegment().getIdMetier()) {
                    case "S1":
                        bilanParcelleCultivee.getBilanParSegment().setSemences(bilanParcelleCultivee.getBilanParSegment().getSemences().add(iftTraitement.getIft()));
                        total.setSemences(total.getSemences().add(iftTraitement.getIft().multiply(parcelleCultivee.getParcelle().getSurface())));
                        break;
                    case "S2":
                        bilanParcelleCultivee.getBilanParSegment().setBiocontrole(bilanParcelleCultivee.getBilanParSegment().getBiocontrole().add(iftTraitement.getIft()));
                        total.setBiocontrole(total.getBiocontrole().add(iftTraitement.getIft().multiply(parcelleCultivee.getParcelle().getSurface())));
                        break;
                    case "S3":
                        bilanParcelleCultivee.getBilanParSegment().setHerbicide(bilanParcelleCultivee.getBilanParSegment().getHerbicide().add(iftTraitement.getIft()));
                        total.setHerbicide(total.getHerbicide().add(iftTraitement.getIft().multiply(parcelleCultivee.getParcelle().getSurface())));
                        break;
                    case "S4":
                        bilanParcelleCultivee.getBilanParSegment().setInsecticidesAcaricides(bilanParcelleCultivee.getBilanParSegment().getInsecticidesAcaricides().add(iftTraitement.getIft()));
                        total.setInsecticidesAcaricides(total.getInsecticidesAcaricides().add(iftTraitement.getIft().multiply(parcelleCultivee.getParcelle().getSurface())));
                        break;
                    case "S5":
                        bilanParcelleCultivee.getBilanParSegment().setFongicidesBactericides(bilanParcelleCultivee.getBilanParSegment().getFongicidesBactericides().add(iftTraitement.getIft()));
                        total.setFongicidesBactericides(total.getFongicidesBactericides().add(iftTraitement.getIft().multiply(parcelleCultivee.getParcelle().getSurface())));
                        break;
                    case "S6":
                        bilanParcelleCultivee.getBilanParSegment().setAutres(bilanParcelleCultivee.getBilanParSegment().getAutres().add(iftTraitement.getIft()));
                        total.setAutres(total.getAutres().add(iftTraitement.getIft().multiply(parcelleCultivee.getParcelle().getSurface())));
                        break;
                }

                traitements.add(iftTraitement);
            }

            if (!fromSignature) {
                if (parcelleCultivee.getId() == null) {
                    parcelleCultivee.setId(UUID.randomUUID());
                }
                if (parcelleCultivee.getParcelle().getId() == null) {
                    parcelleCultivee.getParcelle().setId(UUID.randomUUID());
                }
            }
            parcelleCultivee.setTraitements(traitements);
            bilanParcelleCultivee.setParcelleCultivee(parcelleCultivee);

            bilanParcelleCultivee.getBilanParSegment().setTotal(getTotalSegments(bilanParcelleCultivee.getBilanParSegment()));
            bilanParcelleCultivee.getBilanParSegment().setSurface(parcelleCultivee.getParcelle().getSurface());
            bilanDTO.getBilanParcellesCultivees().add(bilanParcelleCultivee);
        }

        bilanDTO.setBilanParSegment(getBilanParSegment(total, totalSurfaceToutesParcelles));

        bilanDTO.setBilanGroupesCultures(getBilanGroupeCultures(bilanDTO.getBilanParcellesCultivees()));
        bilanDTO.setBilanParcelles(getBilanParcelles(bilanDTO.getBilanParcellesCultivees()));

        bilanDTO.getBilanParcellesCultivees().forEach(
                bilanParcelleCultivee -> bilanParcelleCultivee.getParcelleCultivee().getTraitements()
                        .sort(Comparator.comparing(IftTraitement::getDateTraitement,
                                Comparator.nullsLast(Comparator.naturalOrder())))
        );
        return bilanDTO;
    }

    @Override
    public void getBilanPDF(OutputStream out, String titre, BilanCertifieDTO bilanCertifieDTO) {
            byte[] qrCode = QrCodeGenerator.generateQrCode(bilanCertifieDTO.getVerificationUrl());

            Integer nombreDeTraitements = bilanCertifieDTO.getBilanDTO().getBilanParcellesCultivees().stream()
                    .map(bilanParcelleCultivee -> bilanParcelleCultivee.getParcelleCultivee().getTraitements())
                    .filter(Objects::nonNull)
                    .mapToInt(List::size)
                    .sum();

            Context ctx = new Context();
            ctx.setVariable("bilanDTO", bilanCertifieDTO.getBilanDTO());
            ctx.setVariable("campagne", bilanCertifieDTO.getBilanDTO().getCampagne());
            ctx.setVariable("nombreDeTraitements", nombreDeTraitements);
            ctx.setVariable("customFormatter", new CustomFormatter());
            ctx.setVariable("editeParUrl", configurationService.getValue(CONF_FRONT_END_URL));
            ctx.setVariable("qrCodeUrl", bilanCertifieDTO.getVerificationUrl());
            ctx.setVariable("dateEdition", DateUtils.parseLocalDateTime(LocalDateTime.now()));
            ctx.setVariable("titre", titre);

            try {
                pdfGeneratorUtil.createPdf(out, ctx, "bilan", qrCode);
            } catch (Exception e) {
                e.printStackTrace();
            }
    }

    private List<BilanParcelle> getBilanParcelles(List<BilanParcelleCultivee> bilanParcelleCultivees) {
        Map<String, List<BilanParcelleCultivee>> bilanParcelleMap = new HashMap<>();

        bilanParcelleCultivees.forEach(bilanParcelleCultivee -> {
            Parcelle parcelle = bilanParcelleCultivee.getParcelleCultivee().getParcelle();
            if (!bilanParcelleMap.containsKey(parcelle.getNom())) {
                List<BilanParcelleCultivee> bilanParcelleCultiveeList = new ArrayList<>();
                bilanParcelleCultiveeList.add(bilanParcelleCultivee);
                bilanParcelleMap.put(parcelle.getNom(), bilanParcelleCultiveeList);
            } else {
                bilanParcelleMap.get(parcelle.getNom()).add(bilanParcelleCultivee);
            }

        });

        List<BilanParcelle> bilanParcelles = new ArrayList<>();
        bilanParcelleMap.forEach((parcelle, bilansParcelleCultivee) -> {

            BigDecimal surfaceTotale = getSurfaceTotale(bilansParcelleCultivee);
            BilanParSegment bilanParSegment = getBilanParSegment(bilansParcelleCultivee, surfaceTotale);
            bilanParSegment.setSurface(surfaceTotale);

            BilanParcelle bilanParcelle = BilanParcelle.builder()
                    .parcelle(Parcelle.builder().id(UUID.randomUUID())
                            .nom(parcelle)
                            .surface(surfaceTotale).build())
                    .bilanParSegment(bilanParSegment)
                    .build();

            bilanParcelles.add(bilanParcelle);
        });

        return bilanParcelles;
    }

    private List<BilanGroupeCultures> getBilanGroupeCultures(List<BilanParcelleCultivee> bilanParcelleCultivees) {
        Map<GroupeCultures, BilanGroupeCultures> bilanGroupeCulturesMap = new HashMap<>();
        Map<Culture, BilanCulture> bilanCultureMap = new HashMap<>();

        bilanParcelleCultivees.forEach(bilanParcelleCultivee -> {
            Culture culture = bilanParcelleCultivee.getParcelleCultivee().getCulture();
            if (!bilanCultureMap.containsKey(culture)) {
                BilanCulture bilanCulture = BilanCulture.builder()
                        .culture(culture)
                        .bilanParcellesCultivees(new ArrayList<>())
                        .bilanParSegment(new BilanParSegment())
                        .build();
                bilanCultureMap.put(culture, bilanCulture);
            }

            bilanCultureMap.get(culture).getBilanParcellesCultivees().add(bilanParcelleCultivee);
        });


        bilanCultureMap.forEach((culture, bilanCulture) -> {
            BigDecimal surfaceTotale = getSurfaceTotale(bilanCulture.getBilanParcellesCultivees());
            bilanCulture.setBilanParSegment(getBilanParSegment(bilanCulture.getBilanParcellesCultivees(), surfaceTotale));
            bilanCulture.getBilanParSegment().setSurface(surfaceTotale);

            GroupeCultures groupeCultures = culture.getGroupeCultures();
            if (!bilanGroupeCulturesMap.containsKey(groupeCultures)) {
                BilanGroupeCultures bilanGroupeCultures = BilanGroupeCultures.builder()
                        .groupeCultures(groupeCultures)
                        .bilanCultures(new ArrayList<>())
                        .build();
                bilanGroupeCulturesMap.put(groupeCultures, bilanGroupeCultures);
            }

            bilanGroupeCulturesMap.get(groupeCultures).getBilanCultures().add(bilanCulture);
        });

        List<BilanGroupeCultures> bilanGroupesCultures = new ArrayList<>();
        bilanGroupeCulturesMap.forEach(((groupeCultures, bilanGroupeCultures) -> {
            List<BilanParcelleCultivee> bilanParcellesCultivees = bilanGroupeCultures.getBilanCultures()
                    .stream()
                    .map(BilanCulture::getBilanParcellesCultivees)
                    .flatMap(List::stream)
                    .collect(Collectors.toList());

            BigDecimal surfaceTotale = getSurfaceTotale(bilanParcellesCultivees);
            bilanGroupeCultures.setBilanParSegment(getBilanParSegment(bilanParcellesCultivees, surfaceTotale));
            bilanGroupeCultures.getBilanParSegment().setSurface(surfaceTotale);

            bilanGroupesCultures.add(bilanGroupeCultures);
        }));

        return bilanGroupesCultures;
    }

    private BigDecimal getTotalSegments(BilanParSegment total) {
        return total.getSemences()
                .add(total.getBiocontrole())
                .add(total.getHerbicide())
                .add(total.getInsecticidesAcaricides())
                .add(total.getFongicidesBactericides())
                .add(total.getAutres());
    }

    private BigDecimal getSurfaceTotale(List<BilanParcelleCultivee> bilanParcellesCultivees) {
        return bilanParcellesCultivees.stream()
                .map(bilanParcelleCultivee -> bilanParcelleCultivee.getParcelleCultivee().getParcelle().getSurface())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal getMoyennePonderee(String segment, List<BilanParcelleCultivee> bilanParcellesCultivees, BigDecimal surfaceTotale) {
        BigDecimal bilanParSegment = bilanParcellesCultivees.stream()
                .map(bilanParcelleCultivee -> bilanParcelleCultivee.getParcelleCultivee().getTraitements().stream()
                        .map(inputIftTraitement -> {
                            if (inputIftTraitement.getSegment().getIdMetier().equals(segment)) {
                                return inputIftTraitement.getIft().multiply(bilanParcelleCultivee.getParcelleCultivee().getParcelle().getSurface());
                            } else {
                                return new BigDecimal("0");
                            }
                        })
                        .reduce(BigDecimal.ZERO, BigDecimal::add))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return bilanParSegment.divide(surfaceTotale, 10, RoundingMode.HALF_EVEN);
    }

    private BilanParSegment getBilanParSegment(BilanParSegment total, BigDecimal surface) {
        BilanParSegment bilanParSegment = new BilanParSegment();
        bilanParSegment.setSemences(total.getSemences().divide(surface, 10, RoundingMode.HALF_EVEN));
        bilanParSegment.setBiocontrole(total.getBiocontrole().divide(surface, 10, RoundingMode.HALF_EVEN));
        bilanParSegment.setHerbicide(total.getHerbicide().divide(surface, 10, RoundingMode.HALF_EVEN));
        bilanParSegment.setInsecticidesAcaricides(total.getInsecticidesAcaricides().divide(surface, 10, RoundingMode.HALF_EVEN));
        bilanParSegment.setFongicidesBactericides(total.getFongicidesBactericides().divide(surface, 10, RoundingMode.HALF_EVEN));
        bilanParSegment.setAutres(total.getAutres().divide(surface, 10, RoundingMode.HALF_EVEN));
        bilanParSegment.setTotal(getTotalSegments(bilanParSegment));
        bilanParSegment.setSurface(surface);
        return bilanParSegment;
    }

    private BilanParSegment getBilanParSegment(List<BilanParcelleCultivee> bilanParcellesCultivees, BigDecimal surfaceTotale) {
        BilanParSegment bilanParSegment = new BilanParSegment();
        bilanParSegment.setSemences(getMoyennePonderee("S1", bilanParcellesCultivees, surfaceTotale));
        bilanParSegment.setBiocontrole(getMoyennePonderee("S2", bilanParcellesCultivees, surfaceTotale));
        bilanParSegment.setHerbicide(getMoyennePonderee("S3", bilanParcellesCultivees, surfaceTotale));
        bilanParSegment.setInsecticidesAcaricides(getMoyennePonderee("S4", bilanParcellesCultivees, surfaceTotale));
        bilanParSegment.setFongicidesBactericides(getMoyennePonderee("S5", bilanParcellesCultivees, surfaceTotale));
        bilanParSegment.setAutres(getMoyennePonderee("S6", bilanParcellesCultivees, surfaceTotale));
        bilanParSegment.setTotal(getTotalSegments(bilanParSegment));
        return bilanParSegment;
    }
}
