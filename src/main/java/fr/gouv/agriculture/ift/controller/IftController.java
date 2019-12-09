package fr.gouv.agriculture.ift.controller;

import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.gouv.agriculture.ift.Constants;
import fr.gouv.agriculture.ift.dto.BilanCertifieDTO;
import fr.gouv.agriculture.ift.dto.IftTraitementSigneDTO;
import fr.gouv.agriculture.ift.dto.ParcelleCultiveeListDTO;
import fr.gouv.agriculture.ift.exception.InvalidBindingEntityException;
import fr.gouv.agriculture.ift.exception.InvalidParameterException;
import fr.gouv.agriculture.ift.exception.ServerException;
import fr.gouv.agriculture.ift.model.BilanDTO;
import fr.gouv.agriculture.ift.model.IftTraitement;
import fr.gouv.agriculture.ift.model.IftTraitementSigne;
import fr.gouv.agriculture.ift.service.*;
import fr.gouv.agriculture.ift.util.Views;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.UUID;

import static fr.gouv.agriculture.ift.Constants.*;

@Slf4j
@RestController
@RequestMapping(value = Constants.API_IFT_ROOT, produces = MediaType.APPLICATION_JSON_VALUE)
@Api(tags = {Constants.IFT}, description = "Calculer, certifier et vérifier un IFT & éditer un bilan d'IFT")
public class IftController {

    @Autowired
    private IftTraitementService iftTraitementService;

    @Autowired
    private IftService iftService;

    @Autowired
    private CertificationService certificationService;

    @Autowired
    private BilanIftService bilanIftService;

    @Autowired
    private ConfigurationService configurationService;

    @Autowired
    private ObjectMapper objectMapper;

