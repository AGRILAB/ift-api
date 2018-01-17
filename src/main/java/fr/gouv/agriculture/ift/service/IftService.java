package fr.gouv.agriculture.ift.service;

import fr.gouv.agriculture.ift.model.IftTraitement;

import java.math.BigDecimal;
import java.util.UUID;

public interface IftService {
    IftTraitement computeIftTraitement(String campagneIdMetier, String numeroAmmIdMetier, String cultureIdMetier, String cibleIdMetier, String traitementIdMetier, String uniteIdMetier, BigDecimal dose, BigDecimal volumeDeBouillie, BigDecimal facteurDeCorrection);
}
