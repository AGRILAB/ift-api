package fr.gouv.agriculture.ift.service;

import fr.gouv.agriculture.ift.controller.form.CibleForm;
import fr.gouv.agriculture.ift.model.Cible;
import org.springframework.data.domain.Pageable;

import java.io.InputStream;
import java.util.List;
import java.util.UUID;

public interface CibleService {

    List<Cible> findCibles(String campagneIdMetier, String cultureIdMetier, String numeroAmmIdMetier, String filtre, Pageable pageable);
    List<Cible> findAllCibles();
    List<Cible> findAllCibles(Pageable pageable);
    List<Cible> findAllCibles(String filtre, Pageable pageable);
    List<Cible> findCiblesByCampagneAndCultureAndOrNumeroAmm(String campagneIdMetier, String cultureIdMetier, String numeroAmmIdMetier, String filtre, Pageable pageable);
    Cible findCibleById(UUID cibleId);
    Cible findCibleById(UUID id, Class<? extends Throwable> throwableClass);
    Cible findCibleByIdMetier(String idMetier);
    Cible findCibleByIdMetier(String idMetier, Class<? extends Throwable> throwableClass);
    Cible save(CibleForm cibleForm);
    Cible updateById(UUID id, CibleForm cibleForm);

    void delete(UUID id);

    List<Cible> addCibles(InputStream inputStream);
}