    @ApiOperation(value = "findIftTraitement", notes = "Vérifier l'IFT d'un traitement à partir de son id")
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "Bad Request")
    })
    @JsonView(Views.Public.class)
    @GetMapping(TRAITEMENT + CERTIFIE + "/{id}")
    public IftTraitement findIftTraitement(@ApiParam(value = "Identifiant du traitement signé", required = true)
                                               @PathVariable UUID id) {
        try {
            return iftTraitementService.findIftTraitement(id);
        } catch (IOException e) {
            e.printStackTrace();
            throw new ServerException("Erreur lors de la récupération de l'IFT signé");
        }
    }

    @ApiOperation(value = "computeIftTraitement", notes = "Calcul l'IFT d'un traitement en fonction des données en entrée")
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "Bad Request")
    })
    @JsonView(Views.Public.class)
    @GetMapping(TRAITEMENT)
    public IftTraitement computeIftTraitement(@ApiParam(value = "Identifiant métier de la campagne", required = true)
                                           @RequestParam String campagneIdMetier,
                                           @ApiParam(value = "Identifiant métier du numéro Amm")
                                           @RequestParam(required = false) String numeroAmmIdMetier,
                                           @ApiParam(value = "Identifiant métier de la culture", required = true)
                                           @RequestParam String cultureIdMetier,
                                           @ApiParam(value = "Identifiant métier de la cible")
                                           @RequestParam(required = false) String cibleIdMetier,
                                           @ApiParam(value = "Identifiant métier du type de traitement", required = true)
                                           @RequestParam String typeTraitementIdMetier,
                                           @ApiParam(value = "Identifiant métier de l'unité")
                                           @RequestParam(required = false) String uniteIdMetier,
                                           @ApiParam(value = "Dose")
                                           @RequestParam(required = false) BigDecimal dose,
                                           @ApiParam(value = "Volume de bouillie")
                                           @RequestParam(required = false) BigDecimal volumeDeBouillie,
                                           @ApiParam(value = "Facteur de correction")
                                           @RequestParam(required = false) BigDecimal facteurDeCorrection) {
        return iftService.computeIftTraitement(campagneIdMetier, numeroAmmIdMetier, cultureIdMetier, cibleIdMetier, typeTraitementIdMetier, uniteIdMetier, dose, volumeDeBouillie, facteurDeCorrection);
    }

    @ApiOperation(value = "computeSignedIftTraitement", notes = "Certifie l'IFT d'un traitement en fonction des données en entrée")
    @JsonView(Views.Public.class)
    @GetMapping(TRAITEMENT + CERTIFIE)
    public IftTraitementSigneDTO computeIftTraitementSigne(@ApiParam(value = "Identifiant métier de la campagne", required = true)
                                                       @RequestParam String campagneIdMetier,
                                                           @ApiParam(value = "Identifiant métier du numéro Amm")
                                                       @RequestParam(required = false) String numeroAmmIdMetier,
                                                           @ApiParam(value = "Identifiant métier de la culture", required = true)
                                                       @RequestParam String cultureIdMetier,
                                                           @ApiParam(value = "Identifiant métier de la cible")
                                                       @RequestParam(required = false) String cibleIdMetier,
                                                           @ApiParam(value = "Identifiant métier du type de traitement", required = true)
                                                       @RequestParam String typeTraitementIdMetier,
                                                           @ApiParam(value = "Identifiant métier de l'unité")
                                                       @RequestParam(required = false) String uniteIdMetier,
                                                           @ApiParam(value = "Dose")
                                                       @RequestParam(required = false) BigDecimal dose,
                                                           @ApiParam(value = "Volume de bouillie")
                                                       @RequestParam(required = false) BigDecimal volumeDeBouillie,
                                                           @ApiParam(value = "Facteur de correction")
                                                       @RequestParam(required = false) BigDecimal facteurDeCorrection,
                                                           @ApiParam(value = "Libellé du produit")
                                                       @RequestParam(required = false) String produitLibelle,
                                                           @ApiParam(value = "Commentaire")
                                                       @RequestParam(required = false) String commentaire) {
        IftTraitementSigne iftTraitementSigne = iftTraitementService.getIftTraitementSigne(campagneIdMetier, numeroAmmIdMetier, cultureIdMetier, cibleIdMetier, typeTraitementIdMetier, uniteIdMetier, dose, volumeDeBouillie, facteurDeCorrection, produitLibelle, commentaire);

        return IftTraitementSigneDTO.builder()
                .id(iftTraitementSigne.getSignature().getId())
                .iftTraitement(iftTraitementSigne.getIftTraitement())
                .signature(iftTraitementSigne.getSignature().getSignature())
                .build();
    }

    @ApiOperation(value = "generateIftTraitementSignePDF", notes = "Edite et certifie l'IFT d'un traitement en fonction des données en entrée")
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "Bad Request")
    })
    @JsonView(Views.Public.class)
    @GetMapping(value = TRAITEMENT + CERTIFIE + PDF, produces = "application/pdf")
    @ResponseStatus(code = HttpStatus.OK)
    public byte[] generateIftTraitementSignePDF(@ApiParam(value = "Identifiant métier de la campagne", required = true)
                                        @RequestParam String campagneIdMetier,
                                        @ApiParam(value = "Identifiant métier du numéro Amm")
                                        @RequestParam(required = false) String numeroAmmIdMetier,
                                        @ApiParam(value = "Identifiant métier de la culture", required = true)
                                        @RequestParam String cultureIdMetier,
                                        @ApiParam(value = "Identifiant métier de la cible")
                                        @RequestParam(required = false) String cibleIdMetier,
                                        @ApiParam(value = "Identifiant métier du type de traitement", required = true)
                                        @RequestParam String typeTraitementIdMetier,
                                        @ApiParam(value = "Identifiant métier de l'unité")
                                        @RequestParam(required = false) String uniteIdMetier,
                                        @ApiParam(value = "Dose")
                                        @RequestParam(required = false) BigDecimal dose,
                                        @ApiParam(value = "Volume de bouillie")
                                        @RequestParam(required = false) BigDecimal volumeDeBouillie,
                                        @ApiParam(value = "Facteur de correction")
                                        @RequestParam(required = false) BigDecimal facteurDeCorrection,
                                        @ApiParam(value = "Libellé du produit")
                                        @RequestParam(required = false) String produitLibelle,
                                        @ApiParam(value = "Commentaire")
                                        @RequestParam(required = false) String commentaire,
                                        @ApiParam(value = "Titre du PDF")
                                        @RequestParam(required = false) String titre) {
        if (titre != null && titre.length() > 80) {
            throw new InvalidParameterException("Le titre doit faire moins de 80 caractères");
        }

        IftTraitementSigne iftTraitementSigne = iftTraitementService.getIftTraitementSigne(campagneIdMetier,
                numeroAmmIdMetier,
                cultureIdMetier,
                cibleIdMetier,
                typeTraitementIdMetier,
                uniteIdMetier,
                dose,
                volumeDeBouillie,
                facteurDeCorrection,
                produitLibelle,
                commentaire);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        iftTraitementService.getIftTraitementSignePDF(byteArrayOutputStream, titre, iftTraitementSigne);

         return byteArrayOutputStream.toByteArray();
    }

    @ApiOperation(value = "findBilanIft", notes = "Retourne le bilan d'une liste de traitements d'IFT")
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "Bad Request")
    })
    @JsonView(Views.Public.class)
    @PostMapping(value = BILAN, consumes = MediaType.APPLICATION_JSON_VALUE)
    public BilanDTO findBilanIft(@ApiParam(value = "Liste des traitements", required = true)
                              @RequestBody @Valid ParcelleCultiveeListDTO parcellesCultivees,
                                 BindingResult result) {

        if (result.hasErrors()) {
            throw new InvalidBindingEntityException(result);
        }

        return bilanIftService.getBilan(parcellesCultivees.getParcellesCultivees());
    }

    @ApiOperation(value = "findBilanIftCertifie", notes = "Certifie la liste des traitements d'un bilan et retourne le bilan et l'URL de vérification")
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "Bad Request")
    })
    @JsonView(Views.Public.class)
    @PostMapping(value = BILAN + CERTIFIE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public BilanCertifieDTO findBilanIftCertifie(
            @ApiParam(value = "Identifiant métier de la campagne", required = true)
            @RequestParam String campagneIdMetier,
            @ApiParam(value = "Liste des traitements", required = true)
                                 @RequestBody @Valid ParcelleCultiveeListDTO parcellesCultivees,
                                 BindingResult result) {

        if (result.hasErrors()) {
            throw new InvalidBindingEntityException(result);
        }

        return bilanIftService.getBilanCertifie(parcellesCultivees.getParcellesCultivees(), campagneIdMetier);
    }

    @ApiOperation(value = "findBilanIftPDF", notes = "Edite le bilan d'une liste de traitements d'IFT")
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "Bad Request")
    })
    @JsonView(Views.Public.class)
    @PostMapping(value = BILAN + PDF, consumes = MediaType.APPLICATION_JSON_VALUE, produces = "application/pdf")
    public byte[] findBilanIftPDF(@ApiParam(value = "Identifiant métier de la campagne", required = true)
                                  @RequestParam String campagneIdMetier,
                                  @ApiParam(value = "Titre du PDF")
                                  @RequestParam(required = false) String titre,
                                  @ApiParam(value = "Liste des traitements", required = true)
                                  @RequestBody @Valid ParcelleCultiveeListDTO parcellesCultivees,
                                  BindingResult result) {

        if (result.hasErrors()) {
            throw new InvalidBindingEntityException(result);
        }
        if (titre != null && titre.length() > 80) {
            throw new InvalidParameterException("Le titre doit faire moins de 80 caractères");
        }

        BilanCertifieDTO bilan = bilanIftService.getBilanCertifie(parcellesCultivees.getParcellesCultivees(), campagneIdMetier);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bilanIftService.getBilanPDF(byteArrayOutputStream, titre, bilan);

        return byteArrayOutputStream.toByteArray();
    }

    @ApiOperation(value = "checkBilan", notes = "Vérifie le bilan d'une liste de traitements d'IFT")
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "Bad Request")
    })
    @JsonView(Views.Public.class)
    @GetMapping(value = BILAN + "/verifier" + "/{id}")
    public BilanDTO checkBilan(@ApiParam(value = "Id du bilan", required = true)
                                   @PathVariable UUID id) {
        return bilanIftService.getBilanById(id);
    }
}
