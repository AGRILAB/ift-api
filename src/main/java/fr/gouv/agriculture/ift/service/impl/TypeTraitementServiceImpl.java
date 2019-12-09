package fr.gouv.agriculture.ift.service.impl;

import fr.gouv.agriculture.ift.controller.form.TypeTraitementForm;
import fr.gouv.agriculture.ift.exception.NotFoundException;
import fr.gouv.agriculture.ift.model.TypeTraitement;
import fr.gouv.agriculture.ift.repository.TypeTraitementRepository;
import fr.gouv.agriculture.ift.service.TypeTraitementService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class TypeTraitementServiceImpl implements TypeTraitementService {

    @Autowired
    private TypeTraitementRepository repository;

    @Override
    public List<TypeTraitement> findAllTypesTraitements() {
        log.debug("Get All Traitements");
        return repository.findAll(new Sort(Sort.Direction.ASC, "libelle"));
    }

    @Override
    public TypeTraitement findTypeTraitementById(UUID id) {
        log.debug("Get TypeTraitement by Id: {}", id.toString());
        TypeTraitement found = repository.findOne(id);

        if (found == null) {
            throw new NotFoundException();
        } else {
            return found;
        }
    }

    @Override
    public TypeTraitement findTypeTraitementByIdMetier(String idMetier) {
        log.debug("Get TypeTraitement by IdMetier: {}", idMetier);
        TypeTraitement found = repository.findTypeTraitementByIdMetier(idMetier);

        if (found == null) {
            throw new NotFoundException();
        } else {
            return found;
        }
    }

    @Override
    public TypeTraitement updateById(UUID id, TypeTraitementForm typeTraitementForm) {
        TypeTraitement found = repository.findOne(id);

        if (found == null) {
            throw new NotFoundException();
        } else {
            TypeTraitement typeTraitement = TypeTraitementForm.mapToTypeTraitement(typeTraitementForm);
            typeTraitement.setId(id);
            typeTraitement.setIdMetier(found.getIdMetier());
            typeTraitement.setDateCreation(found.getDateCreation());
            typeTraitement.setDateDerniereMaj(LocalDateTime.now());
            log.debug("Update TypeTraitement: {}", typeTraitement);
            return repository.save(typeTraitement);
        }
    }

}
