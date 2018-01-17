package fr.gouv.agriculture.ift.service;

import fr.gouv.agriculture.ift.controller.form.DoseReferenceForm;
import fr.gouv.agriculture.ift.model.Campagne;
import fr.gouv.agriculture.ift.model.DoseReference;
import fr.gouv.agriculture.ift.model.enumeration.TypeDoseReference;
import org.springframework.data.domain.Pageable;

import java.io.InputStream;
import java.util.List;
import java.util.UUID;

public interface DoseReferenceService {

    List<DoseReference> findAllDosesReference();
    List<DoseReference> findAllDosesReference(Pageable pageable);
    List<DoseReference> findDosesReference(String campagneIdMetier, String cultureIdMetier, String numeroAmmIdMetier, String cibleIdMetier, TypeDoseReference typeDoseReference);
    List<DoseReference> findDosesReference(String campagneIdMetier, String cultureIdMetier, String numeroAmmIdMetier, String cibleIdMetier, TypeDoseReference typeDoseReference, Pageable pageable);

    DoseReference findDoseReferenceByCampagneAndCultureAndNumeroAmmAndCible(String campagneIdMetier, String cultureIdMetier, String numeroAmmIdMetier, String cibleIdMetier);
    DoseReference findDoseReferenceById(UUID id);

    DoseReference save(DoseReferenceForm doseReferenceForm);
    DoseReference updateById(UUID id, DoseReferenceForm doseReferenceForm);
    void delete(UUID id);

    List<DoseReference> addDosesReferenceCible(Campagne campagne, InputStream inputStream);
    List<DoseReference> addDosesReferenceCulture(Campagne campagne, InputStream inputStream);
    void deleteDoseReferenceCible(Campagne campagne);
    void deleteDoseReferenceCulture(Campagne campagne);
}
