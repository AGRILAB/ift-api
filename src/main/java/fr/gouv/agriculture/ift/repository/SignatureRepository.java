package fr.gouv.agriculture.ift.repository;

import fr.gouv.agriculture.ift.model.Signature;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SignatureRepository extends JpaRepository<Signature, UUID> {

    Signature findBySignature(String signature);
}
