package fr.gouv.agriculture.ift.service.impl;

import fr.gouv.agriculture.ift.controller.csv.ProduitCSV;
import fr.gouv.agriculture.ift.controller.form.ProduitForm;
import fr.gouv.agriculture.ift.exception.ConflictException;
import fr.gouv.agriculture.ift.exception.InvalidParameterException;
import fr.gouv.agriculture.ift.exception.NotFoundException;
import fr.gouv.agriculture.ift.model.*;
import fr.gouv.agriculture.ift.repository.DoseReferencePredicateBuilder;
import fr.gouv.agriculture.ift.repository.NumeroAmmRepository;
import fr.gouv.agriculture.ift.repository.ProduitRepository;
import fr.gouv.agriculture.ift.repository.ValiditeProduitRepository;
import fr.gouv.agriculture.ift.service.ProduitService;
import fr.gouv.agriculture.ift.util.CsvUtils;
import fr.gouv.agriculture.ift.util.StringHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
public class ProduitServiceImpl implements ProduitService {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private ProduitRepository repository;

    @Autowired
    private NumeroAmmRepository numeroAmmRepository;

    @Autowired
    private ValiditeProduitRepository validiteProduitRepository;

    private static final String nomProduit = "Nom_produit";
    private static final String amm = "AMM";

    private static final Sort SORT = new Sort(Sort.Direction.ASC, "libelle");

    @Override
    public List<Produit> findProduits(String campagneIdMetier, String cultureIdMetier, String cibleIdMetier, String filter, Pageable pageable) {

        if ((cultureIdMetier != null || cibleIdMetier != null) && campagneIdMetier == null){
            throw new InvalidParameterException("Le paramètre campagneIdMetier est requis lorsque cultureIdMetier ou cibleIdMetier sont renseignés");
        }

        List<Produit> produits;
        if (!StringUtils.isEmpty(campagneIdMetier) ||
                !StringUtils.isEmpty(cultureIdMetier) ||
                !StringUtils.isEmpty(cibleIdMetier)) {
            produits = findProduitsByCampagneAndOrCultureAndOrCible(campagneIdMetier, cultureIdMetier, cibleIdMetier, filter, pageable);
        } else if (!StringUtils.isEmpty(filter)){
            produits = findAllProduits(filter, pageable);
        } else {
            produits = findAllProduits(pageable);
        }

        if (produits.size() == 0){
            throw new NotFoundException();
        }
        return produits;

    }

    private List<Produit> findAllCultures() {
        log.debug("Get All Cultures");
        return repository.findAll(SORT);
    }

    private List<Produit> findAllProduits(Pageable pageable){
        if (pageable != null){
            log.debug("Get All Produits");
            PageRequest pageRequest = new PageRequest(pageable.getPageNumber(), pageable.getPageSize(), SORT);
            return repository.findAll(pageRequest).getContent();
        }else {
            return findAllCultures();
        }
    }

    private List<Produit> findAllProduits(String filter, Pageable pageable) {
        String normalizedFilter = StringHelper.normalizeTerm(filter);
        return repository.findProduitByNormalizedLibelleStartingWithOrNormalizedLibelleContainingOrderByLibelleAsc(
                normalizedFilter, " " + normalizedFilter, pageable);
    }

    @Override
    public List<Produit> findProduitsByCampagneAndOrCultureAndOrCible(String campagneIdMetier, String cultureIdMetier, String cibleIdMetier, String filtre, Pageable pageable) {
        return queryProduits(campagneIdMetier, cultureIdMetier, cibleIdMetier, filtre, pageable);
    }

    @Override
    public String findProduitsByCampagneAsCSV(String campagneIdMetier) {
        List<ValiditeProduit> validitesProduit = validiteProduitRepository.findByCampagneIdMetier(campagneIdMetier);
        return CsvUtils.writeAsCSV(ProduitCSV.class, ProduitCSV.toCsvDTO(validitesProduit).toArray());
    }

