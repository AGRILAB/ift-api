package fr.gouv.agriculture.ift.service.impl;

import fr.gouv.agriculture.ift.exception.InvalidParameterException;
import fr.gouv.agriculture.ift.exception.NotFoundException;
import fr.gouv.agriculture.ift.model.*;
import fr.gouv.agriculture.ift.model.enumeration.TypeDeConversion;
import fr.gouv.agriculture.ift.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Slf4j
@Service
public class IftServiceImpl implements IftService {

    @Autowired
    private CampagneService campagneService;

    @Autowired
    private CultureService cultureService;

    @Autowired
    private CibleService cibleService;

    @Autowired
    private TraitementService traitementService;

    @Autowired
    private SegmentService segmentService;

    @Autowired
    private UniteService uniteService;

    @Autowired
    private NumeroAmmService numeroAmmService;

    @Autowired
    private DoseReferenceService doseReferenceService;

    @Autowired
    private AvertissementService avertissementService;

    @Override
    public IftTraitement computeIftTraitement(String campagneIdMetier, String numeroAmmIdMetier, String cultureIdMetier, String cibleIdMetier, String traitementIdMetier, String uniteIdMetier, BigDecimal dose, BigDecimal volumeDeBouillie, BigDecimal facteurDeCorrection) {
        IftTraitement ift = new IftTraitement();

        facteurDeCorrection = facteurDeCorrection != null ? facteurDeCorrection : new BigDecimal("100");
        ift.setFacteurDeCorrection(facteurDeCorrection);

        Campagne campagne = getCampagne(campagneIdMetier);
        ift.setCampagne(campagne);
        Traitement traitement = getTraitement(traitementIdMetier);
        ift.setTraitement(traitement);

        // 1. Traitement de semences
        if (traitement.getAvantSemis()) {
            ift.setIft(getDefaultIft(facteurDeCorrection));
            ift.setSegment(getSegment(false, null, traitement));
            return ift;
        }

        Culture culture = getCulture(cultureIdMetier);
        ift.setCulture(culture);

        try {
            NumeroAmm numeroAmm = getNumeroAmm(numeroAmmIdMetier);
            ift.setNumeroAmm(numeroAmm);

            // 2. Autres traitements, on va au plus précis d'abord
            // 2.1. Calcul par dose référence à la cible
            if (!StringUtils.isEmpty(cibleIdMetier)) {
                Cible cible = getCible(cibleIdMetier);
                ift.setCible(cible);

                DoseReference doseReferenceCible = null;
                try {
                    doseReferenceCible = doseReferenceService.findDoseReferenceByCampagneAndCultureAndNumeroAmmAndCible(campagne.getIdMetier(), culture.getIdMetier(), numeroAmm.getIdMetier(), cible.getIdMetier());
                } catch (NotFoundException nfe) {
                    //Nothing to do;
                }
                if (doseReferenceCible != null) {
                    return getIftTraitement(ift, uniteIdMetier, doseReferenceCible.getUnite(), doseReferenceCible.getBiocontrole(), volumeDeBouillie, dose, doseReferenceCible.getDose(), facteurDeCorrection, traitement, doseReferenceCible.getSegment());
                }
            }

            // 2.2. Calcul par dose référence à la culture
            DoseReference doseReferenceCulture = null;

            try {
                doseReferenceCulture = doseReferenceService.findDoseReferenceByCampagneAndCultureAndNumeroAmmAndCible(campagne.getIdMetier(), culture.getIdMetier(), numeroAmm.getIdMetier(), null);
            } catch (NotFoundException nfe) {
                //Nothing to do
            }

            if (doseReferenceCulture != null) {
                Avertissement avertissement = avertissementService.findAvertissementByIdMetier("AV2");
                ift.setAvertissement(avertissement);
                ift = getIftTraitement(ift, uniteIdMetier, doseReferenceCulture.getUnite(), doseReferenceCulture.getBiocontrole(), volumeDeBouillie, dose, doseReferenceCulture.getDose(), facteurDeCorrection, traitement, doseReferenceCulture.getSegment());
                return ift;
            }
        } catch (Exception ignoredException) { }

        // 2.3. Hypothèse de calcul par défault
        ift.setSegment(getSegment(false, null, traitement));
        ift.setIft(getDefaultIft(facteurDeCorrection));
        Avertissement avertissement = avertissementService.findAvertissementByIdMetier("AV1");
        ift.setAvertissement(avertissement);
        return ift;
    }

