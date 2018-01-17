package fr.gouv.agriculture.ift.repository;

import fr.gouv.agriculture.ift.model.Avis;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AvisRepository extends JpaRepository<Avis, UUID> {

    List<Avis> findAvisByNote(Integer note);
}
