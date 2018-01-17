package fr.gouv.agriculture.ift.service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.PublicKey;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;

public interface CertificationService {

    String hash(String toBeHashed);
    String sign(String toBeSigned) throws IOException, GeneralSecurityException;
    String verify(String givenData, String givenSignature, PublicKey publicKey) throws IOException;
    X509Certificate getCertificate() throws IOException;
    String getClePublique() throws CertificateEncodingException, IOException;
}
