package fr.gouv.agriculture.ift.service.impl;

import fr.gouv.agriculture.ift.controller.form.DoseReferenceForm;
import fr.gouv.agriculture.ift.exception.InvalidParameterException;
import fr.gouv.agriculture.ift.exception.NotFoundException;
import fr.gouv.agriculture.ift.model.*;
import fr.gouv.agriculture.ift.model.enumeration.TypeDoseReference;
import fr.gouv.agriculture.ift.repository.DoseReferencePredicateBuilder;
import fr.gouv.agriculture.ift.repository.DoseReferenceRepository;
import fr.gouv.agriculture.ift.repository.NumeroAmmRepository;
import fr.gouv.agriculture.ift.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
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
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
public class DoseReferenceServiceImpl implements DoseReferenceService {

    @Autowired
    EntityManager entityManager;

    @Autowired
    private DoseReferenceRepository repository;

    @Autowired
    private NumeroAmmRepository numeroAmmRepository;

    @Autowired
    private CampagneService campagneService;

    @Autowired
    private CultureService cultureService;

    @Autowired
    private CibleService cibleService;

    @Autowired
    private SegmentService segmentService;

    @Autowired
    private UniteService uniteService;

    @Autowired
    private NumeroAmmService numeroAmmService;

    private static final String idCultureColumn = "Id_culture";
    private static final String codeAmmColumn = "Code_AMM";
    private static final String biocontroleColumn = "Biocontrole";
    private static final String idCibleColumn = "Id_cible";
    private static final String idSegmentColumn = "Id_Segment";
    private static final String doseRefColumn = "Dose_ref";
    private static final String idUniteColumn = "Id_unite_dose_ref";

    @Override
    public List<DoseReference> findAllDosesReference() {
        log.debug("Get All DosesReference");
        return repository.findAll();
    }

    @Override
    public List<DoseReference> findAllDosesReference(Pageable pageable) {
        log.debug("Get All DosesReference");
        return pageable == null ? repository.findAll() : repository.findAll(pageable).getContent();
    }


    @Override
    public List<DoseReference> findDosesReference(String campagneIdMetier,
                                                  String cultureIdMetier,
                                                  String numeroAmmIdMetier,
                                                  String cibleIdMetier,
                                                  TypeDoseReference typeDoseReference) {

        log.debug("Get All DosesReference by campagneIdMetier: {}, cultureIdMetier: {}, numeroAmmIdMetier: {}, cibleIdMetier: {}, typeDoseReference: {}",
                campagneIdMetier, cultureIdMetier, numeroAmmIdMetier, cibleIdMetier, typeDoseReference);
        return queryDosesReference(campagneIdMetier, cultureIdMetier, numeroAmmIdMetier, cibleIdMetier, typeDoseReference, null);
    }

    @Override
    public List<DoseReference> findDosesReference(String campagneIdMetier,
                                                  String cultureIdMetier,
                                                  String numeroAmmIdMetier,
                                                  String cibleIdMetier,
                                                  TypeDoseReference typeDoseReference,
                                                  Pageable pageable) {
        log.debug("Get pageable DosesReference by campagneIdMetier: {}, cultureIdMetier: {}, numeroAmmIdMetier: {}, cibleIdMetier: {}, typeDoseReference: {}",
                campagneIdMetier, cultureIdMetier, numeroAmmIdMetier, cibleIdMetier, typeDoseReference);
        return queryDosesReference(campagneIdMetier, cultureIdMetier, numeroAmmIdMetier, cibleIdMetier, typeDoseReference, pageable);
    }

    private List<DoseReference> queryDosesReference(String campagneIdMetier, String cultureIdMetier, String numeroAmmIdMetier, String cibleIdMetier, TypeDoseReference typeDoseReference, Pageable pageable){
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<DoseReference> query = builder.createQuery(DoseReference.class);
        Root<DoseReference> root = query.from(DoseReference.class);

        DoseReferencePredicateBuilder predicateBuilder = new DoseReferencePredicateBuilder(builder, root);
        Predicate predicate = predicateBuilder.appendPredicate(builder.conjunction(), campagneIdMetier, numeroAmmIdMetier, cultureIdMetier, cibleIdMetier, typeDoseReference, null);
        query.where(predicate);

        Path<Campagne> campagneType = root.get("campagne");
        Path<String> idMetierCampagnePath = campagneType.get("idMetier");
        Path<Campagne> segmentType = root.get("segment");
        Path<String> idMetierSegmentPath = segmentType.get("idMetier");
        Path<Cible> uniteType = root.get("unite");
        Path<String> idMetierUnitePath = uniteType.get("idMetier");
        Path<Cible> cultureType = root.get("culture");
        Path<String> libelleCulturePath = cultureType.get("libelle");
        query.orderBy(builder.desc(idMetierCampagnePath), builder.asc(idMetierUnitePath), builder.asc(idMetierSegmentPath), builder.asc(libelleCulturePath));

        TypedQuery<DoseReference> typedQuery = entityManager.createQuery(query);

        if (pageable != null){
            typedQuery = typedQuery.setFirstResult(pageable.getOffset())
                    .setMaxResults(pageable.getPageSize());
        }
        return typedQuery.getResultList();
    }

