package fr.gouv.agriculture.ift.service;

import fr.gouv.agriculture.ift.controller.form.UniteForm;
import fr.gouv.agriculture.ift.model.Unite;

import java.util.List;
import java.util.UUID;

public interface UniteService {

    Unite save(UniteForm uniteForm);
    List<Unite> findAllUnites();
    Unite findUniteById(UUID uniteId);
    Unite findUniteById(UUID uniteId, Class<? extends Throwable> throwableClass);
    Unite findUniteByIdMetier(String idMetier);
    Unite findUniteByIdMetier(String idMetier, Class<? extends Throwable> throwableClass);
    Unite findUniteByIdMetierWithCache(String idMetier, Class<? extends Throwable> throwableClass);
    void cleanCache();
    Unite updateById(UUID id, UniteForm uniteForm);
    void delete(UUID id);

}
