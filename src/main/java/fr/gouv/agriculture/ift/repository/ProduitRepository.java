package fr.gouv.agriculture.ift.repository;

import fr.gouv.agriculture.ift.model.Produit;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ProduitRepository extends JpaRepository<Produit, UUID> {

    Produit findByLibelle(String libelle);

    List<Produit> findProduitByNormalizedLibelleStartingWithOrNormalizedLibelleContainingOrderByLibelleAsc(
            String normalizedLibelleStart, String normalizedLibelleContains, Pageable pageable);
}