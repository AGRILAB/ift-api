package fr.gouv.agriculture.ift.service.impl;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import fr.gouv.agriculture.ift.controller.form.AvisForm;
import fr.gouv.agriculture.ift.exception.NotFoundException;
import fr.gouv.agriculture.ift.model.Avis;
import fr.gouv.agriculture.ift.repository.AvisRepository;
import fr.gouv.agriculture.ift.service.AvisService;
import lombok.extern.slf4j.Slf4j;

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
        return repository.findAll(new Sort(Sort.Direction.DESC, "dateCreation"));
    }

    @Override
    public List<Avis> findAvisByNote(Integer note) {
        log.debug("Get Avis by Note: {}", note);
        return repository.findAvisByNote(note);
    }
    

    @Override
    public void delete(UUID id) {
        log.debug("Delete Avis: {}", id);
        Avis found = repository.findOne(id);
        if (found == null) {
            throw new NotFoundException();
        } else {
            repository.delete(id);
        }
    }
}
