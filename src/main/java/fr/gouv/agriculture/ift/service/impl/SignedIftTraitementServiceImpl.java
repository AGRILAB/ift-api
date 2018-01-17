package fr.gouv.agriculture.ift.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.gouv.agriculture.ift.exception.NotFoundException;
import fr.gouv.agriculture.ift.exception.ServerException;
import fr.gouv.agriculture.ift.model.ClePublique;
import fr.gouv.agriculture.ift.model.IftTraitement;
import fr.gouv.agriculture.ift.model.SignedIftTraitement;
import fr.gouv.agriculture.ift.repository.SignedIftTraitementRepository;
import fr.gouv.agriculture.ift.service.CertificationService;
import fr.gouv.agriculture.ift.service.ClePubliqueService;
import fr.gouv.agriculture.ift.service.IftService;
import fr.gouv.agriculture.ift.service.SignedIftTraitementService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.security.GeneralSecurityException;
import java.util.UUID;

@Slf4j
@Service
public class SignedIftTraitementServiceImpl implements SignedIftTraitementService {

    @Autowired
    private SignedIftTraitementRepository repository;

    @Autowired
    private IftService iftService;

    @Autowired
    private CertificationService certificationService;

    @Autowired
    private ClePubliqueService clePubliqueService;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public SignedIftTraitement getSignedIftTraitement(String campagneIdMetier, String numeroAmmIdMetier, String cultureIdMetier, String cibleIdMetier, String traitementIdMetier, String uniteIdMetier, BigDecimal dose, BigDecimal volumeDeBouillie, BigDecimal facteurDeCorrection) {
        IftTraitement ift = iftService.computeIftTraitement(campagneIdMetier, numeroAmmIdMetier, cultureIdMetier, cibleIdMetier, traitementIdMetier, uniteIdMetier, dose, volumeDeBouillie, facteurDeCorrection);
        ift.setIft(ift.getIft().stripTrailingZeros());

        try {
            String serializedIft = objectMapper.writeValueAsString(ift);
            String hashedIft = certificationService.hash(serializedIft);
            String signedIft = certificationService.sign(hashedIft);

            ClePublique clePublique;
            try {
                clePublique = clePubliqueService.findClePubliqueByCle(certificationService.getClePublique());
            } catch (NotFoundException ex) {
                clePublique = ClePublique.builder()
                        .cle(certificationService.getClePublique())
                        .build();
                clePublique = clePubliqueService.save(clePublique);
            }

            SignedIftTraitement signedIftTraitement = SignedIftTraitement.builder()
                    .id(UUID.randomUUID())
                    .iftTraitement(ift)
                    .signature(signedIft)
                    .iftTraitementJson(serializedIft)
                    .clePublique(clePublique)
                    .build();

            return save(signedIftTraitement);
        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
            throw new ServerException("Erreur lors de la signature de l'IFT");
        }
    }

    @Override
    public SignedIftTraitement save(SignedIftTraitement signedIftTraitement) throws IOException {
        SignedIftTraitement savedSignedIftTraitement = repository.save(signedIftTraitement);
        savedSignedIftTraitement.setIftTraitement(objectMapper.readValue(savedSignedIftTraitement.getIftTraitementJson(), IftTraitement.class));
        return savedSignedIftTraitement;
    }

    @Override
    public SignedIftTraitement findSignedIftTraitementBySignature(String signature) throws IOException {
        SignedIftTraitement signedIftTraitement = repository.findFirstSignedIftTraitementBySignature(signature);

        if (signedIftTraitement == null) {
            throw new NotFoundException();
        } else {
            signedIftTraitement.setIftTraitement(objectMapper.readValue(signedIftTraitement.getIftTraitementJson(), IftTraitement.class));
            return signedIftTraitement;
        }
    }
}
