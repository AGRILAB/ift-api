package fr.gouv.agriculture.ift.service;

import fr.gouv.agriculture.ift.controller.form.CampagneForm;
import fr.gouv.agriculture.ift.model.Campagne;

import java.util.List;
import java.util.UUID;

public interface CampagneService {

    Campagne save(CampagneForm campagneForm);
    Campagne getCurrentCampagne();
    List<Campagne> findAllCampagnes();
    Campagne findCampagneById(UUID campagneId);
    Campagne findCampagneById(UUID campagneId, Class<? extends Throwable> throwableClass);
    Campagne findCampagneByIdMetier(String idMetier);
    Campagne updateById(UUID id, CampagneForm campagneForm);
    void delete(UUID id);

}
