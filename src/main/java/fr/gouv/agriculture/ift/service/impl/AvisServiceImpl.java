package fr.gouv.agriculture.ift.service.impl;

import fr.gouv.agriculture.ift.controller.form.AvisForm;
import fr.gouv.agriculture.ift.model.Avis;
import fr.gouv.agriculture.ift.repository.AvisRepository;
import fr.gouv.agriculture.ift.service.AvisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class AvisServiceImpl implements AvisService {

    @Autowired
    private AvisRepository repository;

    @Override
    public Avis save(AvisForm avisForm) {
        Avis avis = AvisForm.mapToAvis(avisForm);

        avis.setId(UUID.randomUUID());
        log.debug("Create Avis: {}", avis);

        return repository.save(avis);
    }

    @Override
    public List<Avis> findAllAvis() {
        log.debug("Get All Avis");
        return repository.findAll();
    }

    @Override
    public List<Avis> findAvisByNote(Integer note) {
        log.debug("Get Avis by Note: {}", note);
        return repository.findAvisByNote(note);
    }
}
