package fr.gouv.agriculture.ift.repository;

import fr.gouv.agriculture.ift.model.DoseReference;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.UUID;

public interface DoseReferenceRepository extends JpaRepository<DoseReference, UUID>, JpaSpecificationExecutor<DoseReference> {

    /**
     * Recherche une dose de référence par campagneId, numeroAmmId, cultureId, cibleId
     * @param campagneId
     * @param numeroAmmId
     * @param cultureId
     * @param cibleId
     * @return
     */
    DoseReference findOneDoseReferenceByCampagneIdAndNumeroAmmIdAndCultureIdAndCibleId(UUID campagneId, UUID numeroAmmId, UUID cultureId, UUID cibleId);

    List<DoseReference> findDoseReferenceByCampagneIdAndCulture_GroupeCulturesId(
            UUID campagneId, UUID groupeCulturesId);

    /**
     * Supprime toutes les doses de référence à la culture pour une campagne
     * @param campagneId
     */
    void deleteByCampagneIdAndCibleIdIsNull(UUID campagneId);

    /**
     * Supprime toutes les doses de référence à la cible pour une campagne
     * @param campagneId
     */
    void deleteByCampagneIdAndCibleIdIsNotNull(UUID campagneId);


    /**
     * Recherche une dose de référence par campagneId, numeroAmmId, cultureId
     * @param campagneId
     * @param numeroAmmId
     * @param cultureId
     * @return la première DoseReference qui match les critères
     */
	DoseReference findOneDoseReferenceByCampagneIdAndNumeroAmmIdAndCultureId(UUID campagneId, UUID numeroAmmId, UUID cultureId);
}
