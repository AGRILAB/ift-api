package fr.gouv.agriculture.ift.service;

import fr.gouv.agriculture.ift.controller.form.CultureForm;
import fr.gouv.agriculture.ift.model.Culture;
import org.springframework.data.domain.Pageable;

import java.io.InputStream;
import java.util.List;
import java.util.UUID;

public interface CultureService {

    List<Culture> findCultures(String campagneIdMetier, String numeroAmmIdMetier, String cibleIdMetier, String filter, Pageable pageable);
    List<Culture> findAllCultures();
    List<Culture> findAllCultures(Pageable pageable);
    List<Culture> findAllCultures(String filter, Pageable pageable);
    List<Culture> findCulturesByCampagneAndNumeroAmmAndOrCible(String campagneIdMetier, String numeroAmmIdMetier, String cibleIdMetier, String filter, Pageable pageable);
    List<Culture> findCulturesByGroupeCultures(String groupeCulturesIdMetier);

    Culture findCultureById(UUID cibleId);
    Culture findCultureById(UUID id, Class<? extends Throwable> throwableClass);
    Culture findCultureByIdMetier(String idMetier);
    Culture findCultureByIdMetier(String idMetier, Class<? extends Throwable> throwableClass);

    Culture save(CultureForm cultureForm);
    Culture updateById(UUID id, CultureForm cultureForm);
    void delete(UUID id);

    List<Culture> addCultures(InputStream inputStream);
}