    @Override
    public DoseReference findDoseReferenceByCampagneAndCultureAndNumeroAmmAndCible(String campagneIdMetier, String cultureIdMetier, String numeroAmmIdMetier, String cibleIdMetier) {
        DoseReference found = repository.findOneDoseReferenceByCampagneIdMetierAndNumeroAmmIdMetierAndCultureIdMetierAndCibleIdMetier(campagneIdMetier, numeroAmmIdMetier, cultureIdMetier, cibleIdMetier);

        if (found == null) {
            log.debug("La dose de référence n'existe pas pour les critères campagneIdMetier: {}, cultureIdMetier: {}, numeroAmmIdMetier: {}, cibleIdMetier: {}", campagneIdMetier, cultureIdMetier, numeroAmmIdMetier, cibleIdMetier);
            throw new NotFoundException();
        } else {
            return found;
        }
    }
    
    @Override
    public DoseReference findDoseReferenceById(UUID id) {
        log.debug("Get doseReference by Id: {}", id.toString());
        DoseReference found = repository.findOne(id);

        if (found == null) {
            throw new NotFoundException();
        } else {
            return found;
        }
    }

    @Override
    public DoseReference save(DoseReferenceForm doseReferenceForm) {
        Campagne campagne = campagneService.findCampagneById(doseReferenceForm.getCampagneId(), InvalidParameterException.class);
        Culture culture = cultureService.findCultureById(doseReferenceForm.getCultureId(), InvalidParameterException.class);
        Segment segment = segmentService.findSegmentById(doseReferenceForm.getSegmentId(), InvalidParameterException.class);
        Unite unite = uniteService.findUniteById(doseReferenceForm.getUniteId(), InvalidParameterException.class);
        NumeroAmm numeroAmm = numeroAmmService.findNumeroAmmById(doseReferenceForm.getNumeroAmmId(), InvalidParameterException.class);
        Cible cible = null;
        if (doseReferenceForm.getCibleId() != null){
            cible = cibleService.findCibleById(doseReferenceForm.getCibleId(), InvalidParameterException.class);
        }

        DoseReference newdoseReference = DoseReferenceForm.mapToDoseReference(doseReferenceForm, numeroAmm, campagne, culture, cible, segment, unite);
        return save(newdoseReference);
    }

    private DoseReference save(DoseReference doseReference) {
        DoseReference found = repository.findOneDoseReferenceByCampagneIdAndNumeroAmmIdAndCultureIdAndCibleId(
                doseReference.getCampagne().getId(),
                doseReference.getNumeroAmm().getId(),
                doseReference.getCulture().getId(),
                doseReference.getCible() != null ? doseReference.getCible().getId() : (UUID)null);

        if (found == null) {
            doseReference.setId(UUID.randomUUID());
            log.debug("Create doseReference: {}", doseReference);
        } else {
            doseReference.setId(found.getId());
            doseReference.setDateCreation(found.getDateCreation());
            doseReference.setDateDerniereMaj(LocalDateTime.now());
            log.debug("Update doseReference: {}", doseReference);
        }
        return repository.save(doseReference);
    }

    @Override
    public DoseReference updateById(UUID id, DoseReferenceForm doseReferenceForm) {
        DoseReference found = repository.findOne(id);

        if (found == null) {
            log.error("Dose de référence non trouvée pour l'id {}", id);
            throw new NotFoundException();
        } else {
            Campagne campagne = campagneService.findCampagneById(doseReferenceForm.getCampagneId(), InvalidParameterException.class);
            Culture culture = cultureService.findCultureById(doseReferenceForm.getCultureId(), InvalidParameterException.class);
            Segment segment = segmentService.findSegmentById(doseReferenceForm.getSegmentId(), InvalidParameterException.class);
            Unite unite = uniteService.findUniteById(doseReferenceForm.getUniteId(), InvalidParameterException.class);
            NumeroAmm numeroAmm = numeroAmmService.findNumeroAmmById(doseReferenceForm.getNumeroAmmId(), InvalidParameterException.class);
            Cible cible = null;
            if (doseReferenceForm.getCibleId() != null){
                cible = cibleService.findCibleById(doseReferenceForm.getCibleId(), InvalidParameterException.class);
            }

            DoseReference doseReference = DoseReferenceForm.mapToDoseReference(doseReferenceForm, numeroAmm, campagne, culture, cible, segment, unite);
            doseReference.setId(id);
            doseReference.setDateCreation(found.getDateCreation());
            doseReference.setDateDerniereMaj(LocalDateTime.now());
            log.debug("Update doseReference: {}", doseReference);
            return repository.save(doseReference);

        }
    }

    @Override
    public void delete(UUID id) {
        log.debug("Delete doseReference: {}", id);
        DoseReference found = repository.findOne(id);
        if (found == null) {
            throw new NotFoundException();
        } else {
            repository.delete(id);
        }
    }

