package fr.gouv.agriculture.ift.service.impl;

import fr.gouv.agriculture.ift.controller.form.AvertissementForm;
import fr.gouv.agriculture.ift.exception.NotFoundException;
import fr.gouv.agriculture.ift.model.Avertissement;
import fr.gouv.agriculture.ift.repository.AvertissementRepository;
import fr.gouv.agriculture.ift.service.AvertissementService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class AvertissementServiceImpl implements AvertissementService {

    @Autowired
    private AvertissementRepository repository;

    @Override
    public Avertissement findAvertissementByIdMetier(String idMetier) {
        log.debug("Get Avertissement by IdMetier: {}", idMetier);
        Avertissement found = repository.findAvertissementByIdMetier(idMetier);

        if (found == null) {
            throw new NotFoundException();
        } else {
            return found;
        }
    }

    @Override
    public List<Avertissement> findAllAvertissements() {
        log.debug("Get All Avertissements");
        return repository.findAll(new Sort(Sort.Direction.ASC, "libelle"));
    }

    @Override
    public Avertissement updateById(UUID id, AvertissementForm avertissementForm) {
        Avertissement found = repository.findOne(id);

        if (found == null) {
            throw new NotFoundException();
        } else {
            Avertissement avertissement = AvertissementForm.mapToAvertissement(avertissementForm);
            avertissement.setId(id);
            avertissement.setIdMetier(found.getIdMetier());
            avertissement.setDateCreation(found.getDateCreation());
            avertissement.setDateDerniereMaj(LocalDateTime.now());
            log.debug("Update Avertissement: {}", avertissement);
            return repository.save(avertissement);
        }
    }

}
