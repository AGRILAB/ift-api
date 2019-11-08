package fr.gouv.agriculture.ift.controller;

import com.fasterxml.jackson.annotation.JsonView;
import fr.gouv.agriculture.ift.Constants;
import fr.gouv.agriculture.ift.model.NumeroAmm;
import fr.gouv.agriculture.ift.model.Produit;
import fr.gouv.agriculture.ift.service.NumeroAmmService;
import fr.gouv.agriculture.ift.service.ProduitService;
import fr.gouv.agriculture.ift.util.Views;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static fr.gouv.agriculture.ift.Constants.CSV;

@Slf4j
@RestController
@RequestMapping(value = Constants.API_PRODUITS_ROOT, produces = MediaType.APPLICATION_JSON_VALUE)
@Api(tags = {Constants.PRODUITS}, description = "Référentiel des noms commerciaux des produits phytosanitaires")
public class ProduitController {

    @Autowired
    ProduitService produitService;

    @Autowired
    NumeroAmmService numeroAmmService;

    @ApiOperation(value = "findAllProduits", notes = "Retourne la liste des produits")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", dataType = "int", paramType = "query",
                    value = "Page de résultats à récupérer (0 par défaut)."),
            @ApiImplicitParam(name = "size", dataType = "int", paramType = "query",
                    value = "Nombre de résultats par page (200 par défaut, max 2000).")
    })
    @JsonView(Views.Public.class)
    @GetMapping
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

    @ApiOperation(value = "getNumeroAmmByProduitAndCampagne", notes = "Retourne le numero AMM du produit pour une campagne donnée")
    @ApiResponses(value = {
            @ApiResponse(code = 404, message = "Not Found")
    })
    @JsonView(Views.Public.class)
    @GetMapping("/{libelle}/numero-amm/{campagne}")
    public List<NumeroAmm> getNumerosAmmByCampagne(@ApiParam(value = "Libelle du produit", required = true)
                                                        @PathVariable String libelle,
                                                   @ApiParam(value = "Identifiant métier de la campagne", required = true)
                                                        @PathVariable String campagne,
                                                   @ApiParam(value = "Identifiant métier de la culture")
                                                        @RequestParam(value = "cultureIdMetier", required = false) String cultureIdMetier,
                                                   @ApiParam(value = "Identifiant métier de la cible")
                                                        @RequestParam(value = "cibleIdMetier", required = false) String cibleIdMetier) {
        return numeroAmmService.findNumerosAmmByCampagneAndCultureAndProduitAndCible(campagne, cultureIdMetier, libelle, cibleIdMetier);
    }

    @ApiOperation(value = "findProduitsByCampagneAsCSV", notes = "Retourne la liste des produits au format CSV pour une campagne donnée")
    @JsonView(Views.Public.class)
    @GetMapping(value = "/{campagne}" + CSV, produces = "text/csv")
    public HttpEntity<String> findProduitsByCampagneAsCSV(@ApiParam(value = "Identifiant métier de la campagne", required = true)
                                           @PathVariable String campagne) {
        HttpHeaders header = new HttpHeaders();
        header.set(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=Produits" + campagne + ".csv");

        return new HttpEntity<>(produitService.findProduitsByCampagneAsCSV(campagne), header);
    }
}
