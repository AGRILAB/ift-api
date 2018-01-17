package fr.gouv.agriculture.ift.service;

import fr.gouv.agriculture.ift.controller.form.SegmentForm;
import fr.gouv.agriculture.ift.model.Segment;

import java.util.List;
import java.util.UUID;

public interface SegmentService {

    Segment save(SegmentForm segmentForm);
    List<Segment> findAllSegments();
    Segment findSegmentById(UUID segmentId);
    Segment findSegmentById(UUID id, Class<? extends Throwable> throwableClass);
    Segment findSegmentByIdMetier(String idMetier);
    Segment findSegmentByIdMetier(String idMetier, Class<? extends Throwable> throwableClass);
    Segment updateById(UUID id, SegmentForm segmentForm);
    void delete(UUID id);

}
