package fr.gouv.agriculture.ift.service.impl;

import fr.gouv.agriculture.ift.dto.NumeroAmmDTO;
import fr.gouv.agriculture.ift.exception.ConflictException;
import fr.gouv.agriculture.ift.exception.InvalidParameterException;
import fr.gouv.agriculture.ift.exception.NotFoundException;
import fr.gouv.agriculture.ift.model.*;
import fr.gouv.agriculture.ift.repository.DoseReferencePredicateBuilder;
import fr.gouv.agriculture.ift.repository.NumeroAmmRepository;
import fr.gouv.agriculture.ift.repository.ValiditeProduitRepository;
import fr.gouv.agriculture.ift.service.CampagneService;
import fr.gouv.agriculture.ift.service.NumeroAmmService;
import fr.gouv.agriculture.ift.service.ProduitService;
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
import org.springframework.transaction.annotation.Transactional;
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
import java.util.stream.Collectors;

@Slf4j
@Service
@CacheConfig(cacheNames = "numeroAmm")
public class NumeroAmmServiceImpl implements NumeroAmmService {

    @Autowired
    EntityManager entityManager;

    @Autowired
    private NumeroAmmRepository repository;

    @Autowired
    private ValiditeProduitRepository validiteProduitRepository;

    @Autowired
    private CampagneService campagneService;

    @Autowired
    private ProduitService produitService;

    private static final String codeAmm = "Code_AMM";
    private static final Sort SORT = new Sort(Sort.Direction.ASC, "idMetier");

    public List<NumeroAmm> findNumerosAmm(String campagneIdMetier, String cultureIdMetier, String cibleIdMetier, String filtre, Pageable pageable){
        List<NumeroAmm> numerosAmm;
        if (!StringUtils.isEmpty(campagneIdMetier) || !StringUtils.isEmpty(cultureIdMetier) || !StringUtils.isEmpty(cibleIdMetier)) {
            numerosAmm = findNumerosAmmByCampagneAndCultureAndCible(campagneIdMetier, cultureIdMetier, cibleIdMetier, filtre, pageable);
        } else if (!StringUtils.isEmpty(filtre)){
            numerosAmm = findAllNumerosAmm(filtre, pageable);
        } else {
            numerosAmm = findAllNumerosAmm(pageable);
        }

        if (numerosAmm.size() == 0){
            throw new NotFoundException();
        }
        return numerosAmm;
    }

    @Override
    public List<NumeroAmm> findAllNumerosAmm() {
        log.debug("Get All NumerosAmm");
        return repository.findAll(SORT);
    }

    @Override
    public List<NumeroAmm> findAllNumerosAmm(Pageable pageable) {
        log.debug("Get All NumerosAmm");

        if (pageable != null){
            //Rebuild PageRequest to force sort by numeroAmm.idMetier
            PageRequest pageRequest = new PageRequest(pageable.getPageNumber(), pageable.getPageSize(), SORT);
            return repository.findAll(pageRequest).getContent();
        }else {
            return findAllNumerosAmm();
        }

    }

    public List<NumeroAmm> findAllNumerosAmm(String filtre, Pageable pageable) {
        return repository.findNumeroAmmByIdMetierLike(filtre + "%", pageable);
    }

    @Override
    public List<NumeroAmm> findNumerosAmmByCampagneAndCultureAndCible(String campagneIdMetier, String cultureIdMetier, String cibleIdMetier, String filtre, Pageable pageable) {
        log.debug("Get All Numeros Amm by Campagne And Culture And/Or Cible: {}", campagneIdMetier, cultureIdMetier, cibleIdMetier);

        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<NumeroAmm> query = builder.createQuery(NumeroAmm.class);
        Root<NumeroAmm> fromNumeroAmm = query.from(NumeroAmm.class);

        Subquery doseRefQuery = query.subquery(DoseReference.class);
        Root<DoseReference> fromDoseRef = doseRefQuery.from(DoseReference.class);

        DoseReferencePredicateBuilder predicateBuilder = new DoseReferencePredicateBuilder(builder, fromDoseRef);
        Predicate doseRefPredicate = predicateBuilder.appendPredicate(builder.conjunction(), campagneIdMetier, null, cultureIdMetier, cibleIdMetier);

        doseRefQuery.select(fromDoseRef.get("numeroAmm"));
        doseRefQuery.where(doseRefPredicate);

        Predicate numeroAmmPredicate = builder.in(fromNumeroAmm.get("id")).value(doseRefQuery);
        Path<String> idMetierPath = fromNumeroAmm.get("idMetier");

        if (!StringUtils.isEmpty(filtre)) {
            Predicate filterPredicate = builder.like(idMetierPath, filtre + "%");
            numeroAmmPredicate = builder.and(numeroAmmPredicate, filterPredicate);
        }

        query.where(numeroAmmPredicate);
        query.orderBy(builder.asc(idMetierPath));

        TypedQuery<NumeroAmm> typedQuery = entityManager.createQuery(query);
        if (pageable != null){
            typedQuery = typedQuery.setFirstResult(pageable.getOffset())
                    .setMaxResults(pageable.getPageSize());
        }

        return typedQuery.getResultList();

    }