    @Override
    public List<DoseReference> addDosesReferenceCible(Campagne campagne, InputStream inputStream) {
        try {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String crunchifyLine = bufferedReader.readLine();
            String[] splitData = crunchifyLine.split("\\t");
            Map<String, Integer> columns = new HashMap<>();
            for (int i = 0; i < splitData.length; i++) {
                switch (splitData[i]) {
                    case idCultureColumn:
                        columns.put(idCultureColumn, i);
                        break;
                    case codeAmmColumn:
                        columns.put(codeAmmColumn, i);
                        break;
                    case biocontroleColumn:
                        columns.put(biocontroleColumn, i);
                        break;
                    case idCibleColumn:
                        columns.put(idCibleColumn, i);
                        break;
                    case idSegmentColumn:
                        columns.put(idSegmentColumn, i);
                        break;
                    case doseRefColumn:
                        columns.put(doseRefColumn, i);
                        break;
                    case idUniteColumn:
                        columns.put(idUniteColumn, i);
                        break;
                    default:
                        break;
                }
            }

            List<DoseReference> doseReferences = new ArrayList<>();
            while ((crunchifyLine = bufferedReader.readLine()) != null) {
                DoseReference doseReference = crunchifyCSVtoArrayList(crunchifyLine, campagne, columns);
                doseReference.setId(UUID.randomUUID());
                doseReferences.add(doseReference);
            }
            repository.save(doseReferences);
            return doseReferences;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<DoseReference> addDosesReferenceCulture(Campagne campagne, InputStream inputStream) {
        try {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String crunchifyLine = bufferedReader.readLine();
            String[] splitData = crunchifyLine.split("\\t");
            Map<String, Integer> columns = new HashMap<>();
            for (int i = 0; i < splitData.length; i++) {
                switch (splitData[i]) {
                    case idCultureColumn:
                        columns.put(idCultureColumn, i);
                        break;
                    case codeAmmColumn:
                        columns.put(codeAmmColumn, i);
                        break;
                    case biocontroleColumn:
                        columns.put(biocontroleColumn, i);
                        break;
                    case idSegmentColumn:
                        columns.put(idSegmentColumn, i);
                        break;
                    case doseRefColumn:
                        columns.put(doseRefColumn, i);
                        break;
                    case idUniteColumn:
                        columns.put(idUniteColumn, i);
                        break;
                    default:
                        break;
                }
            }

            List<DoseReference> doseReferenceCultures = new ArrayList<>();
            while ((crunchifyLine = bufferedReader.readLine()) != null) {
                DoseReference doseReferenceCulture = crunchifyCSVtoArrayList(crunchifyLine, campagne, columns);
                doseReferenceCulture.setId(UUID.randomUUID());
                doseReferenceCultures.add(doseReferenceCulture);
            }
            repository.save(doseReferenceCultures);
            return doseReferenceCultures;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Transactional
    @Override
    public void deleteDoseReferenceCible(Campagne campagne) {
        repository.deleteByCampagneIdAndCibleIdIsNotNull(campagne.getId());
    }

    @Transactional
    @Override
    public void deleteDoseReferenceCulture(Campagne campagne) {
        repository.deleteByCampagneIdAndCibleIdIsNull(campagne.getId());
    }

    // Utility which converts CSV to ArrayList using Split Operation
    private DoseReference crunchifyCSVtoArrayList(String crunchifyCSV, Campagne campagne, Map<String, Integer> columns) {
        if (crunchifyCSV != null) {
            String[] splitData = crunchifyCSV.split("\\t");
            Culture culture = cultureService.findCultureByIdMetier(splitData[columns.get(idCultureColumn)], InvalidParameterException.class);
            Segment segment = segmentService.findSegmentByIdMetier(splitData[columns.get(idSegmentColumn)], InvalidParameterException.class);
            Unite unite = uniteService.findUniteByIdMetier(splitData[columns.get(idUniteColumn)], InvalidParameterException.class);

            Cible cible = null;
            if (columns.get(idCibleColumn) != null){
                cible = cibleService.findCibleByIdMetier(splitData[columns.get(idCibleColumn)], InvalidParameterException.class);
            }

            NumeroAmm numeroAmm;
            try {
                numeroAmm = numeroAmmService.findNumeroAmmByIdMetier(splitData[columns.get(codeAmmColumn)]);
            } catch (NotFoundException ex) {
                numeroAmm = NumeroAmm.builder()
                        .id(UUID.randomUUID())
                        .idMetier(splitData[columns.get(codeAmmColumn)])
                        .build();
                numeroAmm = numeroAmmRepository.save(numeroAmm);
            }

            DoseReference doseReference = DoseReference.builder()
                    .id(UUID.randomUUID())
                    .numeroAmm(numeroAmm)
                    .campagne(campagne)
                    .biocontrole(Integer.parseInt(splitData[columns.get(biocontroleColumn)]) == 1)
                    .culture(culture)
                    .cible(cible)
                    .segment(segment)
                    .unite(unite)
                    .build();
            if (!StringUtils.isEmpty(splitData[columns.get(doseRefColumn)].trim())) {
                doseReference.setDose(new BigDecimal(splitData[columns.get(doseRefColumn)].replace(",", ".")));
            }
            return doseReference;

        }
        return null;
    }
}
