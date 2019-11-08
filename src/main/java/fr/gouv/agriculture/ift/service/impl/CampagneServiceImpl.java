package fr.gouv.agriculture.ift.service.impl;

import fr.gouv.agriculture.ift.controller.form.CampagneForm;
import fr.gouv.agriculture.ift.exception.ConflictException;
import fr.gouv.agriculture.ift.exception.InvalidParameterException;
import fr.gouv.agriculture.ift.exception.NotFoundException;
import fr.gouv.agriculture.ift.model.Campagne;
import fr.gouv.agriculture.ift.repository.CampagneRepository;
import fr.gouv.agriculture.ift.service.CampagneService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class CampagneServiceImpl implements CampagneService {

    @Autowired
    private CampagneRepository repository;

    @Override
    public Campagne save(CampagneForm campagneForm) {
        Campagne newCampagne = CampagneForm.mapToCampagne(campagneForm);
        Campagne found = repository.findCampagneByIdMetier(newCampagne.getIdMetier());

        if (found == null) {
            newCampagne.setId(UUID.randomUUID());
            log.debug("Create Campagne: {}", newCampagne);
        } else {
            throw newConflictException(newCampagne);
        }

        if (newCampagne.getActive()) {
            Campagne previousActiveCampagne = getCurrentCampagne();
            if (previousActiveCampagne != null) {
                previousActiveCampagne.setActive(false);
                repository.save(previousActiveCampagne);
            }
        }

        return repository.save(newCampagne);
    }

    @Override
    public Campagne getCurrentCampagne() {
        return repository.findFirstByActive(true);
    }

    @Override
    public List<Campagne> findAllCampagnes() {
        log.debug("Get All Campagnes");
        return repository.findAll(new Sort(Sort.Direction.ASC, "idMetier"));
    }

    @Override
    public Campagne findCampagneById(UUID id) {
        return findCampagneById(id, null);
    }

    @Override
    public Campagne findCampagneById(UUID id, Class<? extends Throwable> throwableClass) {
        log.debug("Get Campagne by Id: {}", id.toString());
        Campagne found = repository.findOne(id);

        if (found == null) {
            if (InvalidParameterException.class.equals(throwableClass)){
                throw new InvalidParameterException("La campagne ayant pour id " + id + " n'existe pas.");
            }
            throw new NotFoundException();
        } else {
            return found;
        }
    }

    @Override
    public Campagne findCampagneByIdMetier(String idMetier) {
        log.debug("Get Campagne by IdMetier: {}", idMetier);
        Campagne found = repository.findCampagneByIdMetier(idMetier);

        if (found == null) {
            throw new NotFoundException();
        } else {
            return found;
        }
    }

    @Override
    public Campagne updateById(UUID id, CampagneForm campagneForm) {
        Campagne found = repository.findOne(id);

        if (found == null) {
            throw new NotFoundException();
        } else {
            if (found.getActive() && !campagneForm.getActive()) {
                throw new InvalidParameterException("Il faut obligatoirement une campagne active.");
            }

            Campagne campagne = CampagneForm.mapToCampagne(campagneForm);
            campagne.setId(id);
            campagne.setDateCreation(found.getDateCreation());
            campagne.setDateDerniereMaj(LocalDateTime.now());
            log.debug("Update Campagne: {}", campagne);

            if (campagne.getActive()) {
                Campagne previousActiveCampagne = getCurrentCampagne();
                if (previousActiveCampagne != null) {
                    previousActiveCampagne.setActive(false);
                    repository.save(previousActiveCampagne);
                }
            }

            try{
                return repository.save(campagne);
            } catch (DataIntegrityViolationException e) {
                throw newConflictException(campagne);
            }
        }
    }

    @Override
    public void delete(UUID id) {
        log.debug("Delete Campagne: {}", id);
        Campagne found = repository.findOne(id);
        if (found == null) {
            throw new NotFoundException();
        } else {
            repository.delete(id);
            Campagne active = getCurrentCampagne();
            if (active == null) {
                Campagne last = repository.findFirstByOrderByDateCreationDesc();
                last.setActive(true);
                repository.save(last);
            }
        }
    }

    private ConflictException newConflictException(Campagne campagne) {
        return new ConflictException("La campagne avec l'identifiant " + campagne.getIdMetier() + " existe déjà.");
    }
}