    @Override
    public List<NumeroAmm> findNumerosAmmByCampagneAndCultureAndProduitAndCible(
            String campagneIdMetier, String cultureIdMetier, String produitLibelle, String cibleIdMetier){
        log.debug("Get All NumerosAmm by Campagne And/Or Culture And/Or Produit And/Or Cible: {}", campagneIdMetier, cultureIdMetier, produitLibelle, cibleIdMetier);

        CriteriaBuilder builder = entityManager.getCriteriaBuilder();

        CriteriaQuery<NumeroAmm> query = builder.createQuery(NumeroAmm.class);
        Root<NumeroAmm> fromNumeroAmm = query.from(NumeroAmm.class);

        //Subquery for DoseReference
        Subquery produitDoseRefQuery = query.subquery(ProduitDoseReference.class);
        Root<ProduitDoseReference> fromProduitDoseRef = produitDoseRefQuery.from(ProduitDoseReference.class);

        DoseReferencePredicateBuilder predicateBuilder = new DoseReferencePredicateBuilder(builder, fromProduitDoseRef);
        Predicate doseRefPredicate = predicateBuilder.appendPredicate(builder.conjunction(), campagneIdMetier, null, cultureIdMetier, cibleIdMetier, produitLibelle);

        produitDoseRefQuery.select(fromProduitDoseRef.get("numeroAmm"));
        produitDoseRefQuery.where(doseRefPredicate);

        Predicate numeroAmmPredicate = builder.in(fromNumeroAmm.get("id")).value(produitDoseRefQuery);
        Path<String> idMetierPath = fromNumeroAmm.get("idMetier");

        query.where(numeroAmmPredicate);
        query.orderBy(builder.asc(idMetierPath));

        TypedQuery<NumeroAmm> typedQuery = entityManager.createQuery(query);

        List<NumeroAmm> numerosAmm = typedQuery.getResultList();

        if (numerosAmm.size() == 0){
            throw new NotFoundException();
        }
        return numerosAmm;
    }

    @Override
    public List<NumeroAmmDTO> findNumerosAmmWithValidities(String filtre, Pageable pageable) {
        log.debug("Get All Numeros Amm with validites filtered by id métier: {}", filtre);

        PageRequest pageRequest = new PageRequest(pageable.getPageNumber(), pageable.getPageSize(), SORT);

        List<NumeroAmm> numeroAmms;
        if (filtre != null){
            numeroAmms = repository.findNumeroAmmByIdMetierLike(filtre.trim() + '%', pageRequest);
        }else {
            numeroAmms = repository.findAll(pageRequest).getContent();
        }


        List<UUID> numeroAmmsIds = numeroAmms.stream().map((numeroAmm) -> numeroAmm.getId()).collect(Collectors.toList());
        List<ValiditeProduit> validitesProduit = validiteProduitRepository.findByNumeroAmmIdInOrderByNumeroAmmIdMetierAscProduitLibelleAsc(numeroAmmsIds);

        List<Campagne> campagnes = campagneService.findAllCampagnes();

        List<NumeroAmmDTO> numerosAmmDto = numeroAmms.stream().map(numeroAmm -> {

            List<ValiditeProduit> filteredValiditesProduit = validitesProduit.stream()
                    .filter(validiteProduit -> validiteProduit.getNumeroAmm().getId().equals(numeroAmm.getId()))
                    .collect(Collectors.toList());

            return getNumeroAmmWithValidite(numeroAmm, campagnes, filteredValiditesProduit);
        }).collect(Collectors.toList());

        if (numerosAmmDto.size() == 0){
            throw new NotFoundException();
        }
        return numerosAmmDto;
    }

    private NumeroAmmDTO getNumeroAmmWithValidite(NumeroAmm numeroAmm){
        List<Campagne> campagnes = campagneService.findAllCampagnes();
        List<ValiditeProduit> validitesProduit = validiteProduitRepository.findByNumeroAmmIdInOrderByNumeroAmmIdMetierAscProduitLibelleAsc(Arrays.asList(numeroAmm.getId()));
        return getNumeroAmmWithValidite(numeroAmm, campagnes, validitesProduit);
    }

