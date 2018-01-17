package fr.gouv.agriculture.ift.repository;

import fr.gouv.agriculture.ift.model.ValiditeProduit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ValiditeProduitRepository extends JpaRepository<ValiditeProduit, UUID> {

    ValiditeProduit findByProduitIdAndCampagneIdAndNumeroAmmId(UUID produitId, UUID campagneId, UUID numeroAmmId);
    List<ValiditeProduit> findByProduitLibelleAndCampagneIdMetier(String produitLibelle, String campagneIdMetier);
    List<ValiditeProduit> findByNumeroAmmIdInOrderByNumeroAmmIdMetierAscProduitLibelleAsc(List<UUID> numeroAmmIds);
    void deleteByCampagneId(UUID campagneId);
}
