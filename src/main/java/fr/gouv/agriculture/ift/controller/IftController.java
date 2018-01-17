package fr.gouv.agriculture.ift.controller;

import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.gouv.agriculture.ift.Constants;
import fr.gouv.agriculture.ift.exception.InvalidParameterException;
import fr.gouv.agriculture.ift.exception.NotFoundException;
import fr.gouv.agriculture.ift.exception.ServerException;
import fr.gouv.agriculture.ift.model.Bilan;
import fr.gouv.agriculture.ift.model.IftTraitement;
import fr.gouv.agriculture.ift.model.Parcelle;
import fr.gouv.agriculture.ift.model.SignedIftTraitement;
import fr.gouv.agriculture.ift.service.BilanIftService;
import fr.gouv.agriculture.ift.service.CertificationService;
import fr.gouv.agriculture.ift.service.IftService;
import fr.gouv.agriculture.ift.service.SignedIftTraitementService;
import fr.gouv.agriculture.ift.util.Views;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import sun.security.provider.X509Factory;

import javax.validation.Valid;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.util.List;

import static fr.gouv.agriculture.ift.Constants.*;

@Slf4j
@RestController
@RequestMapping(value = Constants.API_IFT_ROOT, produces = MediaType.APPLICATION_JSON_VALUE)
@Api(tags = {Constants.IFT}, description = "Ressources sur les indices de fréquence de traitement")
public class IftController {

    @Autowired
    private SignedIftTraitementService signedIftTraitementService;

    @Autowired
    private IftService iftService;

    @Autowired
    private CertificationService certificationService;

    @Autowired
    private BilanIftService bilanIftService;

    @Autowired
    private ObjectMapper objectMapper;

    @ApiOperation(value = "findIftTraitement", notes = "Retourne l'ift de traitement en fonction des données en entrée")
    @JsonView(Views.Public.class)
    @GetMapping(TRAITEMENT)
    public IftTraitement findIftTraitement(@ApiParam(value = "Identifiant métier de la campagne", required = true)
                                           @RequestParam String campagneIdMetier,
                                           @ApiParam(value = "Identifiant métier du numéro Amm")
                                           @RequestParam(required = false) String numeroAmmIdMetier,
                                           @ApiParam(value = "Identifiant métier de la culture", required = true)
                                           @RequestParam String cultureIdMetier,
                                           @ApiParam(value = "Identifiant métier de la cible")
                                           @RequestParam(required = false) String cibleIdMetier,
                                           @ApiParam(value = "Identifiant métier du traitement", required = true)
                                           @RequestParam String traitementIdMetier,
                                           @ApiParam(value = "Identifiant métier de l'unité")
                                           @RequestParam(required = false) String uniteIdMetier,
                                           @ApiParam(value = "Dose")
                                           @RequestParam(required = false) BigDecimal dose,
                                           @ApiParam(value = "Volume de bouillie")
                                           @RequestParam(required = false) BigDecimal volumeDeBouillie,
                                           @ApiParam(value = "Facteur de correction")
                                           @RequestParam(required = false) BigDecimal facteurDeCorrection) {
        return iftService.computeIftTraitement(campagneIdMetier, numeroAmmIdMetier, cultureIdMetier, cibleIdMetier, traitementIdMetier, uniteIdMetier, dose, volumeDeBouillie, facteurDeCorrection);
    }

    @ApiOperation(value = "findSignedIftTraitement", notes = "Retourne l'ift de traitement signé en fonction des données en entrée")
    @JsonView(Views.Public.class)
    @GetMapping(TRAITEMENT + CERTIFIE)
    public SignedIftTraitement findSignedIftTraitement(@ApiParam(value = "Identifiant métier de la campagne", required = true)
                                                       @RequestParam String campagneIdMetier,
                                                       @ApiParam(value = "Identifiant métier du numéro Amm")
                                                       @RequestParam(required = false) String numeroAmmIdMetier,
                                                       @ApiParam(value = "Identifiant métier de la culture", required = true)
                                                       @RequestParam String cultureIdMetier,
                                                       @ApiParam(value = "Identifiant métier de la cible")
                                                       @RequestParam(required = false) String cibleIdMetier,
                                                       @ApiParam(value = "Identifiant métier du traitement", required = true)
                                                       @RequestParam String traitementIdMetier,
                                                       @ApiParam(value = "Identifiant métier de l'unité")
                                                       @RequestParam(required = false) String uniteIdMetier,
                                                       @ApiParam(value = "Dose")
                                                       @RequestParam(required = false) BigDecimal dose,
                                                       @ApiParam(value = "Volume de bouillie")
                                                       @RequestParam(required = false) BigDecimal volumeDeBouillie,
                                                       @ApiParam(value = "Facteur de correction")
                                                       @RequestParam(required = false) BigDecimal facteurDeCorrection) {
        return signedIftTraitementService.getSignedIftTraitement(campagneIdMetier, numeroAmmIdMetier, cultureIdMetier, cibleIdMetier, traitementIdMetier, uniteIdMetier, dose, volumeDeBouillie, facteurDeCorrection);
    }

    @ApiOperation(value = "checkSignedIftTraitement", notes = "Vérifie la signature de l'ift de traitement")
    @JsonView(Views.Public.class)
    @GetMapping(TRAITEMENT + VERIFICATION_SIGNATURE)
    public IftTraitement checkSignedIftTraitement(@ApiParam(value = "Signature de l'ift du traitement", required = true)
                                                  @RequestParam String signature) {
        try {
            SignedIftTraitement signedIftTraitement = signedIftTraitementService.findSignedIftTraitementBySignature(signature);
            IftTraitement iftTraitement = signedIftTraitement.getIftTraitement();
            String serializedIftTraitement = objectMapper.writeValueAsString(iftTraitement);
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            String clePublique = signedIftTraitement.getClePublique().getCle().replaceAll(X509Factory.BEGIN_CERT, "").replaceAll(X509Factory.END_CERT, "");
            byte[] publicBytes = java.util.Base64.getDecoder().decode(clePublique);
            InputStream is = new ByteArrayInputStream(publicBytes);
            Certificate cert = cf.generateCertificate(is);
            String hashedData = certificationService.verify(serializedIftTraitement, signature, cert.getPublicKey());
            return objectMapper.readValue(hashedData, IftTraitement.class);
        } catch (IOException e) {
            e.printStackTrace();
            throw new ServerException("Erreur lors de la vérification de la signature de l'IFT");
        } catch (CertificateException e) {
            e.printStackTrace();
            throw new NotFoundException();
        } catch (NotFoundException e) {
            throw new InvalidParameterException("La signature fournie n'est pas valide.");
        }
    }

    @ApiOperation(value = "findBilanIft", notes = "Retourne le bilan d'une liste de traitements d'IFT")
    @JsonView(Views.Public.class)
    @PostMapping(value = BILAN, consumes = MediaType.APPLICATION_JSON_VALUE)
    public Bilan findBilanIft(@ApiParam(value = "Liste des traitements", required = true)
                              @RequestBody @Valid List<Parcelle> traitements) {

        return bilanIftService.getBilan(traitements);
    }
}
