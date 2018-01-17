package fr.gouv.agriculture.ift.service;

import fr.gouv.agriculture.ift.controller.form.TraitementForm;
import fr.gouv.agriculture.ift.model.Traitement;

import java.util.List;
import java.util.UUID;

public interface TraitementService {

    Traitement save(TraitementForm traitementForm);
    List<Traitement> findAllTraitements();
    Traitement findTraitementById(UUID traitementId);
    Traitement findTraitementByIdMetier(String idMetier);
    Traitement updateById(UUID id, TraitementForm traitementForm);
    void delete(UUID id);

}
