package fr.gouv.agriculture.ift.repository;

import fr.gouv.agriculture.ift.model.Unite;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UniteRepository extends JpaRepository<Unite, UUID> {

    Unite findUniteByIdMetier(String idMetier);
}
