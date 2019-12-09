package fr.gouv.agriculture.ift.service.impl;

import fr.gouv.agriculture.ift.controller.form.SegmentForm;
import fr.gouv.agriculture.ift.exception.InvalidParameterException;
import fr.gouv.agriculture.ift.exception.NotFoundException;
import fr.gouv.agriculture.ift.model.Segment;
import fr.gouv.agriculture.ift.repository.SegmentRepository;
import fr.gouv.agriculture.ift.service.SegmentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@CacheConfig(cacheNames = "segment")
public class SegmentServiceImpl implements SegmentService {

    @Autowired
    private SegmentRepository repository;

    @Override
    public List<Segment> findAllSegments() {
        log.debug("Get All Segments");
        return repository.findAll(new Sort(Sort.Direction.ASC, "libelle"));
    }

    @Override
    public Segment findSegmentById(UUID id) {
        return findSegmentById(id, null);
    }

    @Override
    public Segment findSegmentById(UUID id, Class<? extends Throwable> throwableClass) {
        log.debug("Get Segment by Id: {}", id.toString());
        Segment found = repository.findOne(id);

        if (found == null) {
            if (InvalidParameterException.class.equals(throwableClass)){
                throw new InvalidParameterException("Le segment ayant pour id " + id + " n'existe pas.");
            }
            throw new NotFoundException();
        } else {
            return found;
        }
    }


    @Override
    public Segment findSegmentByIdMetier(String idMetier) {
        return findSegmentByIdMetier(idMetier, null);
    }

    @Override
    public Segment findSegmentByIdMetier(String idMetier, Class<? extends Throwable> throwableClass) {
        log.debug("Get Segment by IdMetier: {}", idMetier.toString());
        Segment found = repository.findSegmentByIdMetier(idMetier);

        if (found == null) {
            if (InvalidParameterException.class.equals(throwableClass)){
                throw new InvalidParameterException("Le segment ayant pour id métier " + idMetier + " n'existe pas.");
            }
            throw new NotFoundException();
        } else {
            return found;
        }
    }

    @Override
    @Cacheable(key = "#root.methodName + '_' + #idMetier")
    public Segment findSegmentByIdMetierWithCache(String idMetier, Class<? extends Throwable> throwableClass) {
        return findSegmentByIdMetier(idMetier, throwableClass);
    }

    @CacheEvict(allEntries = true)
    public void cleanCache() { }

    @Override
    public Segment updateById(UUID id, SegmentForm segmentForm) {
        Segment found = repository.findOne(id);

        if (found == null) {
            throw new NotFoundException();
        } else {
            Segment segment = SegmentForm.mapToSegment(segmentForm);
            segment.setId(id);
            segment.setIdMetier(found.getIdMetier());
            segment.setDateCreation(found.getDateCreation());
            segment.setDateDerniereMaj(LocalDateTime.now());
            log.debug("Update Segment: {}", segment);
            return repository.save(segment);
        }
    }

}
