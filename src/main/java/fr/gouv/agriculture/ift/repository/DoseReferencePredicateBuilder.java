package fr.gouv.agriculture.ift.repository;

import fr.gouv.agriculture.ift.model.enumeration.TypeDoseReference;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

public class DoseReferencePredicateBuilder {

    CriteriaBuilder criteriaBuilder;
    Root root;

    public DoseReferencePredicateBuilder(CriteriaBuilder criteriaBuilder, Root root) {
        this.criteriaBuilder = criteriaBuilder;
        this.root = root;
    }

    public Predicate appendPredicate(Predicate predicate, String campagneIdMetier, String numeroAmmIdMetier, String cultureIdMetier, String cibleIdMetier){
        Predicate result = predicate;
        result = appendStringPredicate(result, root.get("numeroAmm").get("idMetier"), numeroAmmIdMetier);
        result = appendStringPredicate(result, root.get("cible").get("idMetier"), cibleIdMetier);
        result = appendStringPredicate(result, root.get("culture").get("idMetier"), cultureIdMetier);
        result = appendStringPredicate(result, root.get("campagne").get("idMetier"), campagneIdMetier);

        return result;
    }

    public Predicate appendPredicate(Predicate predicate, String campagneIdMetier, String numeroAmmIdMetier, String cultureIdMetier, String cibleIdMetier, TypeDoseReference typeDoseReference, Boolean biocontrole){

        Predicate result = predicate;
        result = appendPredicate(result, campagneIdMetier, numeroAmmIdMetier, cultureIdMetier, cibleIdMetier);
        result = appendTypeDoseReferencePredicate(result, typeDoseReference);
        result = appendBiocontrolePredicate(result, biocontrole);

        return result;
    }

    public Predicate appendPredicate(Predicate predicate, String campagneIdMetier, String numeroAmmIdMetier, String cultureIdMetier, String cibleIdMetier, String produitLibelle, TypeDoseReference typeDoseReference, Boolean biocontrole) {
        Predicate result = predicate;
        result = appendStringPredicate(result, root.get("produit").get("libelle"), produitLibelle);
        result = appendPredicate(result, campagneIdMetier, numeroAmmIdMetier, cultureIdMetier, cibleIdMetier, typeDoseReference, biocontrole);

        return result;
    }

    public Predicate appendStringPredicate(Predicate predicate, Path path, String criteria){

        Predicate result = predicate;
        if (criteria != null){
            Predicate idMetierPredicate = criteriaBuilder.equal(path, criteria);
            result = criteriaBuilder.and(result, criteriaBuilder.and(idMetierPredicate));
        }

        return result;
    }

    public Predicate appendTypeDoseReferencePredicate(Predicate predicate, TypeDoseReference typeDoseReference){

        Predicate result = predicate;
        Predicate ciblePredicate = null;
        if (TypeDoseReference.culture.equals(typeDoseReference)){
            ciblePredicate = criteriaBuilder.isNull(root.get("cible"));
        }else if (TypeDoseReference.cible.equals(typeDoseReference))
        {
            ciblePredicate = criteriaBuilder.isNotNull(root.get("cible"));
        }

        if (ciblePredicate != null){
            result = criteriaBuilder.and(result, criteriaBuilder.and(ciblePredicate));
        }

        return result;
    }

    public Predicate appendBiocontrolePredicate(Predicate predicate, Boolean biocontrole) {
        if (biocontrole != null) {
            predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("biocontrole"), biocontrole));
        }
        return predicate;
    }


}