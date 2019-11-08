package fr.gouv.agriculture.ift.service;

import fr.gouv.agriculture.ift.controller.form.AvertissementForm;
import fr.gouv.agriculture.ift.model.Avertissement;

import java.util.List;
import java.util.UUID;

public interface AvertissementService {

    Avertissement findAvertissementByIdMetier(String idMetier);
    List<Avertissement> findAllAvertissements();
    Avertissement updateById(UUID id, AvertissementForm avertissementForm);
}
