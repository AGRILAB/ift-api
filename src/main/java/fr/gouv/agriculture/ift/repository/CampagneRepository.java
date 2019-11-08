package fr.gouv.agriculture.ift.repository;

import fr.gouv.agriculture.ift.model.Campagne;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CampagneRepository extends JpaRepository<Campagne, UUID> {

    Campagne findCampagneByIdMetier(String idMetier);
    Campagne findFirstByActive(Boolean active);
    Campagne findFirstByOrderByDateCreationDesc();
}
