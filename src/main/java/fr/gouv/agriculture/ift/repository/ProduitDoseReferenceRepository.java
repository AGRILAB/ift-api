package fr.gouv.agriculture.ift.repository;

import fr.gouv.agriculture.ift.model.ProduitDoseReference;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ProduitDoseReferenceRepository extends JpaRepository<ProduitDoseReference, UUID> {
}