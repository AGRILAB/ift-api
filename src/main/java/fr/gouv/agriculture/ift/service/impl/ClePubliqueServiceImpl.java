package fr.gouv.agriculture.ift.service.impl;

import fr.gouv.agriculture.ift.exception.NotFoundException;
import fr.gouv.agriculture.ift.model.ClePublique;
import fr.gouv.agriculture.ift.repository.ClePubliqueRepository;
import fr.gouv.agriculture.ift.service.ClePubliqueService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@CacheConfig(cacheNames = "clePublique")
public class ClePubliqueServiceImpl implements ClePubliqueService {

    @Autowired
    private ClePubliqueRepository repository;

    @Override
    @Cacheable(key = "#root.methodName + '_' + #cle")
    public ClePublique findClePubliqueByCle(String cle) {
        log.debug("Get ClePublique by Cle: {}", cle);
        ClePublique found = repository.findClePubliqueByCle(cle);

        if (found == null) {
            throw new NotFoundException();
        } else {
            return found;
        }
    }

    @Override
    public ClePublique save(ClePublique clePublique) {
        ClePublique found = repository.findClePubliqueByCle(clePublique.getCle());

        if (found == null) {
            clePublique.setId(UUID.randomUUID());
            log.debug("Create ClePublique: {}", clePublique);
        } else {
            clePublique.setId(found.getId());
            clePublique.setDateCreation(found.getDateCreation());
            clePublique.setDateDerniereMaj(LocalDateTime.now());
            log.debug("Update ClePublique: {}", clePublique);
        }

        return repository.save(clePublique);
    }
}