    private NumeroAmmDTO getNumeroAmmWithValidite(NumeroAmm numeroAmm, List<Campagne> campagnes, List<ValiditeProduit> validitesProduit){
        LinkedHashMap<String, Map<String, Boolean>> validitesProduitMap = new LinkedHashMap<String, Map<String, Boolean>>();

        for (ValiditeProduit validiteProduit : validitesProduit) {
            String produit = validiteProduit.getProduit().getLibelle();
            Map<String, Boolean> validiteCampagnes = validitesProduitMap.get(produit);

            if (validiteCampagnes == null) {
                LinkedHashMap<String, Boolean> validiteCampagnesMap = new LinkedHashMap<String, Boolean>();
                campagnes.stream().forEachOrdered((campagne) -> {
                    validiteCampagnesMap.put(campagne.getIdMetier(), Boolean.FALSE);
                });
                validiteCampagnes = validiteCampagnesMap;
            }

            String campagne = validiteProduit.getCampagne().getIdMetier();
            validiteCampagnes.put(campagne, Boolean.TRUE);
            validitesProduitMap.put(produit, validiteCampagnes);
        }


        return NumeroAmmDTO.builder()
                .id(numeroAmm.getId())
                .idMetier(numeroAmm.getIdMetier())
                .validites(validitesProduitMap)
                .build();
    }

    @Override
    public NumeroAmm findNumeroAmmById(UUID id) {
        return findNumeroAmmById(id, null);
    }

    @Override
    public NumeroAmm findNumeroAmmById(UUID id, Class<? extends Throwable> throwableClass) {
        log.debug("Get NumeroAmm by Id: {}", id.toString());
        NumeroAmm found = repository.findOne(id);

        if (found == null) {
            if (InvalidParameterException.class.equals(throwableClass)){
                throw new InvalidParameterException("Le NumeroAmm ayant pour id " + id + " n'existe pas.");
            }
            throw new NotFoundException();
        } else {
            return found;
        }
    }

    @Override
    public NumeroAmm findNumeroAmmByIdMetier(String idMetier) {
        log.debug("Get NumeroAmm by idMetier: {}", idMetier);
        NumeroAmm found = repository.findNumeroAmmByIdMetier(idMetier);

        if (found == null) {
            throw new NotFoundException();
        } else {
            return found;
        }
    }

    @Override
    @Cacheable(key = "#root.methodName + '_' + #idMetier")
    public NumeroAmm findNumeroAmmByIdMetierWithCache(String idMetier) {
        return findNumeroAmmByIdMetier(idMetier);
    }

    @CacheEvict(allEntries = true)
    public void cleanCache() { }

    @Override
    @Transactional
    public NumeroAmmDTO save(NumeroAmmDTO numeroAmmDTO) {
        NumeroAmm newNumeroAmm = NumeroAmmDTO.mapToNumeroAmm(numeroAmmDTO);
        NumeroAmm found = repository.findNumeroAmmByIdMetier(newNumeroAmm.getIdMetier());

        if (found == null) {
            newNumeroAmm.setId(UUID.randomUUID());
            log.debug("Create NumeroAmm: {}", newNumeroAmm);

            NumeroAmm numeroAmm = repository.save(newNumeroAmm);

            //Create related ValiditeProduit
            Map<String, Map<String, Boolean>> validites = numeroAmmDTO.getValidites();

            if (validites != null){
                createValiditesProduit(numeroAmm, validites);
            }

            return getNumeroAmmWithValidite(numeroAmm);
        } else {
            throw newConflictException(newNumeroAmm);
        }
    }

    private NumeroAmm saveOrUpdate(NumeroAmm numeroAmm) {
        NumeroAmm found = repository.findNumeroAmmByIdMetier(numeroAmm.getIdMetier());

        if (found == null) {
            numeroAmm.setId(UUID.randomUUID());
            log.debug("Create NumeroAmm: {}", numeroAmm);
        } else {
            numeroAmm.setId(found.getId());
            numeroAmm.setDateCreation(found.getDateCreation());
            numeroAmm.setDateDerniereMaj(LocalDateTime.now());
            log.debug("Update NumeroAmm: {}", numeroAmm);
        }

        return repository.save(numeroAmm);
    }

    private void createValiditesProduit(NumeroAmm numeroAmm, Map<String, Map<String, Boolean>> validites){

        List<Campagne> campagnes = campagneService.findAllCampagnes();

        for (String produitLibelle: validites.keySet()){
            Produit produit = produitService.findProduitByLibelle(produitLibelle, InvalidParameterException.class);

            Map<String, Boolean> validiteProduit = validites.get(produitLibelle);

            for (Campagne campagne: campagnes){
                Boolean valide = validiteProduit.get(campagne.getIdMetier());

                if (Boolean.TRUE.equals(valide)){
                    ValiditeProduit validite = ValiditeProduit.builder()
                            .id(UUID.randomUUID())
                            .campagne(campagne)
                            .numeroAmm(numeroAmm)
                            .produit(produit)
                            .build();

                    validiteProduitRepository.save(validite);
                }
            }
        }
    }

