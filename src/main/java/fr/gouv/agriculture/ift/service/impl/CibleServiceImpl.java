package fr.gouv.agriculture.ift.service.impl;

import fr.gouv.agriculture.ift.controller.csv.CibleCSV;
import fr.gouv.agriculture.ift.controller.form.CibleForm;
import fr.gouv.agriculture.ift.exception.ConflictException;
import fr.gouv.agriculture.ift.exception.InvalidParameterException;
import fr.gouv.agriculture.ift.exception.NotFoundException;
import fr.gouv.agriculture.ift.model.Cible;
import fr.gouv.agriculture.ift.model.DoseReference;
import fr.gouv.agriculture.ift.model.enumeration.TypeDoseReference;
import fr.gouv.agriculture.ift.repository.CibleRepository;
import fr.gouv.agriculture.ift.repository.DoseReferencePredicateBuilder;
import fr.gouv.agriculture.ift.service.CibleService;
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
@CacheConfig(cacheNames = "cible")
public class CibleServiceImpl implements CibleService {

    @Autowired
    EntityManager entityManager;

    @Autowired
    private CibleRepository repository;

    private static final String code = "Code";
    private static final String label = "Label";

    private static final Sort SORT = new Sort(Sort.Direction.ASC, "libelle");

    @Override
    public List<Cible> findCibles(String campagneIdMetier, String cultureIdMetier, String[] numeroAmmIdMetier, String filtre, Pageable pageable) {
        List<Cible> cibles;
        if (!StringUtils.isEmpty(campagneIdMetier) ||
                !StringUtils.isEmpty(numeroAmmIdMetier) ||
                !StringUtils.isEmpty(cultureIdMetier)) {
            cibles = findCiblesByCampagneAndCultureAndOrNumeroAmm(campagneIdMetier, cultureIdMetier, numeroAmmIdMetier, filtre, pageable);
        } else if (!StringUtils.isEmpty(filtre)){
            cibles = findAllCibles(filtre, pageable);
        } else {
            cibles = findAllCibles(pageable);
        }

        if (cibles.size() == 0){
            throw new NotFoundException();
        }
        return cibles;
    }

    @Override
    public List<Cible> findAllCibles() {
        log.debug("Get All Cibles");
        return repository.findAll(SORT);
    }

    @Override
    public List<Cible> findAllCibles(Pageable pageable) {
        if (pageable != null){
            log.debug("Get All Cibles");
            PageRequest pageRequest = new PageRequest(pageable.getPageNumber(), pageable.getPageSize(), SORT);
            return repository.findAll(pageRequest).getContent();
        }else {
            return findAllCibles();
        }
    }

    @Override
    public List<Cible> findAllCibles(String filter, Pageable pageable) {
        String normalizedFilter = StringHelper.normalizeTerm(filter);
        return repository.findCibleByNormalizedLibelleStartingWithOrNormalizedLibelleContainingOrIdMetierStartingWithOrderByLibelleAsc(normalizedFilter, " " + normalizedFilter, normalizedFilter, pageable);
    }

    @Override
    public String findAllCiblesAsCSV() {
        List<Cible> cibles = repository.findAll(SORT);
        return CsvUtils.writeAsCSV(CibleCSV.class, CibleCSV.toCsvDTO(cibles).toArray());
    }

    public List<Cible> findCiblesByCampagneAndCultureAndOrNumeroAmm(String campagneIdMetier, String cultureIdMetier, String[] numeroAmmIdMetier, String filtre, Pageable pageable) {
        log.debug("Get All Cibles by Campagne And Culture And/Or NumeroAmm: {}", campagneIdMetier, cultureIdMetier, numeroAmmIdMetier);

        CriteriaBuilder builder = entityManager.getCriteriaBuilder();

        CriteriaQuery<Cible> query = builder.createQuery(Cible.class);
        Root<Cible> fromCible = query.from(Cible.class);

        Subquery doseRefQuery = query.subquery(DoseReference.class);
        Root<DoseReference> fromDoseRef = doseRefQuery.from(DoseReference.class);

        DoseReferencePredicateBuilder predicateBuilder = new DoseReferencePredicateBuilder(builder, fromDoseRef);
        Predicate doseRefPredicate = predicateBuilder.appendPredicate(builder.conjunction(), campagneIdMetier, numeroAmmIdMetier, cultureIdMetier, null, TypeDoseReference.cible, null);

        doseRefQuery.select(fromDoseRef.get("cible"));
        doseRefQuery.where(doseRefPredicate);

        Predicate ciblePredicate = builder.in(fromCible.get("id")).value(doseRefQuery);
        Path<String> libellePath = fromCible.get("libelle");
        Path<String> normalizedLibellePath = fromCible.get("normalizedLibelle");
        Path<String> idMetierPath = fromCible.get("idMetier");

        if (!StringUtils.isEmpty(filtre)){
            String normalizedFilter = StringHelper.normalizeTerm(filtre);
            Predicate filterPredicate = builder.or(builder.like(normalizedLibellePath, normalizedFilter + '%'),
                    builder.like(normalizedLibellePath, "% " + normalizedFilter + "%"),
                    builder.like(idMetierPath, normalizedFilter + "%"));
            ciblePredicate = builder.and(ciblePredicate, filterPredicate);
        }

        query.where(ciblePredicate);
        query.orderBy(builder.asc(libellePath));

        TypedQuery<Cible> typedQuery = entityManager.createQuery(query);
        if (pageable != null){
            typedQuery = typedQuery.setFirstResult(pageable.getOffset())
                    .setMaxResults(pageable.getPageSize());
        }

        return typedQuery.getResultList();
    }

