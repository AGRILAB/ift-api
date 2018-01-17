package fr.gouv.agriculture.ift.repository;

import fr.gouv.agriculture.ift.model.Avertissement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AvertissementRepository extends JpaRepository<Avertissement, UUID> {

    Avertissement findAvertissementByIdMetier(String idMetier);
}
