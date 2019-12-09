package fr.gouv.agriculture.ift.repository;

import fr.gouv.agriculture.ift.model.Cible;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CibleRepository extends JpaRepository<Cible, UUID> {

    Cible findCibleByIdMetier(String idMetier);
    List<Cible> findCibleByNormalizedLibelleStartingWithOrNormalizedLibelleContainingOrIdMetierStartingWithOrderByLibelleAsc(String normalizedLibelleStart, String normalizedLibelleContains, String idMetierStart, Pageable pageable);
}
