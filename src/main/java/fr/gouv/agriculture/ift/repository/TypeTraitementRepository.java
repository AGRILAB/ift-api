package fr.gouv.agriculture.ift.repository;

import fr.gouv.agriculture.ift.model.TypeTraitement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TypeTraitementRepository extends JpaRepository<TypeTraitement, UUID> {

    TypeTraitement findTypeTraitementByIdMetier(String idMetier);
}
