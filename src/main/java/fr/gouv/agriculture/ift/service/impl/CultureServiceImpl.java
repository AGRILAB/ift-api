package fr.gouv.agriculture.ift.service.impl;

import fr.gouv.agriculture.ift.controller.form.CultureForm;
import fr.gouv.agriculture.ift.exception.InvalidParameterException;
import fr.gouv.agriculture.ift.exception.NotFoundException;
import fr.gouv.agriculture.ift.model.Culture;
import fr.gouv.agriculture.ift.model.DoseReference;
import fr.gouv.agriculture.ift.model.GroupeCultures;
import fr.gouv.agriculture.ift.repository.CultureRepository;
import fr.gouv.agriculture.ift.repository.DoseReferencePredicateBuilder;
import fr.gouv.agriculture.ift.service.CultureService;
import fr.gouv.agriculture.ift.service.GroupeCulturesService;
import fr.gouv.agriculture.ift.util.StringHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
@CacheConfig(cacheNames = "culture")
public class CultureServiceImpl implements CultureService {

    @Autowired
    EntityManager entityManager;

    @Autowired
    private CultureRepository repository;

    @Autowired
    private GroupeCulturesService groupeCulturesService;

    private static final String codeCulture = "Code_culture";
    private static final String labelCulture = "Label_culture";
    private static final String codeGroupeCulture = "Code_groupe_culture";

    private static final Sort SORT = new Sort(Sort.Direction.ASC, "libelle");

    @Override
    public List<Culture> findCultures(String campagneIdMetier, String numeroAmmIdMetier, String cibleIdMetier, String filter, Pageable pageable) {
        if (!StringUtils.isEmpty(campagneIdMetier) ||
                !StringUtils.isEmpty(numeroAmmIdMetier) ||
                !StringUtils.isEmpty(cibleIdMetier)) {
            return findCulturesByCampagneAndNumeroAmmAndOrCible(campagneIdMetier, numeroAmmIdMetier, cibleIdMetier, filter, pageable);
        } else if (!StringUtils.isEmpty(filter)){
            return findAllCultures(filter, pageable);
        } else {
            return findAllCultures(pageable);
        }
    }

    @Override
    @Cacheable(key = "#root.methodName")
    public List<Culture> findAllCultures() {
        log.debug("Get All Cultures");
        return repository.findAll(SORT);
    }

    @Override
    public List<Culture> findAllCultures(Pageable pageable) {

        if (pageable != null){
            log.debug("Get All Cultures");
            PageRequest pageRequest = new PageRequest(pageable.getPageNumber(), pageable.getPageSize(), SORT);
            return repository.findAll(pageRequest).getContent();
        }else {
            return findAllCultures();
        }
    }

    @Override
    public List<Culture> findAllCultures(String filter, Pageable pageable) {
        String normalizedFilter = StringHelper.normalizeTerm(filter);
        return repository.findCultureByNormalizedLibelleStartingWithOrNormalizedLibelleContainingOrderByLibelleAsc(normalizedFilter, " " + normalizedFilter, pageable);
    }

    @Override
    public List<Culture> findCulturesByCampagneAndNumeroAmmAndOrCible(
            String campagneIdMetier, String numeroAmmIdMetier, String cibleIdMetier, String filter, Pageable pageable) {
        log.debug("Get All Cultures by Campagne And/Or NumeroAmm And/Or Cible: {}", campagneIdMetier, numeroAmmIdMetier, cibleIdMetier);

        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Culture> query = builder.createQuery(Culture.class);
        Root<DoseReference> root = query.from(DoseReference.class);

        DoseReferencePredicateBuilder predicateBuilder = new DoseReferencePredicateBuilder(builder, root);
        Predicate predicate = predicateBuilder.appendPredicate(builder.conjunction(), campagneIdMetier, numeroAmmIdMetier, null, cibleIdMetier);

        Path<Culture> cultureType = root.get("culture");
        Path<String> libellePath = cultureType.get("libelle");
        Path<String> normalizedLibellePath = cultureType.get("normalizedLibelle");

        if (!StringUtils.isEmpty(filter)){
            String normalizedFilter = StringHelper.normalizeTerm(filter);
            Predicate filterPredicate = builder.or(builder.like(normalizedLibellePath, normalizedFilter + '%'),
                    builder.like(normalizedLibellePath, "% " + normalizedFilter + "%"));
            predicate = builder.and(predicate, filterPredicate);
        }

        query.where(predicate);

        query.select(cultureType).distinct(true);
        query.orderBy(builder.asc(libellePath));

        TypedQuery<Culture> typedQuery = entityManager.createQuery(query);
        if (pageable != null){
            typedQuery = typedQuery.setFirstResult(pageable.getOffset())
                    .setMaxResults(pageable.getPageSize());
        }

        return typedQuery.getResultList();
    }

    @Override
    @Cacheable(key = "#root.methodName + '_' + #groupeCulturesIdMetier")
    public List<Culture> findCulturesByGroupeCultures(String groupeCulturesIdMetier) {
        log.debug("Get All Cultures by GroupeCultures: {}", groupeCulturesIdMetier.toString());
        return repository.findCultureByGroupeCulturesIdMetierOrderByLibelleAsc(groupeCulturesIdMetier);
    }

    @Override
    @Cacheable(key = "#root.methodName + '_' + #id")
    public Culture findCultureById(UUID id) {
        return findCultureById(id, null);
    }

