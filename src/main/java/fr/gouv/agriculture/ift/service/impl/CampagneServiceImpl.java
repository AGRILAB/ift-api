package fr.gouv.agriculture.ift.service.impl;

import fr.gouv.agriculture.ift.controller.form.CampagneForm;
import fr.gouv.agriculture.ift.exception.InvalidParameterException;
import fr.gouv.agriculture.ift.exception.NotFoundException;
import fr.gouv.agriculture.ift.model.Campagne;
import fr.gouv.agriculture.ift.repository.CampagneRepository;
import fr.gouv.agriculture.ift.service.CampagneService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@CacheConfig(cacheNames = "campagne")
public class CampagneServiceImpl implements CampagneService {

    @Autowired
    private CampagneRepository repository;

    @Override
    @CacheEvict(allEntries = true)
    public Campagne save(CampagneForm campagneForm) {
        Campagne newCampagne = CampagneForm.mapToCampagne(campagneForm);
        Campagne found = repository.findCampagneByIdMetier(newCampagne.getIdMetier());

        if (found == null) {
            newCampagne.setId(UUID.randomUUID());
            log.debug("Create Campagne: {}", newCampagne);
        } else {
            newCampagne.setId(found.getId());
            newCampagne.setDateCreation(found.getDateCreation());
            newCampagne.setDateDerniereMaj(LocalDateTime.now());
            log.debug("Update Campagne: {}", newCampagne);
        }

        if (newCampagne.getActive() == true) {
            Campagne previousActiveCampagne = getCurrentCampagne();
            if (previousActiveCampagne != null) {
                previousActiveCampagne.setActive(false);
                repository.save(previousActiveCampagne);
            }
        }

        return repository.save(newCampagne);
    }

    @Override
    @Cacheable(key = "#root.methodName")
    public Campagne getCurrentCampagne() {
        return repository.findFirstByActive(true);
    }

    @Override
    @Cacheable(key = "#root.methodName")
    public List<Campagne> findAllCampagnes() {
        log.debug("Get All Campagnes");
        return repository.findAll(new Sort(Sort.Direction.ASC, "idMetier"));
    }

    @Override
    @Cacheable(key = "#root.methodName + '_' + #id")
    public Campagne findCampagneById(UUID id) {
        return findCampagneById(id, null);
    }

    @Override
    @Cacheable(key = "#root.methodName + '_' + #id")
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
    @Cacheable(key = "#root.methodName + '_' + #idMetier")
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
    @CacheEvict(allEntries = true)
    public Campagne updateById(UUID id, CampagneForm campagneForm) {
        Campagne found = repository.findOne(id);

        if (found == null) {
            throw new NotFoundException();
        } else {
            if (found.getActive() == true && campagneForm.getActive() == false) {
                throw new InvalidParameterException("Il faut obligatoirement une campagne active.");
            }

            Campagne campagne = CampagneForm.mapToCampagne(campagneForm);
            campagne.setId(id);
            campagne.setDateCreation(found.getDateCreation());
            campagne.setDateDerniereMaj(LocalDateTime.now());
            log.debug("Update Campagne: {}", campagne);

            if (campagne.getActive() == true) {
                Campagne previousActiveCampagne = getCurrentCampagne();
                if (previousActiveCampagne != null) {
                    previousActiveCampagne.setActive(false);
                    repository.save(previousActiveCampagne);
                }
            }

            return repository.save(campagne);
        }
    }

    @Override
    @CacheEvict(allEntries = true)
    public void delete(UUID id) {
        log.debug("Delete Campagne: {}", id);
        Campagne found = repository.findOne(id);
        if (found == null) {
            throw new NotFoundException();
        } else {
            repository.delete(id);
        }
    }
}
