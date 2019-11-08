package fr.gouv.agriculture.ift.service;

import fr.gouv.agriculture.ift.model.Certificat;
import fr.gouv.agriculture.ift.model.IftTraitement;
import fr.gouv.agriculture.ift.model.Signature;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.PublicKey;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

public interface CertificationService {

    String sign(IftTraitement ift) throws IOException, GeneralSecurityException;

    IftTraitement verify(String givenSignature, PublicKey publicKey) throws IOException;
    IftTraitement verify(Signature signature) throws IOException;

    X509Certificate getCertificate(Certificat certificat) throws CertificateException;
}
