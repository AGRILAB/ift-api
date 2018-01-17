package fr.gouv.agriculture.ift.repository;

import fr.gouv.agriculture.ift.model.Traitement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TraitementRepository extends JpaRepository<Traitement, UUID> {

    Traitement findTraitementByIdMetier(String idMetier);
}
