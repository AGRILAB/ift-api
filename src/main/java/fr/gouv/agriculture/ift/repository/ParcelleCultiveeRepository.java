package fr.gouv.agriculture.ift.repository;

import fr.gouv.agriculture.ift.model.ParcelleCultivee;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ParcelleCultiveeRepository extends JpaRepository<ParcelleCultivee, UUID> {
}
