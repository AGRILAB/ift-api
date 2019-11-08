package fr.gouv.agriculture.ift.service;

import fr.gouv.agriculture.ift.controller.form.TypeTraitementForm;
import fr.gouv.agriculture.ift.model.TypeTraitement;

import java.util.List;
import java.util.UUID;

public interface TypeTraitementService {

    List<TypeTraitement> findAllTypesTraitements();
    TypeTraitement findTypeTraitementById(UUID typeTraitementId);
    TypeTraitement findTypeTraitementByIdMetier(String idMetier);
    TypeTraitement updateById(UUID id, TypeTraitementForm typeTraitementForm);

}
