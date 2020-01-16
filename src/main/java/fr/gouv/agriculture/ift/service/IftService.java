package fr.gouv.agriculture.ift.service;

import fr.gouv.agriculture.ift.model.IftTraitement;

import java.math.BigDecimal;

public interface IftService {
    IftTraitement computeIftTraitement(String campagneIdMetier, String numeroAmmIdMetier, String cultureIdMetier, String cibleIdMetier, String typeTraitementIdMetier, String uniteIdMetier, BigDecimal dose, BigDecimal volumeDeBouillie, BigDecimal facteurDeCorrection);
}
