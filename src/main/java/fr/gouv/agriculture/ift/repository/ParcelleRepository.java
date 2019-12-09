package fr.gouv.agriculture.ift.repository;

import fr.gouv.agriculture.ift.model.Parcelle;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ParcelleRepository extends JpaRepository<Parcelle, UUID> {
}
