package fr.gouv.agriculture.ift.service;

import fr.gouv.agriculture.ift.controller.form.ProduitForm;
import fr.gouv.agriculture.ift.model.Campagne;
import fr.gouv.agriculture.ift.model.NumeroAmm;
import fr.gouv.agriculture.ift.model.Produit;
import org.springframework.data.domain.Pageable;

import java.io.InputStream;
import java.util.List;
import java.util.UUID;

public interface ProduitService {

    Produit findProduitById(UUID produitId);
    Produit findProduitByLibelle(String libelle,  Class<? extends Throwable> throwableClass);
    List<Produit> findProduits(String campagneIdMetier, String cultureIdMetier, String cibleIdMetier, String filter, Pageable pageable);
    List<Produit> findProduitsByCampagneAndOrCultureAndOrCible(String campagneIdMetier, String cultureIdMetier, String cibleIdMetier, String filtre, Pageable pageable);
    String findProduitsByCampagneAsCSV(String campagneIdMetier);
    List<NumeroAmm> getNumeroAmmByProduitAndCampagne(String produitLibelle, String campagneIdMetier);

    Produit save(ProduitForm produitForm);
    Produit updateById(UUID id, ProduitForm produitForm);
    void delete(UUID id);
    void deleteValiditeProduitByCampagne(Campagne campagne);

    String addProduits(Campagne campagne, InputStream inputStream);
}
