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
    @Cacheable(key = "#root.methodName")
    public List<Segment> findAllSegments() {
        log.debug("Get All Segments");
        return repository.findAll();
    }

    @Override
    @Cacheable(key = "#root.methodName + '_' + #id")
    public Segment findSegmentById(UUID id) {
        return findSegmentById(id, null);
    }

    @Override
    @Cacheable(key = "#root.methodName + '_' + #id")
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
    @Cacheable(key = "#root.methodName + '_' + #idMetier")
    public Segment findSegmentByIdMetier(String idMetier) {
        return findSegmentByIdMetier(idMetier, null);
    }

    @Override
    @Cacheable(key = "#root.methodName + '_' + #idMetier")
    public Segment findSegmentByIdMetier(String idMetier, Class<? extends Throwable> throwableClass) {
        log.debug("Get Segment by IdMetier: {}", idMetier.toString());
        Segment found = repository.findSegmentByIdMetier(idMetier);

        if (found == null) {
            if (InvalidParameterException.class.equals(throwableClass)){
                throw new InvalidParameterException("La culture ayant pour id métier" + idMetier + " n'existe pas.");
            }
            throw new NotFoundException();
        } else {
            return found;
        }
    }

    @Override
    @CacheEvict(allEntries = true)
    public Segment save(SegmentForm segmentForm) {
        Segment newSegment = SegmentForm.mapToSegment(segmentForm);
        Segment found = repository.findSegmentByIdMetier(newSegment.getIdMetier());

        if (found == null) {
            newSegment.setId(UUID.randomUUID());
            log.debug("Create Segment: {}", newSegment);
        } else {
            newSegment.setId(found.getId());
            newSegment.setDateCreation(found.getDateCreation());
            newSegment.setDateDerniereMaj(LocalDateTime.now());
            log.debug("Update Segment: {}", newSegment);
        }

        return repository.save(newSegment);
    }

    @Override
    @CacheEvict(allEntries = true)
    public Segment updateById(UUID id, SegmentForm segmentForm) {
        Segment found = repository.findOne(id);

        if (found == null) {
            throw new NotFoundException();
        } else {
            Segment segment = SegmentForm.mapToSegment(segmentForm);
            segment.setId(id);
            segment.setDateCreation(found.getDateCreation());
            segment.setDateDerniereMaj(LocalDateTime.now());
            log.debug("Update Segment: {}", segment);
            return repository.save(segment);
        }
    }

    @Override
    @CacheEvict(allEntries = true)
    public void delete(UUID id) {
        log.debug("Delete Segment: {}", id);
        Segment found = repository.findOne(id);
        if (found == null) {
            throw new NotFoundException();
        } else {
            repository.delete(id);
        }
    }

}