    @Override
    @Transactional
    public NumeroAmmDTO updateById(UUID id, NumeroAmmDTO numeroAmmDTO) {
        NumeroAmm found = repository.findOne(id);

        if (found == null) {
            throw new NotFoundException();
        } else {
            NumeroAmm numeroAmm = NumeroAmmDTO.mapToNumeroAmm(numeroAmmDTO);
            numeroAmm.setId(id);
            numeroAmm.setDateCreation(found.getDateCreation());
            numeroAmm.setDateDerniereMaj(LocalDateTime.now());
            log.debug("Update NumeroAmm: {}", numeroAmm);

            try {
                numeroAmm = repository.save(numeroAmm);

                //Create related ValiditeProduit
                Map<String, Map<String, Boolean>> validites = numeroAmmDTO.getValidites();

                if (validites != null){
                    updateValiditesProduit(numeroAmm, validites);
                }
            } catch (DataIntegrityViolationException e) {
                throw newConflictException(numeroAmm);
            }
            return getNumeroAmmWithValidite(numeroAmm);
        }
    }

    private void updateValiditesProduit(NumeroAmm numeroAmm, Map<String, Map<String, Boolean>> validites){

        List<Campagne> campagnes = campagneService.findAllCampagnes();

        for (String produitLibelle: validites.keySet()){
            Produit produit = produitService.findProduitByLibelle(produitLibelle, InvalidParameterException.class);

            Map<String, Boolean> validiteProduit = validites.get(produitLibelle);

            for (Campagne campagne: campagnes){
                Boolean valide = validiteProduit.get(campagne.getIdMetier());

                if (Boolean.TRUE.equals(valide)){
                    ValiditeProduit found = validiteProduitRepository.findByProduitIdAndCampagneIdAndNumeroAmmId(produit.getId(), campagne.getId(), numeroAmm.getId());

                    if (found == null){
                        ValiditeProduit validite = ValiditeProduit.builder()
                                .id(UUID.randomUUID())
                                .campagne(campagne)
                                .numeroAmm(numeroAmm)
                                .produit(produit)
                                .build();

                        validiteProduitRepository.save(validite);
                    }
                }else {
                    //Search existing validite to delete it
                    ValiditeProduit validite = validiteProduitRepository.findByProduitIdAndCampagneIdAndNumeroAmmId(produit.getId(), campagne.getId(), numeroAmm.getId());
                    if (validite != null){
                        validiteProduitRepository.delete(validite);
                    }
                }
            }
        }
    }

    @Override
    public void delete(UUID id) {
        log.debug("Delete NumeroAmm: {}", id);
        NumeroAmm found = repository.findOne(id);
        if (found == null) {
            throw new NotFoundException();
        } else {
            repository.delete(id);
        }
    }

    @Override
    public String addNumerosAmm(InputStream inputStream) {
        try {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String crunchifyLine = bufferedReader.readLine();
            String[] splitData = crunchifyLine.split("\\t");
            Map<String, Integer> columns = new HashMap<>();
            for (int i = 0; i < splitData.length; i++) {
                splitData[i] = StringHelper.removeDoubleQuotes(splitData[i]);
                switch (splitData[i]) {
                    case codeAmm:
                        columns.put(codeAmm, i);
                        break;
                    default:
                        break;
                }
            }
            List<NumeroAmm> numerosAmm = new ArrayList<>();

            while ((crunchifyLine = bufferedReader.readLine()) != null) {
                NumeroAmm numeroAmm = crunchifyCSVtoArrayList(crunchifyLine, columns);
                NumeroAmm saved = saveOrUpdate(numeroAmm);
                numerosAmm.add(saved);
            }

            return numerosAmm.size() + " numéros AMM ont été ajoutés ou mis à jour.\n";
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Utility which converts CSV to ArrayList using Split Operation
    private NumeroAmm crunchifyCSVtoArrayList(String crunchifyCSV, Map<String, Integer> columns) {
        if (crunchifyCSV != null) {
            String[] splitData = crunchifyCSV.split("\\t");

            NumeroAmm numeroAmm = NumeroAmm.builder()
                    .id(UUID.randomUUID())
                    .idMetier(StringHelper.removeDoubleQuotes(splitData[columns.get(codeAmm)]))
                    .build();
            return numeroAmm;
        }
        return null;
    }

    private ConflictException newConflictException(NumeroAmm numeroAmm){
        return new ConflictException("Le numéro AMM avec l'identifiant " + numeroAmm.getIdMetier() + " existe déjà.");
    }
}
