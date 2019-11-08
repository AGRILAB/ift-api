package fr.gouv.agriculture.ift.repository;

import fr.gouv.agriculture.ift.model.Certificat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CertificatRepository extends JpaRepository<Certificat, UUID> {
    Certificat findCertificatByCert(String cert);
}
