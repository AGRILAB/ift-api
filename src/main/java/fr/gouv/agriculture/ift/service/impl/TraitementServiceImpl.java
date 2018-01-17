package fr.gouv.agriculture.ift.service.impl;

import fr.gouv.agriculture.ift.controller.form.TraitementForm;
import fr.gouv.agriculture.ift.exception.NotFoundException;
import fr.gouv.agriculture.ift.model.Traitement;
import fr.gouv.agriculture.ift.repository.TraitementRepository;
import fr.gouv.agriculture.ift.service.TraitementService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@CacheConfig(cacheNames = "traitement")
public class TraitementServiceImpl implements TraitementService {

    @Autowired
    private TraitementRepository repository;

    @Override
    @Cacheable(key = "#root.methodName")
    public List<Traitement> findAllTraitements() {
        log.debug("Get All Traitements");
        return repository.findAll();
    }

    @Override
    @Cacheable(key = "#root.methodName + '_' + #id")
    public Traitement findTraitementById(UUID id) {
        log.debug("Get Traitement by Id: {}", id.toString());
        Traitement found = repository.findOne(id);

        if (found == null) {
            throw new NotFoundException();
        } else {
            return found;
        }
    }

    @Override
    @Cacheable(key = "#root.methodName + '_' + #idMetier")
    public Traitement findTraitementByIdMetier(String idMetier) {
        log.debug("Get Traitement by IdMetier: {}", idMetier);
        Traitement found = repository.findTraitementByIdMetier(idMetier);

        if (found == null) {
            throw new NotFoundException();
        } else {
            return found;
        }
    }

    @Override
    @CacheEvict(allEntries = true)
    public Traitement save(TraitementForm traitementForm) {
        Traitement newTraitement = TraitementForm.mapToTraitement(traitementForm);
        Traitement found = repository.findTraitementByIdMetier(newTraitement.getIdMetier());

        if (found == null) {
            newTraitement.setId(UUID.randomUUID());
            log.debug("Create Traitement: {}", newTraitement);
        } else {
            newTraitement.setId(found.getId());
            newTraitement.setDateCreation(found.getDateCreation());
            newTraitement.setDateDerniereMaj(LocalDateTime.now());
            log.debug("Update Traitement: {}", newTraitement);
        }

        return repository.save(newTraitement);
    }

    @Override
    @CacheEvict(allEntries = true)
    public Traitement updateById(UUID id, TraitementForm traitementForm) {
        Traitement found = repository.findOne(id);

        if (found == null) {
            throw new NotFoundException();
        } else {
            Traitement traitement = TraitementForm.mapToTraitement(traitementForm);
            traitement.setId(id);
            traitement.setDateCreation(found.getDateCreation());
            traitement.setDateDerniereMaj(LocalDateTime.now());
            log.debug("Update Traitement: {}", traitement);
            return repository.save(traitement);
        }
    }

    @Override
    @CacheEvict(allEntries = true)
    public void delete(UUID id) {
        log.debug("Delete Traitement: {}", id);
        Traitement found = repository.findOne(id);
        if (found == null) {
            throw new NotFoundException();
        } else {
            repository.delete(id);
        }
    }

}
