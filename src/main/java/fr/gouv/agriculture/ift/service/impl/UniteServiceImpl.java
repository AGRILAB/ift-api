package fr.gouv.agriculture.ift.service.impl;

import fr.gouv.agriculture.ift.controller.form.UniteForm;
import fr.gouv.agriculture.ift.exception.InvalidParameterException;
import fr.gouv.agriculture.ift.exception.NotFoundException;
import fr.gouv.agriculture.ift.model.Unite;
import fr.gouv.agriculture.ift.repository.UniteRepository;
import fr.gouv.agriculture.ift.service.UniteService;
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
@CacheConfig(cacheNames = "unite")
public class UniteServiceImpl implements UniteService {

    @Autowired
    private UniteRepository repository;

    @Override
    @Cacheable(key = "#root.methodName")
    public List<Unite> findAllUnites() {
        log.debug("Get All Unites");
        return repository.findAll();
    }

    @Override
    @Cacheable(key = "#root.methodName + '_' + #id")
    public Unite findUniteById(UUID id) {
        return findUniteById(id, null);
    }

    @Override
    @Cacheable(key = "#root.methodName + '_' + #id")
    public Unite findUniteById(UUID id, Class<? extends Throwable> throwableClass) {
        log.debug("Get Unite by Id: {}", id.toString());
        Unite found = repository.findOne(id);

        if (found == null) {
            if (InvalidParameterException.class.equals(throwableClass)){
                throw new InvalidParameterException("L'unité ayant pour id " + id + " n'existe pas.");
            }
            throw new NotFoundException();
        } else {
            return found;
        }
    }

    @Override
    @Cacheable(key = "#root.methodName + '_' + #idMetier")
    public Unite findUniteByIdMetier(String idMetier) {
        return findUniteByIdMetier(idMetier, null);
    }

    @Override
    @Cacheable(key = "#root.methodName + '_' + #idMetier")
    public Unite findUniteByIdMetier(String idMetier, Class<? extends Throwable> throwableClass) {
        log.debug("Get Unite by IdMetier: {}", idMetier);
        Unite found = repository.findUniteByIdMetier(idMetier);

        if (found == null) {
            if (InvalidParameterException.class.equals(throwableClass)){
                throw new InvalidParameterException("L'unité ayant pour id métier" + idMetier + " n'existe pas.");
            }
            throw new NotFoundException();
        } else {
            return found;
        }
    }

    @Override
    @CacheEvict(allEntries = true)
    public Unite save(UniteForm uniteForm) {
        Unite newUnite = UniteForm.mapToUnite(uniteForm);
        Unite found = repository.findUniteByIdMetier(newUnite.getIdMetier());

        if (newUnite.getUniteDeConversion() != null && newUnite.getUniteDeConversion().getId() == null) {
            newUnite.getUniteDeConversion().setUnite(findUniteByIdMetier(newUnite.getUniteDeConversion().getUnite().getIdMetier()));
            newUnite.getUniteDeConversion().setId(UUID.randomUUID());
        }

        if (found == null) {
            newUnite.setId(UUID.randomUUID());
            log.debug("Create Unite: {}", newUnite);
        } else {
            newUnite.setId(found.getId());
            newUnite.setDateCreation(found.getDateCreation());
            newUnite.setDateDerniereMaj(LocalDateTime.now());
            log.debug("Update Unite: {}", newUnite);
        }

        return repository.save(newUnite);
    }

    @Override
    @CacheEvict(allEntries = true)
    public Unite updateById(UUID id, UniteForm uniteForm) {
        Unite found = repository.findOne(id);

        if (found == null) {
            throw new NotFoundException();
        } else {
            Unite unite = UniteForm.mapToUnite(uniteForm);

            if (unite.getUniteDeConversion() != null && unite.getUniteDeConversion().getId() == null) {
                unite.getUniteDeConversion().setUnite(findUniteByIdMetier(unite.getUniteDeConversion().getUnite().getIdMetier()));
                unite.getUniteDeConversion().setId(UUID.randomUUID());
            }

            unite.setId(id);
            unite.setDateCreation(found.getDateCreation());
            unite.setDateDerniereMaj(LocalDateTime.now());
            log.debug("Update Unite: {}", unite);
            return repository.save(unite);
        }
    }

    @Override
    @CacheEvict(allEntries = true)
    public void delete(UUID id) {
        log.debug("Delete Unite: {}", id);
        Unite found = repository.findOne(id);
        if (found == null) {
            throw new NotFoundException();
        } else {
            repository.delete(id);
        }
    }

}
