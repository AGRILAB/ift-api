package fr.gouv.agriculture.ift.repository;

import fr.gouv.agriculture.ift.model.SignedIftTraitement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SignedIftTraitementRepository extends JpaRepository<SignedIftTraitement, UUID> {
    SignedIftTraitement findFirstSignedIftTraitementBySignature(String signature);
}
