package fr.gouv.agriculture.ift.service.impl;

import fr.gouv.agriculture.ift.controller.form.GroupeCulturesForm;
import fr.gouv.agriculture.ift.exception.ConflictException;
import fr.gouv.agriculture.ift.exception.NotFoundException;
import fr.gouv.agriculture.ift.model.GroupeCultures;
import fr.gouv.agriculture.ift.repository.GroupeCulturesRepository;
import fr.gouv.agriculture.ift.service.GroupeCulturesService;
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
public class GroupeCulturesServiceImpl implements GroupeCulturesService {

    @Autowired
    private GroupeCulturesRepository repository;

    @Override
    public GroupeCultures save(GroupeCulturesForm groupeCulturesForm) {
        GroupeCultures newGroupeCultures = GroupeCulturesForm.mapToGroupeCultures(groupeCulturesForm);
        GroupeCultures found = repository.findGroupeCulturesByIdMetier(newGroupeCultures.getIdMetier());

        if (found == null) {
            newGroupeCultures.setId(UUID.randomUUID());
            log.debug("Create GroupeCultures: {}", newGroupeCultures);
        } else {
            throw newConflictException(newGroupeCultures);
        }

        return repository.save(newGroupeCultures);
    }

    @Override
    public List<GroupeCultures> findAllGroupesCultures() {
        log.debug("Get All GroupesCultures");
        return repository.findAll(new Sort(Sort.Direction.ASC, "libelle"));
    }

    @Override
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

            try {
                return repository.save(groupeCultures);
            } catch (DataIntegrityViolationException e) {
                throw newConflictException(groupeCultures);
            }
        }
    }

    @Override
    public void delete(UUID id) {
        log.debug("Delete GroupeCultures: {}", id);
        GroupeCultures found = repository.findOne(id);
        if (found == null) {
            throw new NotFoundException();
        } else {
            repository.delete(id);
        }
    }

    private ConflictException newConflictException(GroupeCultures groupeCultures){
        return new ConflictException("Le groupe de cultures avec l'identifiant " + groupeCultures.getIdMetier() + " existe déjà.");
    }

}
