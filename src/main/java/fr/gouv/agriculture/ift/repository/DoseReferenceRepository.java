package fr.gouv.agriculture.ift.repository;

import fr.gouv.agriculture.ift.model.DoseReference;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

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

    /**
     * Recherche une dose de référence par campagneIdMetier, numeroAmmIdMetier, cultureIdMetier, cibleIdMetier
     * @param campagneIdMetier
     * @param numeroAmmIdMetier
     * @param cultureIdMetier
     * @param cibleIdMetier
     * @return
     */
    DoseReference findOneDoseReferenceByCampagneIdMetierAndNumeroAmmIdMetierAndCultureIdMetierAndCibleIdMetier(String campagneIdMetier, String numeroAmmIdMetier, String cultureIdMetier, String cibleIdMetier);

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
}