    @Override
    @Cacheable(key = "#root.methodName + '_' + #id")
    public Culture findCultureById(UUID id, Class<? extends Throwable> throwableClass) {
        log.debug("Get Culture by Id: {}", id.toString());
        Culture found = repository.findOne(id);

        if (found == null) {
            if (InvalidParameterException.class.equals(throwableClass)){
                throw new InvalidParameterException("La culture ayant pour id " + id + " n'existe pas.");
            }
            throw new NotFoundException();
        } else {
            return found;
        }
    }

    @Override
    @Cacheable(key = "#root.methodName + '_' + #idMetier")
    public Culture findCultureByIdMetier(String idMetier) {
        return findCultureByIdMetier(idMetier, null);
    }

    @Override
    @Cacheable(key = "#root.methodName + '_' + #idMetier")
    public Culture findCultureByIdMetier(String idMetier, Class<? extends Throwable> throwableClass) {
        log.debug("Get Culture by IdMetier: {}", idMetier);
        Culture found = repository.findCultureByIdMetier(idMetier);

        if (found == null) {
            if (InvalidParameterException.class.equals(throwableClass)){
                throw new InvalidParameterException("La culture ayant pour id métier " + idMetier + " n'existe pas.");
            }
            throw new NotFoundException();
        } else {
            return found;
        }
    }

    @Override
    @CacheEvict(allEntries = true)
    public Culture save(CultureForm cultureForm) {
        try {
            GroupeCultures groupeCultures = groupeCulturesService.findGroupeCulturesById(cultureForm.getGroupeCulturesId());
            Culture newCulture = CultureForm.mapToCulture(cultureForm, groupeCultures);
            return save(newCulture);
        } catch (NotFoundException ex) {
            throw new InvalidParameterException("Le groupe de cultures ayant pour id " + cultureForm.getGroupeCulturesId() + " n'existe pas.");
        }
    }

    private Culture save(Culture culture) {
        Culture found = repository.findCultureByIdMetier(culture.getIdMetier());

        if (found == null) {
            culture.setId(UUID.randomUUID());
            log.debug("Create Culture: {}", culture);
        } else {
            culture.setId(found.getId());
            culture.setDateCreation(found.getDateCreation());
            culture.setDateDerniereMaj(LocalDateTime.now());
            log.debug("Update Culture: {}", culture);
        }

        culture.setNormalizedLibelle(StringHelper.normalizeTerm(culture.getLibelle()));

        return repository.save(culture);
    }

    @Override
    @CacheEvict(allEntries = true)
    public Culture updateById(UUID id, CultureForm cultureForm) {
        Culture found = repository.findOne(id);

        if (found == null) {
            throw new NotFoundException();
        } else {
            try {
                GroupeCultures groupeCultures = groupeCulturesService.findGroupeCulturesById(cultureForm.getGroupeCulturesId());
                Culture culture = CultureForm.mapToCulture(cultureForm, groupeCultures);
                culture.setId(id);
                culture.setDateCreation(found.getDateCreation());
                culture.setDateDerniereMaj(LocalDateTime.now());
                culture.setNormalizedLibelle(StringHelper.normalizeTerm(culture.getLibelle()));
                log.debug("Update Culture: {}", culture);
                return repository.save(culture);
            } catch (NotFoundException ex) {
                throw new InvalidParameterException("Le groupe de cultures ayant pour id " + cultureForm.getGroupeCulturesId() + " n'existe pas.");
            }
        }
    }

    @Override
    @CacheEvict(allEntries = true)
    public void delete(UUID id) {
        log.debug("Delete Culture: {}", id);
        Culture found = repository.findOne(id);
        if (found == null) {
            throw new NotFoundException();
        } else {
            repository.delete(id);
        }
    }

    @Override
    @CacheEvict(allEntries = true)
    public List<Culture> addCultures(InputStream inputStream) {
        try {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String crunchifyLine = bufferedReader.readLine();
            String[] splitData = crunchifyLine.split("\\t");
            Map<String, Integer> columns = new HashMap<>();
            for (int i = 0; i < splitData.length; i++) {
                switch (splitData[i]) {
                    case codeCulture:
                        columns.put(codeCulture, i);
                        break;
                    case labelCulture:
                        columns.put(labelCulture, i);
                        break;
                    case codeGroupeCulture:
                        columns.put(codeGroupeCulture, i);
                        break;
                    default:
                        break;
                }
            }
            List<Culture> cultures = new ArrayList<>();

            while ((crunchifyLine = bufferedReader.readLine()) != null) {
                Culture culture = crunchifyCSVtoArrayList(crunchifyLine, columns);
                Culture saved = save(culture);
                cultures.add(saved);
            }
            return cultures;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Utility which converts CSV to ArrayList using Split Operation
    private Culture crunchifyCSVtoArrayList(String crunchifyCSV, Map<String, Integer> columns) {
        if (crunchifyCSV != null) {
            String[] splitData = crunchifyCSV.split("\\t");
            try {
                GroupeCultures groupeCultures = groupeCulturesService.findGroupeCulturesByIdMetier(splitData[columns.get(codeGroupeCulture)]);

                Culture culture = Culture.builder()
                        .id(UUID.randomUUID())
                        .idMetier(splitData[columns.get(codeCulture)])
                        .libelle(splitData[columns.get(labelCulture)])
                        .groupeCultures(groupeCultures)
                        .build();
                return culture;
            } catch (NotFoundException ex) {
                throw new InvalidParameterException("Le groupe de cultures ayant pour id métier " + splitData[columns.get(codeGroupeCulture)] + " n'existe pas.");
            }
        }
        return null;
    }

}