    @Override
    public Cible findCibleById(UUID id) {
        return findCibleById(id, null);
    }

    @Override
    public Cible findCibleById(UUID id, Class<? extends Throwable> throwableClass) {
        log.debug("Get Cible by Id: {}", id.toString());
        Cible found = repository.findOne(id);

        if (found == null) {
            if (InvalidParameterException.class.equals(throwableClass)){
                throw new InvalidParameterException("La cible ayant pour id " + id + " n'existe pas.");
            }
            throw new NotFoundException();
        } else {
            return found;
        }
    }

    @Override
    public Cible findCibleByIdMetier(String idMetier) {
        return findCibleByIdMetier(idMetier, null);
    }

    @Override
    public Cible findCibleByIdMetier(String idMetier, Class<? extends Throwable> throwableClass) {
        log.debug("Get Cible by IdMetier: {}", idMetier);
        Cible found = repository.findCibleByIdMetier(idMetier);

        if (found == null) {
            if (InvalidParameterException.class.equals(throwableClass)){
                throw new InvalidParameterException("La cible ayant pour id métier" + idMetier + " n'existe pas.");
            }
            throw new NotFoundException();
        } else {
            return found;
        }
    }

    @Override
    @Cacheable(key = "#root.methodName + '_' + #idMetier")
    public Cible findCibleByIdMetierWithCache(String idMetier, Class<? extends Throwable> throwableClass) {
        return findCibleByIdMetier(idMetier, throwableClass);
    }

    @CacheEvict(allEntries = true)
    public void cleanCache() { }

    @Override
    public Cible save(CibleForm cibleForm) {
        Cible cible = CibleForm.mapToCible(cibleForm);
        Cible found = repository.findCibleByIdMetier(cible.getIdMetier());

        if (found == null) {
            cible.setId(UUID.randomUUID());
            log.debug("Create Cible: {}", cible);
        } else {
            throw newConflictException(cible);
        }

        cible.setNormalizedLibelle(StringHelper.normalizeTerm(cible.getLibelle()));

        return repository.save(cible);
    }

    @Override
    public Cible saveOrUpdate(CibleForm cibleForm) {
        Cible cible = CibleForm.mapToCible(cibleForm);
        Cible found = repository.findCibleByIdMetier(cible.getIdMetier());

        if (found == null) {
            cible.setId(UUID.randomUUID());
            log.debug("Create Cible: {}", cible);
        } else {
            cible.setId(found.getId());
            cible.setDateCreation(found.getDateCreation());
            cible.setDateDerniereMaj(LocalDateTime.now());
            log.debug("Update Cible: {}", cible);
        }

        cible.setNormalizedLibelle(StringHelper.normalizeTerm(cible.getLibelle()));

        return repository.save(cible);
    }

    @Override
    public Cible updateById(UUID id, CibleForm cibleForm) {
        Cible found = repository.findOne(id);

        if (found == null) {
            throw new NotFoundException();
        } else {
            Cible cible = CibleForm.mapToCible(cibleForm);
            cible.setId(id);
            cible.setDateCreation(found.getDateCreation());
            cible.setDateDerniereMaj(LocalDateTime.now());
            cible.setNormalizedLibelle(StringHelper.normalizeTerm(cible.getLibelle()));
            log.debug("Update Cible: {}", cible);

            try {
                return repository.save(cible);
            } catch (DataIntegrityViolationException e) {
                throw newConflictException(cible);
            }
        }
    }

    @Override
    public void delete(UUID id) {
        log.debug("Delete Cible: {}", id);
        Cible found = repository.findOne(id);
        if (found == null) {
            throw new NotFoundException();
        } else {
            repository.delete(id);
        }
    }

    @Override
    public String addCibles(InputStream inputStream) {
        try {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String crunchifyLine = bufferedReader.readLine();
            String[] splitData = crunchifyLine.split("\\t");
            Map<String, Integer> columns = new HashMap<>();
            for (int i = 0; i < splitData.length; i++) {
                splitData[i] = StringHelper.removeDoubleQuotes(splitData[i]);
                switch (splitData[i]) {
                    case code:
                        columns.put(code, i);
                        break;
                    case label:
                        columns.put(label, i);
                        break;
                    default:
                        break;
                }
            }

            List<Cible> cibles = new ArrayList<>();

            while ((crunchifyLine = bufferedReader.readLine()) != null) {
                CibleForm cible = crunchifyCSVtoArrayList(crunchifyLine, columns);
                Cible saved = saveOrUpdate(cible);
                cibles.add(saved);
            }
            return cibles.size() + " cibles ont été ajoutées ou mises à jour.\n";
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Utility which converts CSV to ArrayList using Split Operation
    private CibleForm crunchifyCSVtoArrayList(String crunchifyCSV, Map<String, Integer> columns) {
        if (crunchifyCSV != null) {
            String[] splitData = crunchifyCSV.split("\\t");
            return CibleForm.builder()
                    .idMetier(StringHelper.removeDoubleQuotes(splitData[columns.get(code)]))
                    .libelle(StringHelper.removeDoubleQuotes(splitData[columns.get(label)]))
                    .build();
        }
        return null;
    }

    private ConflictException newConflictException(Cible cible){
        return new ConflictException("La cible avec l'identifiant " + cible.getIdMetier() + " existe déjà.");
    }
}
