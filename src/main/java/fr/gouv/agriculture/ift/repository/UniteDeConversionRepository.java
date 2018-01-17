package fr.gouv.agriculture.ift.repository;

import fr.gouv.agriculture.ift.model.UniteDeConversion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UniteDeConversionRepository extends JpaRepository<UniteDeConversion, UUID> {

    UniteDeConversion findUniteDeConversionById(String id);
}
