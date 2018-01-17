package fr.gouv.agriculture.ift.repository;

import fr.gouv.agriculture.ift.model.Segment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SegmentRepository extends JpaRepository<Segment, UUID> {

    Segment findSegmentByIdMetier(String idMetier);
}
