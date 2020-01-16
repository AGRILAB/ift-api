package fr.gouv.agriculture.ift.service.impl;

import fr.gouv.agriculture.ift.controller.csv.CultureCSV;
import fr.gouv.agriculture.ift.controller.form.CultureForm;
import fr.gouv.agriculture.ift.exception.ConflictException;
import fr.gouv.agriculture.ift.exception.InvalidParameterException;
import fr.gouv.agriculture.ift.exception.NotFoundException;
import fr.gouv.agriculture.ift.model.Culture;
import fr.gouv.agriculture.ift.model.DoseReference;
import fr.gouv.agriculture.ift.model.GroupeCultures;
import fr.gouv.agriculture.ift.repository.CultureRepository;
import fr.gouv.agriculture.ift.repository.DoseReferencePredicateBuilder;
import fr.gouv.agriculture.ift.service.CultureService;
import fr.gouv.agriculture.ift.service.GroupeCulturesService;
import fr.gouv.agriculture.ift.util.CsvUtils;
import fr.gouv.agriculture.ift.util.StringHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DataIntegrityViolationException;
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
    public List<Culture> findCultures(String campagneIdMetier, String[] numeroAmmIdMetier, String cibleIdMetier, String filter, Pageable pageable) {
        List<Culture> cultures;
        if (!StringUtils.isEmpty(campagneIdMetier) ||
                !StringUtils.isEmpty(numeroAmmIdMetier) ||
                !StringUtils.isEmpty(cibleIdMetier)) {
            cultures = findCulturesByCampagneAndNumeroAmmAndOrCible(campagneIdMetier, numeroAmmIdMetier, cibleIdMetier, filter, pageable);
        } else if (!StringUtils.isEmpty(filter)){
            cultures = findAllCultures(filter, pageable);
        } else {
            cultures = findAllCultures(pageable);
        }

        if (cultures.size() == 0){
            throw new NotFoundException();
        }
        return cultures;

    }

    @Override
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
        return repository.findCultureByNormalizedLibelleStartingWithOrNormalizedLibelleContainingOrIdMetierStartingWithOrderByLibelleAsc(normalizedFilter, " " + normalizedFilter, normalizedFilter, pageable);
    }

    @Override
    public String findAllCulturesAsCSV() {
        List<Culture> cultures = repository.findAll(SORT);
        return CsvUtils.writeAsCSV(CultureCSV.class, CultureCSV.toCsvDTO(cultures).toArray());
    }

    @Override
    public List<Culture> findCulturesByCampagneAndNumeroAmmAndOrCible(
            String campagneIdMetier, String[] numeroAmmIdMetier, String cibleIdMetier, String filter, Pageable pageable) {
        log.debug("Get All Cultures by Campagne And/Or NumeroAmm And/Or Cible: {}", campagneIdMetier, numeroAmmIdMetier, cibleIdMetier);

        CriteriaBuilder builder = entityManager.getCriteriaBuilder();

        CriteriaQuery<Culture> query = builder.createQuery(Culture.class);
        Root<Culture> fromCulture = query.from(Culture.class);

        //Subquery for DoseReference
        Subquery doseRefQuery = query.subquery(DoseReference.class);
        Root<DoseReference> fromDoseRef = doseRefQuery.from(DoseReference.class);

        DoseReferencePredicateBuilder predicateBuilder = new DoseReferencePredicateBuilder(builder, fromDoseRef);
        Predicate doseRefPredicate = predicateBuilder.appendPredicate(builder.conjunction(), campagneIdMetier, numeroAmmIdMetier, null, cibleIdMetier);

        doseRefQuery.select(fromDoseRef.get("culture"));
        doseRefQuery.where(doseRefPredicate);

        Predicate culturePredicate = builder.in(fromCulture.get("id")).value(doseRefQuery);
        Path<String> libellePath = fromCulture.get("libelle");
        Path<String> normalizedLibellePath = fromCulture.get("normalizedLibelle");
        Path<String> idMetierPath = fromCulture.get("idMetier");

        if (!StringUtils.isEmpty(filter)){
            String normalizedFilter = StringHelper.normalizeTerm(filter);
            Predicate filterPredicate = builder.or(builder.like(normalizedLibellePath, normalizedFilter + '%'),
                    builder.like(normalizedLibellePath, "% " + normalizedFilter + "%"),
                    builder.like(idMetierPath, normalizedFilter + "%"));
            culturePredicate = builder.and(culturePredicate, filterPredicate);
        }

        query.where(culturePredicate );
        query.orderBy(builder.asc(libellePath));

        TypedQuery<Culture> typedQuery = entityManager.createQuery(query);
        if (pageable != null){
            typedQuery = typedQuery.setFirstResult(pageable.getOffset())
                    .setMaxResults(pageable.getPageSize());
        }

        return typedQuery.getResultList();
    }

    @Override
    public List<Culture> findCulturesByGroupeCultures(String groupeCulturesIdMetier) {
        log.debug("Get All Cultures by GroupeCultures: {}", groupeCulturesIdMetier.toString());
        return repository.findCultureByGroupeCulturesIdMetierOrderByLibelleAsc(groupeCulturesIdMetier);
    }

    @Override
    public Culture findCultureById(UUID id) {
        return findCultureById(id, null);
    }

    @Override
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
    public Culture findCultureByIdMetier(String idMetier) {
        return findCultureByIdMetier(idMetier, null);
    }

    @Override
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
    @Cacheable(key = "#root.methodName + '_' + #idMetier")
    public Culture findCultureByIdMetierWithCache(String idMetier, Class<? extends Throwable> throwableClass) {
        return findCultureByIdMetier(idMetier, throwableClass);
    }

    @CacheEvict(allEntries = true)
    public void cleanCache() { }

    @Override
    public Culture save(CultureForm cultureForm) {
        try {
            GroupeCultures groupeCultures = groupeCulturesService.findGroupeCulturesById(cultureForm.getGroupeCulturesId());
            Culture newCulture = CultureForm.mapToCulture(cultureForm, groupeCultures);

            Culture found = repository.findCultureByIdMetier(newCulture.getIdMetier());

            if (found == null) {
                newCulture.setId(UUID.randomUUID());
                log.debug("Create Culture: {}", newCulture);
            } else {
                throw newConflictException(newCulture);
            }

            newCulture.setNormalizedLibelle(StringHelper.normalizeTerm(newCulture.getLibelle()));

            return repository.save(newCulture);
        } catch (NotFoundException ex) {
            throw new InvalidParameterException("Le groupe de cultures ayant pour id " + cultureForm.getGroupeCulturesId() + " n'existe pas.");
        }
    }

    private Culture saveOrUpdate(Culture culture) {
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
    public Culture updateById(UUID id, CultureForm cultureForm) {
        Culture found = repository.findOne(id);

        if (found == null) {
            throw new NotFoundException();
        } else {
            GroupeCultures groupeCultures;
            try {
                groupeCultures = groupeCulturesService.findGroupeCulturesById(cultureForm.getGroupeCulturesId());
            } catch (NotFoundException ex) {
                throw new InvalidParameterException("Le groupe de cultures ayant pour id " + cultureForm.getGroupeCulturesId() + " n'existe pas.");
            }
            Culture culture = CultureForm.mapToCulture(cultureForm, groupeCultures);
            culture.setId(id);
            culture.setDateCreation(found.getDateCreation());
            culture.setDateDerniereMaj(LocalDateTime.now());
            culture.setNormalizedLibelle(StringHelper.normalizeTerm(culture.getLibelle()));
            log.debug("Update Culture: {}", culture);

            try{
                return repository.save(culture);
            } catch (DataIntegrityViolationException e){
                throw newConflictException(culture);
            }
        }
    }

    @Override
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
    public String addCultures(InputStream inputStream) {
        try {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String crunchifyLine = bufferedReader.readLine();
            String[] splitData = crunchifyLine.split("\\t");
            Map<String, Integer> columns = new HashMap<>();
            for (int i = 0; i < splitData.length; i++) {
                splitData[i] = StringHelper.removeDoubleQuotes(splitData[i]);
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
                Culture saved = saveOrUpdate(culture);
                cultures.add(saved);
            }
            return cultures.size() + " cultures ont été ajoutées ou mises à jour.\n";
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
                GroupeCultures groupeCultures = groupeCulturesService.findGroupeCulturesByIdMetier(StringHelper.removeDoubleQuotes(splitData[columns.get(codeGroupeCulture)]));

                Culture culture = Culture.builder()
                        .id(UUID.randomUUID())
                        .idMetier(StringHelper.removeDoubleQuotes(splitData[columns.get(codeCulture)]))
                        .libelle(StringHelper.removeDoubleQuotes(splitData[columns.get(labelCulture)]))
                        .groupeCultures(groupeCultures)
                        .build();
                return culture;
            } catch (NotFoundException ex) {
                throw new InvalidParameterException("Le groupe de cultures ayant pour id métier " + StringHelper.removeDoubleQuotes(splitData[columns.get(codeGroupeCulture)]) + " n'existe pas.");
            }
        }
        return null;
    }

    private ConflictException newConflictException(Culture culture){
        return new ConflictException("La culture avec l'identifiant " + culture.getIdMetier() + " existe déjà.");
    }

}
