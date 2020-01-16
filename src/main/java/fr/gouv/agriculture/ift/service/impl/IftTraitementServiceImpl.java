package fr.gouv.agriculture.ift.service.impl;

import fr.gouv.agriculture.ift.exception.NotFoundException;
import fr.gouv.agriculture.ift.exception.ServerException;
import fr.gouv.agriculture.ift.model.Certificat;
import fr.gouv.agriculture.ift.model.IftTraitement;
import fr.gouv.agriculture.ift.model.IftTraitementSigne;
import fr.gouv.agriculture.ift.model.Signature;
import fr.gouv.agriculture.ift.repository.SignatureRepository;
import fr.gouv.agriculture.ift.service.*;
import fr.gouv.agriculture.ift.util.DateUtils;
import fr.gouv.agriculture.ift.util.pdf.CustomFormatter;
import fr.gouv.agriculture.ift.util.pdf.PDFGeneratorUtil;
import fr.gouv.agriculture.ift.util.pdf.QrCodeGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.GeneralSecurityException;
import java.util.UUID;

import static fr.gouv.agriculture.ift.Constants.CONF_FRONT_END_URL;
import static fr.gouv.agriculture.ift.Constants.CONF_FRONT_END_URL_VERIFIER_IFT;

@Slf4j
@Service
public class IftTraitementServiceImpl implements IftTraitementService {

    @Autowired
    private SignatureRepository signatureRepository;

    @Autowired
    private IftService iftService;

    @Autowired
    private CertificationService certificationService;

    @Autowired
    @Qualifier("signatureCertificate")
    private Certificat certificat;

    @Autowired
    private CertificatService certificatService;

    @Autowired
    private PDFGeneratorUtil pdfGeneratorUtil;

    @Autowired
    private ConfigurationService configurationService;

    @Override
    public IftTraitement findIftTraitement(UUID id) throws IOException {
        Signature signature = signatureRepository.findOne(id);

        if (signature == null) {
            throw new NotFoundException();
        } else {
            return getIftTraitementFromSignature(signature);
        }
    }

    @Override
    public IftTraitementSigne getIftTraitementSigne(String campagneIdMetier,
                                                    String numeroAmmIdMetier,
                                                    String cultureIdMetier,
                                                    String cibleIdMetier,
                                                    String traitementIdMetier,
                                                    String uniteIdMetier,
                                                    BigDecimal dose,
                                                    BigDecimal volumeDeBouillie,
                                                    BigDecimal facteurDeCorrection,
                                                    String produitLibelle,
                                                    String commentaire) {

        IftTraitement ift = iftService.computeIftTraitement(campagneIdMetier, numeroAmmIdMetier, cultureIdMetier, cibleIdMetier, traitementIdMetier, uniteIdMetier, dose, volumeDeBouillie, facteurDeCorrection);

        ift.setIft(ift.getIft().stripTrailingZeros());

        if (!ift.getTypeTraitement().getAvantSemis()) {
            ift.setProduitLibelle(produitLibelle);
        }

        ift.setCommentaire(commentaire);

        Signature signature = signeIftTraitement(ift);

        IftTraitementSigne iftSigne = IftTraitementSigne.builder()
                .iftTraitement(ift)
                .signature(signature)
                .build();

        return iftSigne;
    }

    @Override
    public Signature signeIftTraitement(IftTraitement ift){
        String signedIft = null;
        try {

            signedIft = certificationService.sign(ift);

            Certificat certificat;
            try {
                certificat = certificatService.findCertificatByCert(this.certificat.getCert());
            } catch (NotFoundException ex) {
                certificat = certificatService.save(this.certificat);
            }
            Signature signature = Signature.builder()
                    .id(UUID.randomUUID())
                    .signature(signedIft)
                    .certificat(certificat)
                    .build();

            return signatureRepository.save(signature);
        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
            throw new ServerException("Erreur lors de la signature de l'IFT");
        }
    }

    @Override
    public IftTraitement getIftTraitementFromSignature(Signature signature) throws IOException {
        return this.certificationService.verify(signature);
    }

    @Override
    public void getIftTraitementSignePDF(OutputStream out, String titre, IftTraitementSigne iftTraitementSigne) {
        Context ctx = new Context();

        IftTraitement iftTraitement = iftTraitementSigne.getIftTraitement();
        ctx.setVariable("iftTraitement", iftTraitement);
        ctx.setVariable("customFormatter", new CustomFormatter());
        ctx.setVariable("ift", iftTraitement.getIft().setScale(2, RoundingMode.HALF_EVEN));
        ctx.setVariable("dateCreation", DateUtils.parseLocalDateTime(iftTraitement.getDateCreation()));
        ctx.setVariable("editeParUrl", configurationService.getValue(CONF_FRONT_END_URL));
        ctx.setVariable("titre", titre);

        try {
            String url = configurationService.getValue(CONF_FRONT_END_URL_VERIFIER_IFT) + "/" + iftTraitementSigne.getSignature().getId();
            byte[] qrCode = QrCodeGenerator.generateQrCode(url);

            ctx.setVariable("qrCodeUrl", url);
            pdfGeneratorUtil.createPdf(out, ctx, "iftTraitement", qrCode);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
