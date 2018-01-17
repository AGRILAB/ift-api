package fr.gouv.agriculture.ift.service;

import fr.gouv.agriculture.ift.model.SignedIftTraitement;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.UUID;

public interface SignedIftTraitementService {
    SignedIftTraitement getSignedIftTraitement(String campagneIdMetier, String numeroAmmIdMetier, String cultureIdMetier, String cibleIdMetier, String traitementIdMetier, String uniteIdMetier, BigDecimal dose, BigDecimal volumeDeBouillie, BigDecimal facteurDeCorrection);
    SignedIftTraitement save(SignedIftTraitement signedIftTraitement) throws IOException;
    SignedIftTraitement findSignedIftTraitementBySignature(String signature) throws IOException;
}