    private IftTraitement getIftTraitement(IftTraitement ift, String uniteDoseAppliqueeId, Unite uniteDoseReference, boolean biocontrole, BigDecimal volumeDeBouillie, BigDecimal doseAppliquee, BigDecimal doseReference, BigDecimal facteurDeCorrection, Traitement traitement, Segment segment) {
        ift.setSegment(getSegment(biocontrole, segment, traitement));

        // Produit sans dose de référence
        if (uniteDoseReference.getIdMetier().equals("U0") || doseReference == null) {
            ift.setIft(getDefaultIft(facteurDeCorrection));
            return ift;
        }

        if (doseAppliquee != null) {
            ift.setDose(doseAppliquee.stripTrailingZeros());
        }
        if (volumeDeBouillie != null) {
            ift.setVolumeDeBouillie(volumeDeBouillie.stripTrailingZeros());
        }
        Unite uniteDoseAppliquee;
        if (uniteDoseAppliqueeId != null) {
            uniteDoseAppliquee = getUnite(uniteDoseAppliqueeId);
            ift.setUnite(uniteDoseAppliquee);
        } else {
            uniteDoseAppliquee = null;
        }

        // Unité dose non renseignée ou dose non renseignée
        if (uniteDoseAppliquee == null || doseAppliquee == null) {
            Avertissement avertissement = avertissementService.findAvertissementByIdMetier("AV3");
            ift.setAvertissement(avertissement);
            ift.setIft(getDefaultIft(facteurDeCorrection));
            return ift;
        }

        // Unité dose = unité dose ref
        if (uniteDoseAppliquee.getIdMetier().equals(uniteDoseReference.getIdMetier())) {
            ift.setIft(
                    doseAppliquee.divide(doseReference, 10, RoundingMode.CEILING).multiply(getDefaultIft(facteurDeCorrection))
            );
            return ift;
        }

        if (uniteDoseAppliquee.getUniteDeConversion() != null && uniteDoseAppliquee.getUniteDeConversion().getUnite().getId().equals(uniteDoseReference.getId()) && uniteDoseAppliquee.getUniteDeConversion().getType().equals(TypeDeConversion.DIVISION)) {
            if (volumeDeBouillie != null) {
                ift.setIft(
                        doseAppliquee.divide(volumeDeBouillie, 10, RoundingMode.CEILING).divide(doseReference, 10, RoundingMode.CEILING).multiply(facteurDeCorrection)
                );
                return ift;
            } else {
                Avertissement avertissement = avertissementService.findAvertissementByIdMetier("AV4");
                ift.setAvertissement(avertissement);
                ift.setIft(getDefaultIft(facteurDeCorrection));
                return ift;
            }
        } else if (uniteDoseAppliquee.getUniteDeConversion() != null && uniteDoseAppliquee.getUniteDeConversion().getUnite().getId().equals(uniteDoseReference.getId()) && uniteDoseAppliquee.getUniteDeConversion().getType().equals(TypeDeConversion.MULTIPLICATION)) {
            if (volumeDeBouillie != null) {
                ift.setIft(
                        doseAppliquee.multiply(volumeDeBouillie).divide(doseReference, 10, RoundingMode.CEILING).multiply(new BigDecimal("0.01")).multiply(getDefaultIft(facteurDeCorrection))
                );
                return ift;
            } else {
                Avertissement avertissement = avertissementService.findAvertissementByIdMetier("AV4");
                ift.setAvertissement(avertissement);
                ift.setIft(getDefaultIft(facteurDeCorrection));
                return ift;
            }
        } else {
            Avertissement avertissement = avertissementService.findAvertissementByIdMetier("AV4");
            ift.setAvertissement(avertissement);
            ift.setIft(getDefaultIft(facteurDeCorrection));
            return ift;
        }
    }

    private BigDecimal getDefaultIft(BigDecimal facteurDeCorrection) {
        return facteurDeCorrection.divide(new BigDecimal("100"), 10, RoundingMode.CEILING);
    }

    private Segment getSegment(boolean biocontrole, Segment doseReferenceSegment, Traitement traitement) {
        if (biocontrole) {
            return segmentService.findSegmentByIdMetier("S2");
        }

        if (doseReferenceSegment != null) {
            return doseReferenceSegment;
        }

        Segment segment;
        switch (traitement.getIdMetier()) {
            case "T1":
                segment = segmentService.findSegmentByIdMetier("S1");
                break;
            case "T21":
                segment = segmentService.findSegmentByIdMetier("S3");
                break;
            case "T22":
                segment = segmentService.findSegmentByIdMetier("S4");
                break;
            case "T23":
                segment = segmentService.findSegmentByIdMetier("S5");
                break;
            case "T24":
                segment = segmentService.findSegmentByIdMetier("S6");
                break;
            default:
                segment = segmentService.findSegmentByIdMetier("S5");
                break;
        }
        return segment;
    }

    private Campagne getCampagne(String campagneIdMetier) {
        try {
            return campagneService.findCampagneByIdMetier(campagneIdMetier);
        } catch (NotFoundException ex) {
            throw new InvalidParameterException("La campagne ayant pour id métier " + campagneIdMetier + " n'existe pas.");
        }
    }

    private Culture getCulture(String cultureIdMetier) {
        try {
            return cultureService.findCultureByIdMetier(cultureIdMetier);
        } catch (NotFoundException ex) {
            throw new InvalidParameterException("La culture ayant pour id métier " + cultureIdMetier + " n'existe pas.");
        }
    }

    private Cible getCible(String cibleIdMetier) {
        try {
            return cibleService.findCibleByIdMetier(cibleIdMetier);
        } catch (NotFoundException ex) {
            throw new InvalidParameterException("La cible ayant pour id métier " + cibleIdMetier + " n'existe pas.");
        }
    }

    private Traitement getTraitement(String traitementIdMetier) {
        try {
            return traitementService.findTraitementByIdMetier(traitementIdMetier);
        } catch (NotFoundException ex) {
            throw new InvalidParameterException("Le type de traitement ayant pour id métier " + traitementIdMetier + " n'existe pas.");
        }
    }

    private Unite getUnite(String uniteIdMetier) {
        try {
            return uniteService.findUniteByIdMetier(uniteIdMetier);
        } catch (NotFoundException ex) {
            throw new InvalidParameterException("L'unite ayant pour id métier " + uniteIdMetier + " n'existe pas.");
        }
    }

    private NumeroAmm getNumeroAmm(String numeroAmmIdMetier) {
        try {
            return numeroAmmService.findNumeroAmmByIdMetier(numeroAmmIdMetier);
        } catch (NotFoundException ex) {
            throw new InvalidParameterException("Le numéro Amm ayant pour id métier " + numeroAmmIdMetier + " n'existe pas.");
        }
    }
}
