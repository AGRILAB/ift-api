package fr.gouv.agriculture.ift.repository;

import fr.gouv.agriculture.ift.model.Culture;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CultureRepository extends JpaRepository<Culture, UUID> {

    Culture findCultureByIdMetier(String idMetier);
    List<Culture> findCultureByNormalizedLibelleStartingWithOrNormalizedLibelleContainingOrderByLibelleAsc(
            String normalizedLibelleStart, String normalizedLibelleContains, Pageable pageable);
    List<Culture> findCultureByGroupeCulturesIdMetierOrderByLibelleAsc(String groupeCulturesIdMetier);
}
