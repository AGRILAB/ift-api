package fr.gouv.agriculture.ift.service.impl;

import fr.gouv.agriculture.ift.controller.form.GroupeCulturesForm;
import fr.gouv.agriculture.ift.exception.NotFoundException;
import fr.gouv.agriculture.ift.model.GroupeCultures;
import fr.gouv.agriculture.ift.repository.GroupeCulturesRepository;
import fr.gouv.agriculture.ift.service.GroupeCulturesService;
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
@CacheConfig(cacheNames = "groupeCultures")
public class GroupeCulturesServiceImpl implements GroupeCulturesService {

    @Autowired
    private GroupeCulturesRepository repository;

    @Override
    @CacheEvict(allEntries = true)
    public GroupeCultures save(GroupeCulturesForm groupeCulturesForm) {
        GroupeCultures newGroupeCultures = GroupeCulturesForm.mapToGroupeCultures(groupeCulturesForm);
        GroupeCultures found = repository.findGroupeCulturesByIdMetier(newGroupeCultures.getIdMetier());

        if (found == null) {
            newGroupeCultures.setId(UUID.randomUUID());
            log.debug("Create GroupeCultures: {}", newGroupeCultures);
        } else {
            newGroupeCultures.setId(found.getId());
            newGroupeCultures.setDateCreation(found.getDateCreation());
            newGroupeCultures.setDateDerniereMaj(LocalDateTime.now());
            log.debug("Update GroupeCultures: {}", newGroupeCultures);
        }

        return repository.save(newGroupeCultures);
    }

    @Override
    @Cacheable(key = "#root.methodName")
    public List<GroupeCultures> findAllGroupesCultures() {
        log.debug("Get All GroupesCultures");
        return repository.findAll();
    }

    @Override
    @Cacheable(key = "#root.methodName + '_' + #id")
    public GroupeCultures findGroupeCulturesById(UUID id) {
        log.debug("Get GroupeCultures by Id: {}", id.toString());
        GroupeCultures found = repository.findOne(id);

        if (found == null) {
            throw new NotFoundException();
        } else {
            return found;
        }
    }

    @Override
    @Cacheable(key = "#root.methodName + '_' + #idMetier")
    public GroupeCultures findGroupeCulturesByIdMetier(String idMetier) {
        log.debug("Get GroupeCultures by IdMetier: {}", idMetier);
        GroupeCultures found = repository.findGroupeCulturesByIdMetier(idMetier);

        if (found == null) {
            throw new NotFoundException();
        } else {
            return found;
        }
    }

    @Override
    @CacheEvict(allEntries = true)
    public GroupeCultures updateById(UUID id, GroupeCulturesForm groupeCulturesForm) {
        GroupeCultures found = repository.findOne(id);

        if (found == null) {
            throw new NotFoundException();
        } else {
            GroupeCultures groupeCultures = GroupeCulturesForm.mapToGroupeCultures(groupeCulturesForm);
            groupeCultures.setId(id);
            groupeCultures.setDateCreation(found.getDateCreation());
            groupeCultures.setDateDerniereMaj(LocalDateTime.now());
            log.debug("Update GroupeCultures: {}", groupeCultures);
            return repository.save(groupeCultures);
        }
    }

    @Override
    @CacheEvict(allEntries = true)
    public void delete(UUID id) {
        log.debug("Delete GroupeCultures: {}", id);
        GroupeCultures found = repository.findOne(id);
        if (found == null) {
            throw new NotFoundException();
        } else {
            repository.delete(id);
        }
    }

}
