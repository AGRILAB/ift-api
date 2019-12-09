package fr.gouv.agriculture.ift.service.impl;

import fr.gouv.agriculture.ift.exception.NotFoundException;
import fr.gouv.agriculture.ift.model.ProduitDoseReference;
import fr.gouv.agriculture.ift.model.enumeration.TypeDoseReference;
import fr.gouv.agriculture.ift.repository.DoseReferencePredicateBuilder;
import fr.gouv.agriculture.ift.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class ProduitDoseReferenceServiceImpl implements ProduitDoseReferenceService {

    @Autowired
    EntityManager entityManager;

    @Override
    public List<ProduitDoseReference> findProduitsDosesReferenceByCampagneAndCultureAndProduitAndNumeroAmmAndCible(
            String campagneIdMetier, String cultureIdMetier, String produitLibelle, String[] numeroAmmIdMetier, String cibleIdMetier, TypeDoseReference typeDoseReference, Boolean biocontrole, Pageable pageable) {

        List<ProduitDoseReference> doseReferences = new ArrayList<>();

        //Dose de référence à la cible en premier
        if (!TypeDoseReference.culture.equals(typeDoseReference)) {
            doseReferences.addAll(queryProduitsDosesReference(campagneIdMetier, cultureIdMetier, produitLibelle, numeroAmmIdMetier, cibleIdMetier, TypeDoseReference.cible, biocontrole, pageable));
        }

        if ((pageable == null || pageable.getPageSize() > doseReferences.size()) && !TypeDoseReference.cible.equals(typeDoseReference)) {
            doseReferences.addAll(queryProduitsDosesReference(campagneIdMetier, cultureIdMetier, produitLibelle, numeroAmmIdMetier, cibleIdMetier, TypeDoseReference.culture, biocontrole, pageable));
        }

        if (doseReferences.size() == 0){
            throw new NotFoundException();
        }
        return doseReferences;
    }

    private List<ProduitDoseReference> queryProduitsDosesReference(
            String campagneIdMetier, String cultureIdMetier, String produitLibelle, String[] numeroAmmIdMetier, String cibleIdMetier, TypeDoseReference typeDoseReference, Boolean biocontrole, Pageable pageable) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<ProduitDoseReference> query = builder.createQuery(ProduitDoseReference.class);
        Root<ProduitDoseReference> root = query.from(ProduitDoseReference.class);

        DoseReferencePredicateBuilder predicateBuilder = new DoseReferencePredicateBuilder(builder, root);
        Predicate predicate = predicateBuilder.appendPredicate(builder.conjunction(), campagneIdMetier, numeroAmmIdMetier, cultureIdMetier, cibleIdMetier, produitLibelle, typeDoseReference, biocontrole);
        query.where(predicate);

        List<Order> orderBy = new ArrayList<>();
        orderBy.add(builder.asc(root.get("produit").get("libelle")));
        orderBy.add(builder.asc(root.get("culture").get("libelle")));

        if (TypeDoseReference.cible.equals(typeDoseReference)){
            orderBy.add(builder.asc(root.get("cible").get("libelle")));
        }
        query.orderBy(orderBy);

        TypedQuery<ProduitDoseReference> typedQuery = entityManager.createQuery(query);
        if (pageable != null){
            typedQuery = typedQuery.setFirstResult(pageable.getOffset())
                    .setMaxResults(pageable.getPageSize());
        }


        return typedQuery.getResultList();
    }
}
