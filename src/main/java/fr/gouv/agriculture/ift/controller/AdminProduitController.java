package fr.gouv.agriculture.ift.controller;

import com.fasterxml.jackson.annotation.JsonView;
import fr.gouv.agriculture.ift.Constants;
import fr.gouv.agriculture.ift.controller.form.ProduitForm;
import fr.gouv.agriculture.ift.dto.WarningDTO;
import fr.gouv.agriculture.ift.exception.InvalidBindingEntityException;
import fr.gouv.agriculture.ift.exception.InvalidParameterException;
import fr.gouv.agriculture.ift.exception.NotFoundException;
import fr.gouv.agriculture.ift.model.Campagne;
import fr.gouv.agriculture.ift.model.Produit;
import fr.gouv.agriculture.ift.service.CampagneService;
import fr.gouv.agriculture.ift.service.ProduitService;
import fr.gouv.agriculture.ift.util.Views;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.UUID;

import static fr.gouv.agriculture.ift.Constants.CSV;
import static fr.gouv.agriculture.ift.Constants.PRODUITS;

@Slf4j
@RestController
@RequestMapping(value = Constants.API_ADMIN_ROOT, produces = MediaType.APPLICATION_JSON_VALUE)
@Api(tags = {Constants.ADMIN}, description = "Ressources sur les produits pour les administrateurs")
public class AdminProduitController {

    @Autowired
    CampagneService campagneService;

    @Autowired
    ProduitService produitService;

    @ApiOperation(hidden = true, value = "addProduits", notes = "Ajout des produits avec un fichier CSV")
    @JsonView(Views.ExtendedPublic.class)
    @PostMapping(value = PRODUITS + CSV)
    public WarningDTO addProduits(@ApiParam(value = "Identification de la campagne", required = true)
                                        @RequestParam(value = "campagneIdMetier") String campagneIdMetier,
                                  HttpServletRequest request) throws IOException, ServletException {
        InputStream inputStream = request.getPart("file").getInputStream();
        try {
            Campagne campagne = campagneService.findCampagneByIdMetier(campagneIdMetier);
            produitService.deleteValiditeProduitByCampagne(campagne);
            String warningMessage = produitService.addProduits(campagne, inputStream);
            return WarningDTO.builder().message(warningMessage).build();
        } catch (NotFoundException ex) {
            throw new InvalidParameterException("La campagne ayant pour id métier " + campagneIdMetier + " n'existe pas.");
        }
    }

    @ApiOperation(hidden = true, value = "createProduit", notes = "Ajout d'un produit")
    @JsonView(Views.ExtendedPublic.class)
    @PostMapping(value = PRODUITS, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(code = HttpStatus.CREATED)
    public Produit createProduit(@ApiParam(value = "Produit", required = true)
                                     @RequestBody @Valid ProduitForm produitForm,
                                     BindingResult result) {
        if (result.hasErrors()) {
            throw new InvalidBindingEntityException(result);
        }
        return produitService.save(produitForm);
    }

    @ApiOperation(hidden = true, value = "updateProduit", notes = "Modification d'un produit")
    @JsonView(Views.ExtendedPublic.class)
    @PutMapping(value = PRODUITS + "/{produitId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Produit updateProduit(@ApiParam(value = "Identification du produit", required = true)
                                     @PathVariable UUID produitId,
                                     @ApiParam(value = "Produit", required = true)
                                     @RequestBody @Valid ProduitForm produitForm,
                                     BindingResult result) {
        if (result.hasErrors()) {
            throw new InvalidBindingEntityException(result);
        }
        return produitService.updateById(produitId, produitForm);
    }

    @ApiOperation(hidden = true, value = "deleteProduit", notes = "Suppression d'un produit")
    @JsonView(Views.ExtendedPublic.class)
    @DeleteMapping(PRODUITS + "/{produitId}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void deleteProduit(@ApiParam(value = "Identification du produit", required = true)
                                @PathVariable UUID produitId) {
        produitService.delete(produitId);
    }

    @ApiOperation(hidden = true, value = "findAllProduits", notes = "Retourne la liste des produits")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", dataType = "int", paramType = "query",
                    value = "Page de résultats à récupérer (0 par défaut)."),
            @ApiImplicitParam(name = "size", dataType = "int", paramType = "query",
                    value = "Nombre de résultats par page (200 par défaut, max 2000).")
    })
    @JsonView(Views.ExtendedPublic.class)
    @GetMapping(PRODUITS)
    @ResponseStatus(code = HttpStatus.PARTIAL_CONTENT)
    public List<Produit> findAllProduits(@ApiParam(value = "Identifiant métier de la campagne")
                                                  @RequestParam(value = "campagneIdMetier", required = false) String campagneIdMetier,
                                                  @ApiParam(value = "Identifiant métier de la culture")
                                                  @RequestParam(value = "cultureIdMetier", required = false) String cultureIdMetier,
                                                  @ApiParam(value = "Identifiant métier de la cible")
                                                  @RequestParam(value = "cibleIdMetier", required = false) String cibleIdMetier,
                                                  @ApiParam(value = "Filtre sur le libellé du produit")
                                                  @RequestParam(value = "filtre", required = false) String filtre,
                                                  @PageableDefault(page= 0, value = 200) Pageable pageable) {

        return produitService.findProduits(campagneIdMetier, cultureIdMetier, cibleIdMetier, filtre, pageable);

    }
}
