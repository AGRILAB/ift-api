package fr.gouv.agriculture.ift.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.gouv.agriculture.ift.exception.InvalidParameterException;
import fr.gouv.agriculture.ift.service.CertificationService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import sun.security.provider.X509Factory;

import javax.xml.bind.DatatypeConverter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;

import static java.security.KeyStore.getInstance;

@Slf4j
@Service
public class CertificationServiceImpl implements CertificationService {

    public static final String PKCS_12 = "PKCS12";

    public static final String CLAIM = "signature";

    @Value("${security.keystore.file}")
    private String keystore;

    @Value("${security.keystore.password}")
    private String keystorePassword;

    @Value("${security.keystore.certificate.name}")
    private String certificateName;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public String hash(String toBeHashed) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedhash = digest.digest(toBeHashed.getBytes(StandardCharsets.UTF_8));

            StringBuffer hexString = new StringBuffer();
            for (int i = 0; i < encodedhash.length; i++) {
                String hex = Integer.toHexString(0xff & encodedhash[i]);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public String sign(String toBeSigned) throws IOException, GeneralSecurityException {
        PrivateKey privateKey = getPrivateKey();
        return Jwts.builder()
                .claim(CLAIM, toBeSigned)
                .signWith(SignatureAlgorithm.RS256, privateKey)
                .compact();
    }

    @Override
    public String verify(String savedData, String givenSignature, PublicKey publicKey) throws IOException {
        try {
            Jws<Claims> claims = Jwts.parser()
                    .setSigningKey(publicKey).parseClaimsJws(givenSignature);

            String hashedSignedData = claims.getBody().get(CLAIM, String.class);
            String hashedSavedData = hash(savedData);
            if (hashedSignedData.equals(hashedSavedData)) {
                return savedData;
            } else {
                throw new InvalidParameterException("L'IFT n'est pas valide.");
            }
        } catch (Exception e) {
            throw new InvalidParameterException("La signature fournie n'est pas valide.");
        }
    }

    private PrivateKey getPrivateKey() throws IOException, GeneralSecurityException {
        KeyStore keyStore = getKeyStore();
        return (PrivateKey) keyStore.getKey(certificateName, keystorePassword.toCharArray());
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

    @Override
    public X509Certificate getCertificate() throws IOException {
        try {
            KeyStore keyStore = getKeyStore();
            return (X509Certificate) keyStore.getCertificate(certificateName);
        } catch (KeyStoreException e) {
            log.error(e.getMessage());
        }
        return null;
    }

    @Override
    public String getClePublique() throws CertificateEncodingException, IOException {
        X509Certificate certificate = getCertificate();
        String pemCertPre = DatatypeConverter.printBase64Binary(certificate.getEncoded());
        String pemCert = X509Factory.BEGIN_CERT + pemCertPre + X509Factory.END_CERT;
        return pemCert;
    }
}
