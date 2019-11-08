package fr.gouv.agriculture.ift.service;

import fr.gouv.agriculture.ift.controller.form.AvisForm;
import fr.gouv.agriculture.ift.model.Avis;

import java.util.List;
import java.util.UUID;

public interface AvisService {

    Avis save(AvisForm avisForm);
    List<Avis> findAllAvis();
    List<Avis> findAvisByNote(Integer note);
	void delete(UUID avisIdToDelete);

}