    private List<Produit> queryProduits(String campagneIdMetier, String cultureIdMetier, String cibleIdMetier, String filtre, Pageable pageable) {

        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Produit> query = builder.createQuery(Produit.class);
        Root<Produit> fromProduit = query.from(Produit.class);

        Subquery doseRefQuery = query.subquery(ProduitDoseReference.class);
        Root<DoseReference> fromDoseRef = doseRefQuery.from(ProduitDoseReference.class);

        DoseReferencePredicateBuilder predicateBuilder = new DoseReferencePredicateBuilder(builder, fromDoseRef);
        Predicate doseRefPredicate = predicateBuilder.appendPredicate(builder.conjunction(), campagneIdMetier, null, cultureIdMetier, cibleIdMetier);

        doseRefQuery.select(fromDoseRef.get("produit"));
        doseRefQuery.where(doseRefPredicate);

        Predicate produitPredicate = builder.in(fromProduit.get("id")).value(doseRefQuery);
        Path<String> libellePath = fromProduit.get("libelle");
        Path<String> normalizedLibellePath = fromProduit.get("normalizedLibelle");

        if (!StringUtils.isEmpty(filtre)){
            String normalizedFilter = StringHelper.normalizeTerm(filtre);
            Predicate filterPredicate = builder.or(builder.like(normalizedLibellePath, normalizedFilter + '%'),
                    builder.like(normalizedLibellePath, "% " + normalizedFilter + "%"));
            produitPredicate = builder.and(produitPredicate, filterPredicate);
        }

        query.where(produitPredicate);
        query.orderBy(builder.asc(libellePath));

        TypedQuery<Produit> typedQuery = entityManager.createQuery(query);
        if (pageable != null){
            typedQuery = typedQuery.setFirstResult(pageable.getOffset())
                    .setMaxResults(pageable.getPageSize());
        }

        return typedQuery.getResultList();
    }

    @Override
    public List<NumeroAmm> getNumeroAmmByProduitAndCampagne(String produitLibelle, String campagneIdMetier){

        List<ValiditeProduit> validitesProduit = validiteProduitRepository.findByProduitLibelleAndCampagneIdMetier(produitLibelle, campagneIdMetier);

        List<NumeroAmm> numerosAmm = validitesProduit.stream().map(ValiditeProduit::getNumeroAmm).collect(Collectors.toList());

        if (numerosAmm.size() == 0){
            throw new NotFoundException();
        }

        return numerosAmm;
    }

    @Override
    public Produit findProduitById(UUID id) {
        log.debug("Get Produit by Id: {}", id.toString());
        Produit found = repository.findOne(id);

        if (found == null) {
            throw new NotFoundException();
        } else {
            return found;
        }
    }

    @Override
    public Produit findProduitByLibelle(String libelle,  Class<? extends Throwable> throwableClass) {
        log.debug("Get Produit by Libelle: {}", libelle);
        Produit found = repository.findByLibelle(libelle);

        if (found == null) {
            if (InvalidParameterException.class.equals(throwableClass)){
                throw new InvalidParameterException("Le produit ayant pour libelle " + libelle + " n'existe pas.");
            }
            throw new NotFoundException();
        } else {
            return found;
        }
    }

    @Override
    public Produit save(ProduitForm produitForm) {
        Produit newProduit = ProduitForm.mapToProduit(produitForm);
        Produit found = repository.findByLibelle(newProduit.getLibelle());

        if (found == null) {
            newProduit.setId(UUID.randomUUID());
            log.debug("Create Produit: {}", newProduit);
        } else {
            throw newConflictException(newProduit);
        }

        newProduit.setNormalizedLibelle(StringHelper.normalizeTerm(newProduit.getLibelle()));

        return repository.save(newProduit);
    }

