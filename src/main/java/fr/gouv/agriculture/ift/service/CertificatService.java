package fr.gouv.agriculture.ift.service;

import fr.gouv.agriculture.ift.dto.JwksDTO;
import fr.gouv.agriculture.ift.model.Certificat;

import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;

public interface CertificatService {
    Certificat findCertificatByCert(String cert);
    Certificat save(Certificat certificat);

    JwksDTO getAllAsJwks();

    String getCertificatThumbprint(X509Certificate cert) throws CertificateEncodingException;
}
