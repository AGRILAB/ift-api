package fr.gouv.agriculture.ift.repository;

import fr.gouv.agriculture.ift.model.Bilan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface BilanRepository extends JpaRepository<Bilan, UUID> {
}
