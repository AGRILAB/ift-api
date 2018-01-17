package fr.gouv.agriculture.ift.controller;

import com.fasterxml.jackson.annotation.JsonView;
import fr.gouv.agriculture.ift.Constants;
import fr.gouv.agriculture.ift.model.NumeroAmm;
import fr.gouv.agriculture.ift.model.Produit;
import fr.gouv.agriculture.ift.service.ProduitService;
import fr.gouv.agriculture.ift.util.Views;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(value = Constants.API_PRODUITS, produces = MediaType.APPLICATION_JSON_VALUE)
@Api(tags = {Constants.PRODUITS}, description = "Ressources sur les produits")
public class ProduitController {

    @Autowired
    ProduitService produitService;

    @ApiOperation(value = "findAllProduits", notes = "Retourne la liste des produits")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", dataType = "integer", paramType = "query",
                    value = "Page de résultats à récupérer (0 par défaut)."),
            @ApiImplicitParam(name = "size", dataType = "integer", paramType = "query",
                    value = "Nombre de résultats par page (200 par défaut).")
    })
    @JsonView(Views.Public.class)
    @GetMapping
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

    @ApiOperation(value = "getNumeroAmmByProduitAndCampagne", notes = "Retourne le numero Amm du produit pour une campagne donnée")
    @ApiResponses(value = {
            @ApiResponse(code = 404, message = "Not Found")
    })
    @JsonView(Views.Public.class)
    @GetMapping("/{libelle}/numero-amm/{campagne}")
    public List<NumeroAmm> getNumeroAmmByProduitAndCampagne(@ApiParam(value = "Libelle du produit", required = true)
                                   @PathVariable String libelle,
                                                            @ApiParam(value = "Identifiant métier de la campagne", required = true)
                                   @PathVariable String campagne) {
        return produitService.getNumeroAmmByProduitAndCampagne(libelle, campagne);
    }
}