    @Override
    public Produit updateById(UUID id, ProduitForm produitForm) {
        Produit found = repository.findOne(id);

        if (found == null) {
            throw new NotFoundException();
        } else {
            Produit produit = ProduitForm.mapToProduit(produitForm);
            produit.setId(id);
            produit.setDateCreation(found.getDateCreation());
            produit.setDateDerniereMaj(LocalDateTime.now());
            produit.setNormalizedLibelle(StringHelper.normalizeTerm(produit.getLibelle()));
            log.debug("Update Produit: {}", produit);

            try{
                return repository.save(produit);
            } catch (DataIntegrityViolationException e) {
                throw newConflictException(produit);
            }
        }
    }

    @Override
    public void delete(UUID id) {
        log.debug("Delete Produit: {}", id);
        Produit found = repository.findOne(id);
        if (found == null) {
            throw new NotFoundException();
        } else {
            repository.delete(id);
        }
    }

    @Override
    public String addProduits(Campagne campagne, InputStream inputStream) {
        try {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String crunchifyLine = bufferedReader.readLine();
            String[] splitData = crunchifyLine.split("\\t");
            Map<String, Integer> columns = new HashMap<>();
            for (int i = 0; i < splitData.length; i++) {
                splitData[i] = StringHelper.removeDoubleQuotes(splitData[i]);
                switch (splitData[i]) {
                    case nomProduit:
                        columns.put(nomProduit, i);
                        break;
                    case amm:
                        columns.put(amm, i);
                        break;
                    default:
                        break;
                }
            }
            List<ValiditeProduit> validiteProduits = new ArrayList<>();

            while ((crunchifyLine = bufferedReader.readLine()) != null) {
                ValiditeProduit validiteProduit = crunchifyCSVtoArrayList(campagne, crunchifyLine, columns);
                validiteProduits.add(validiteProduit);
            }
            List<ValiditeProduit> validiteProduitList = validiteProduitRepository.save(validiteProduits);
            return validiteProduitList.size() + " produits ont été ajoutés.\n";
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Utility which converts CSV to ArrayList using Split Operation
    private ValiditeProduit crunchifyCSVtoArrayList(Campagne campagne, String crunchifyCSV, Map<String, Integer> columns) {
        if (crunchifyCSV != null) {
            String[] splitData = crunchifyCSV.split("\\t");

            Produit produit = repository.findByLibelle(StringHelper.removeDoubleQuotes(splitData[columns.get(nomProduit)]));
            if (produit == null) {
                produit = Produit.builder()
                        .id(UUID.randomUUID())
                        .libelle(StringHelper.removeDoubleQuotes(splitData[columns.get(nomProduit)]))
                        .normalizedLibelle(StringHelper.normalizeTerm(StringHelper.removeDoubleQuotes(splitData[columns.get(nomProduit)])))
                        .build();
                produit = repository.save(produit);
            }else {
                produit.setNormalizedLibelle(StringHelper.normalizeTerm(produit.getLibelle()));
                produit = repository.save(produit);
            }

            NumeroAmm numeroAmm = numeroAmmRepository.findNumeroAmmByIdMetier(StringHelper.removeDoubleQuotes(splitData[columns.get(amm)]));
            if (numeroAmm == null) {
                numeroAmm = NumeroAmm.builder()
                        .id(UUID.randomUUID())
                        .idMetier(StringHelper.removeDoubleQuotes(splitData[columns.get(amm)]))
                        .build();
                numeroAmm = numeroAmmRepository.save(numeroAmm);
            }

            ValiditeProduit validiteProduit = ValiditeProduit.builder()
                    .id(UUID.randomUUID())
                    .campagne(campagne)
                    .produit(produit)
                    .numeroAmm(numeroAmm)
                    .build();

            return validiteProduit;
        }
        return null;
    }


    @Transactional
    @Override
    public void deleteValiditeProduitByCampagne(Campagne campagne) {
        validiteProduitRepository.deleteByCampagneId(campagne.getId());
    }

    private ConflictException newConflictException(Produit produit){
        return new ConflictException("Le produit " + produit.getLibelle() + " existe déjà.");
    }

}
