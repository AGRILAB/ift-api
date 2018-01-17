package fr.gouv.agriculture.ift.service.impl;

import fr.gouv.agriculture.ift.controller.form.AvertissementForm;
import fr.gouv.agriculture.ift.exception.NotFoundException;
import fr.gouv.agriculture.ift.model.Avertissement;
import fr.gouv.agriculture.ift.repository.AvertissementRepository;
import fr.gouv.agriculture.ift.service.AvertissementService;
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
@CacheConfig(cacheNames = "avertissement")
public class AvertissementServiceImpl implements AvertissementService {

    @Autowired
    private AvertissementRepository repository;

    @Override
    @Cacheable(key = "#root.methodName + '_' + #idMetier")
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
    @CacheEvict(allEntries = true)
    public Avertissement save(AvertissementForm avertissementForm) {
        Avertissement newAvertissement = AvertissementForm.mapToAvertissement(avertissementForm);
        Avertissement found = repository.findAvertissementByIdMetier(newAvertissement.getIdMetier());

        if (found == null) {
            newAvertissement.setId(UUID.randomUUID());
            log.debug("Create GroupeCultures: {}", newAvertissement);
        } else {
            newAvertissement.setId(found.getId());
            newAvertissement.setDateCreation(found.getDateCreation());
            newAvertissement.setDateDerniereMaj(LocalDateTime.now());
            log.debug("Update GroupeCultures: {}", newAvertissement);
        }

        return repository.save(newAvertissement);
    }

    @Override
    @Cacheable(key = "#root.methodName")
    public List<Avertissement> findAllAvertissements() {
        log.debug("Get All Avertissements");
        return repository.findAll();
    }

    @Override
    @CacheEvict(allEntries = true)
    public Avertissement updateById(UUID id, AvertissementForm avertissementForm) {
        Avertissement found = repository.findOne(id);

        if (found == null) {
            throw new NotFoundException();
        } else {
            Avertissement avertissement = AvertissementForm.mapToAvertissement(avertissementForm);
            avertissement.setId(id);
            avertissement.setDateCreation(found.getDateCreation());
            avertissement.setDateDerniereMaj(LocalDateTime.now());
            log.debug("Update Avertissement: {}", avertissement);
            return repository.save(avertissement);
        }
    }

    @Override
    @CacheEvict(allEntries = true)
    public void delete(UUID id) {
        log.debug("Delete Avertissement: {}", id);
        Avertissement found = repository.findOne(id);
        if (found == null) {
            throw new NotFoundException();
        } else {
            repository.delete(id);
        }
    }

}
