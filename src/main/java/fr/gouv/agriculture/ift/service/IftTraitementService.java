package fr.gouv.agriculture.ift.service;

import fr.gouv.agriculture.ift.model.IftTraitement;
import fr.gouv.agriculture.ift.model.IftTraitementSigne;
import fr.gouv.agriculture.ift.model.Signature;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.UUID;

public interface IftTraitementService {

    IftTraitement findIftTraitement(UUID id) throws IOException;

    IftTraitementSigne getIftTraitementSigne(String campagneIdMetier,
                                             String numeroAmmIdMetier,
                                             String cultureIdMetier,
                                             String cibleIdMetier,
                                             String traitementIdMetier,
                                             String uniteIdMetier,
                                             BigDecimal dose,
                                             BigDecimal volumeDeBouillie,
                                             BigDecimal facteurDeCorrection,
                                             String produitLibelle,
                                             String commentaire);

    Signature signeIftTraitement(IftTraitement ift);

    IftTraitement getIftTraitementFromSignature(Signature signature) throws IOException;

    void getIftTraitementSignePDF(OutputStream out, String titre, IftTraitementSigne iftTraitementSigne);

}
