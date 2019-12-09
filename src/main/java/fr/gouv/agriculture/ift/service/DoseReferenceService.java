package fr.gouv.agriculture.ift.service;

import fr.gouv.agriculture.ift.controller.form.DoseReferenceForm;
import fr.gouv.agriculture.ift.model.*;
import fr.gouv.agriculture.ift.model.enumeration.TypeDoseReference;
import org.springframework.data.domain.Pageable;

import java.io.InputStream;
import java.util.List;
import java.util.UUID;

public interface DoseReferenceService {

    List<DoseReference> findAllDosesReference();
    List<DoseReference> findDosesReference(String campagneIdMetier, String cultureIdMetier, String[] numeroAmmIdMetier, String cibleIdMetier, TypeDoseReference typeDoseReference);
    List<DoseReference> findDosesReference(String campagneIdMetier, String cultureIdMetier, String[] numeroAmmIdMetier, String cibleIdMetier, TypeDoseReference typeDoseReference, Pageable pageable);
    String findDosesReferenceByCampagneAndGroupeCulturesAsCSV(String campagneIdMetier, String groupeCulturesIdMetier);

    DoseReference findDoseReferenceByCampagneAndCultureAndNumeroAmmAndCible(Campagne campagne, Culture culture, NumeroAmm numeroAmm, Cible cible);
    DoseReference findDoseReferenceById(UUID id);

    DoseReference save(DoseReferenceForm doseReferenceForm);
    DoseReference updateById(UUID id, DoseReferenceForm doseReferenceForm);
    void delete(UUID id);

    String addDosesReference(Campagne campagne, InputStream inputStream, TypeDoseReference typeDoseReference);
    void deleteDoseReferenceCible(Campagne campagne);
    void deleteDoseReferenceCulture(Campagne campagne);
	
    DoseReference findDoseReferenceByCampagneAndCultureAndNumeroAmm(Campagne campagne, Culture culture,
			NumeroAmm numeroAmm);
}
