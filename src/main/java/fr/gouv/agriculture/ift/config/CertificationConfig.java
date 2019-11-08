package fr.gouv.agriculture.ift.config;

import fr.gouv.agriculture.ift.model.Certificat;
import fr.gouv.agriculture.ift.service.ConfigurationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import sun.security.provider.X509Factory;

import javax.xml.bind.DatatypeConverter;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.PrivateKey;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;

import static fr.gouv.agriculture.ift.Constants.*;
import static java.security.KeyStore.getInstance;

@Configuration
@Slf4j
public class CertificationConfig {

    private String keystore;
    private String keystorePassword;
    private String certificateName;

    @Autowired
    public CertificationConfig(ConfigurationService configurationService) {
        keystore = configurationService.getValue(CONF_SECURITY_KEYSTORE_FILE);
        keystorePassword = configurationService.getValue(CONF_SECURITY_KEYSTORE_PASSWORD);
        certificateName = configurationService.getValue(CONF_SECURITY_KEYSTORE_CERTIFICATE_NAME);
    }

    @Bean(name = "signaturePrivateKey")
    public PrivateKey getPrivateKey() throws IOException, GeneralSecurityException {
        KeyStore keyStore = getKeyStore();
        return (PrivateKey) keyStore.getKey(certificateName, keystorePassword.toCharArray());
    }

    @Bean(name = "signatureX509Certificate")
    public X509Certificate getCertificate() throws IOException {
        try {
            KeyStore keyStore = getKeyStore();
            return (X509Certificate) keyStore.getCertificate(certificateName);
        } catch (KeyStoreException e) {
            log.error(e.getMessage());
        }
        return null;
    }

    @Bean(name = "signatureCertificate")
    public Certificat getClePublique() throws CertificateEncodingException, IOException {
        X509Certificate certificate = getCertificate();
        String pemCertPre = DatatypeConverter.printBase64Binary(certificate.getEncoded());
        String pemCert = X509Factory.BEGIN_CERT + pemCertPre + X509Factory.END_CERT;

        Certificat certificat = Certificat.builder()
                .cert(pemCert)
                .build();
        return certificat;
    }

    private KeyStore getKeyStore() {
        try {
            KeyStore keyStore = getInstance(PKCS_12);
            keyStore.load(getClass().getClassLoader().getResourceAsStream(keystore), keystorePassword.toCharArray());
            return keyStore;
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException("An error occurred while loading keystore.");
        }
    }


}
