package fr.gouv.agriculture.ift.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import fr.gouv.agriculture.ift.exception.InvalidParameterException;
import fr.gouv.agriculture.ift.exception.ServerException;
import fr.gouv.agriculture.ift.model.Certificat;
import fr.gouv.agriculture.ift.model.IftTraitement;
import fr.gouv.agriculture.ift.model.Signature;
import fr.gouv.agriculture.ift.service.CertificationService;
import fr.gouv.agriculture.ift.service.CertificatService;
import fr.gouv.agriculture.ift.util.Views;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.TextCodec;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import sun.security.provider.X509Factory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import static fr.gouv.agriculture.ift.Constants.JWT_KID_PARAM;

@Slf4j
@Service
public class CertificationServiceImpl implements CertificationService {

    @Autowired
    CertificatService certificatService;

    @Autowired
    @Qualifier("signaturePrivateKey")
    PrivateKey privateKey;

    @Autowired
    @Qualifier("signatureX509Certificate")
    X509Certificate certificate;

    CertificateFactory cf;

    private ObjectMapper oldObjectMapper;
    private ObjectMapper newObjectMapper;

    CertificationServiceImpl() throws CertificateException {

        cf = CertificateFactory.getInstance("X.509");

        newObjectMapper = new ObjectMapper();
        newObjectMapper.registerModule(new JavaTimeModule());
        DateFormat newDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        newObjectMapper.setDateFormat(newDateFormat);

        oldObjectMapper = new ObjectMapper();
        oldObjectMapper.registerModule(new JavaTimeModule());

    }

    @Override
    public String sign(IftTraitement ift) throws IOException, GeneralSecurityException {

        String serializedIft = this.newObjectMapper
                .writerWithView(Views.Public.class)
                .writeValueAsString(ift);

        String kid = certificatService.getCertificatThumbprint(certificate);

        return Jwts.builder()
                .setHeaderParam(JWT_KID_PARAM, kid)
                .setPayload(serializedIft)
                .signWith(SignatureAlgorithm.RS256, privateKey)
                .compact();
    }

    @Override
    public IftTraitement verify(String givenSignature, PublicKey publicKey) throws IOException {
        try {
            Jws<Claims> jws = Jwts.parser()
                    .setSigningKey(publicKey).parseClaimsJws(givenSignature);

            //Old way of encoding payload in signature
            String payload = jws.getBody().get("signature", String.class);

            if (payload == null){
                //New way of encoding payload in signature
                String base64UrlEncodedPayload = givenSignature.substring(givenSignature.indexOf('.') + 1,
                        givenSignature.length());
                payload = TextCodec.BASE64URL.decodeToString(base64UrlEncodedPayload);

                return newObjectMapper.readValue(payload, IftTraitement.class);
            }else {
                //For compatibility reasons with existing signatures
                return oldObjectMapper.readValue(payload, IftTraitement.class);
            }


        } catch (Exception e) {
            log.error("La signature fournie n'est pas valide", e);
            throw new InvalidParameterException("La signature fournie n'est pas valide.");
        }
    }

    @Override
    public IftTraitement verify(Signature signature) throws IOException {
        try {
            Certificate cert = getCertificate(signature.getCertificat());
            return verify(signature.getSignature(), cert.getPublicKey());
        }catch (CertificateException e){
            log.error("Erreur lors de la vérification de la signature", e);
            throw new ServerException("Erreur lors de la vérification de la signature");
        }
    }

    @Override
    public X509Certificate getCertificate(Certificat certificat) throws CertificateException {

        String publicKey = certificat.getCert().replaceAll(X509Factory.BEGIN_CERT, "").replaceAll(X509Factory.END_CERT, "");

        byte[] publicBytes = java.util.Base64.getDecoder().decode(publicKey);
        InputStream is = new ByteArrayInputStream(publicBytes);

        return (X509Certificate) cf.generateCertificate(is);
    }

}
