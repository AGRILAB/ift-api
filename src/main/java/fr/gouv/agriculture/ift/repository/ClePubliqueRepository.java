package fr.gouv.agriculture.ift.repository;

import fr.gouv.agriculture.ift.model.ClePublique;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ClePubliqueRepository extends JpaRepository<ClePublique, UUID> {
    ClePublique findClePubliqueByCle(String cle);
}
