package fr.gouv.agriculture.ift.service.impl;

import fr.gouv.agriculture.ift.dto.JwksDTO;
import fr.gouv.agriculture.ift.dto.JwksKeyDTO;
import fr.gouv.agriculture.ift.exception.ConflictException;
import fr.gouv.agriculture.ift.exception.NotFoundException;
import fr.gouv.agriculture.ift.model.Certificat;
import fr.gouv.agriculture.ift.repository.CertificatRepository;
import fr.gouv.agriculture.ift.service.CertificationService;
import fr.gouv.agriculture.ift.service.CertificatService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sun.security.provider.X509Factory;

import javax.xml.bind.DatatypeConverter;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPublicKey;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static fr.gouv.agriculture.ift.Constants.JWK_ALG;
import static fr.gouv.agriculture.ift.Constants.JWK_USE;

@Slf4j
@Service
public class CertificatServiceImpl implements CertificatService {

    @Autowired
    private CertificatRepository repository;

    @Autowired
    private CertificationService certificationService;

    @Override
    public Certificat findCertificatByCert(String cert) {
        log.debug("Get Certificat by Cle: {}", cert);
        Certificat found = repository.findCertificatByCert(cert);

        if (found == null) {
            throw new NotFoundException();
        } else {
            return found;
        }
    }

    @Override
    public Certificat save(Certificat certificat) {
        Certificat found = repository.findCertificatByCert(certificat.getCert());

        if (found == null) {
            certificat.setId(UUID.randomUUID());
            log.debug("Create Certificat: {}", certificat);
        } else {
            throw new ConflictException("La clé publique existe déjà.");
        }

        return repository.save(certificat);
    }

    @Override
    public JwksDTO getAllAsJwks(){

        List<Certificat> allKeys = repository.findAll();

        List<JwksKeyDTO> jwkKeys = allKeys.stream()
                .map(publicKey -> {
                    try {
                        String publicKeyString = publicKey.getCert().replaceAll(X509Factory.BEGIN_CERT, "").replaceAll(X509Factory.END_CERT, "");

                        X509Certificate cert = certificationService.getCertificate(publicKey);
                        RSAPublicKey pub = (RSAPublicKey) cert.getPublicKey();

                        ArrayList<String> x5c = new ArrayList<>();
                        x5c.add(publicKeyString);

                        String x5t = getCertificatThumbprint(cert);

                        return JwksKeyDTO.builder()
                                .alg(JWK_ALG)
                                .kty(pub.getAlgorithm())
                                .use(JWK_USE)
                                .x5c(x5c)
                                .n(Base64.getEncoder().encodeToString(pub.getModulus().toByteArray()))
                                .e(Base64.getEncoder().encodeToString(pub.getPublicExponent().toByteArray()))
                                .kid(x5t)
                                .x5t(x5t)
                                .build();
                    } catch (CertificateException e) {
                        e.printStackTrace();
                    }

                    return null;

                }).filter(jwkKey -> jwkKey != null)
                .collect(Collectors.toList());

        return JwksDTO.builder()
                .keys(jwkKeys)
                .build();
    }

    @Override
    public String getCertificatThumbprint(X509Certificate cert) throws CertificateEncodingException {
        String thumbprint = DatatypeConverter.printHexBinary(DigestUtils.sha1(cert.getEncoded()));
        return Base64.getUrlEncoder().encodeToString(thumbprint.getBytes());

    }

}
