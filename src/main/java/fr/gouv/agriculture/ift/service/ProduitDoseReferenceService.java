package fr.gouv.agriculture.ift.service;

import fr.gouv.agriculture.ift.model.ProduitDoseReference;
import fr.gouv.agriculture.ift.model.enumeration.TypeDoseReference;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ProduitDoseReferenceService {

    List<ProduitDoseReference> findProduitsDosesReferenceByCampagneAndCultureAndProduitAndNumeroAmmAndCible(
            String campagneIdMetier, String cultureIdMetier, String produitLibelle, String[] numeroAmmIdMetier, String cibleIdMetier, TypeDoseReference typeDoseReference, Boolean biocontrole, Pageable pageable);

}
