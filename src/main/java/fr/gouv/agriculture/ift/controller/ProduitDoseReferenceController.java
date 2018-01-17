package fr.gouv.agriculture.ift.controller;

import com.fasterxml.jackson.annotation.JsonView;
import fr.gouv.agriculture.ift.Constants;
import fr.gouv.agriculture.ift.model.ProduitDoseReference;
import fr.gouv.agriculture.ift.model.enumeration.TypeDoseReference;
import fr.gouv.agriculture.ift.service.ProduitDoseReferenceService;
import fr.gouv.agriculture.ift.util.Views;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(value = Constants.API_PRODUITS_DOSES_REFERENCE_ROOT, produces = MediaType.APPLICATION_JSON_VALUE)
@Api(tags = {Constants.PRODUITS_DOSES_REFERENCE}, description = "Ressources sur les doses de référence et les produits associés")
public class ProduitDoseReferenceController {

    @Autowired
    ProduitDoseReferenceService produitDoseReferenceService;

    @ApiOperation(value = "findAllProduitsDosesReference", notes = "Retourne la liste des doses de référence et les produits associés")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", dataType = "integer", paramType = "query",
                    value = "Page de résultats à récupérer (0 par défaut)."),
            @ApiImplicitParam(name = "size", dataType = "integer", paramType = "query",
                    value = "Nombre de résultats par page (200 par défaut).")
    })
    @JsonView(Views.Public.class)
    @GetMapping
    public List<ProduitDoseReference> findAllProduitsDosesReference(@ApiParam(value = "Identifiant métier de la campagne", required = true)
                                                                    @RequestParam(value = "campagneIdMetier") String campagneIdMetier,
                                                                    @ApiParam(value = "Identifiant métier de la culture")
                                                                    @RequestParam(value = "cultureIdMetier", required = false) String cultureIdMetier,
                                                                    @ApiParam(value = "Libellé du produit")
                                                                    @RequestParam(value = "produitLibelle", required = false) String produitLibelle,
                                                                    @ApiParam(value = "Identifiant métier du numero Amm")
                                                                    @RequestParam(value = "numeroAmmIdMetier", required = false) String numeroAmmIdMetier,
                                                                    @ApiParam(value = "Identifiant métier de la cible")
                                                                    @RequestParam(value = "cibleIdMetier", required = false) String cibleIdMetier,
                                                                    @ApiParam(value = "Type de dose de référence (culture ou cible)")
                                                                        @RequestParam(value = "type", required = false) TypeDoseReference typeDoseReference,
                                                                    @ApiParam(value = "Biocontrole")
                                                                    @RequestParam(value = "biocontrole", required = false) Boolean biocontrole,
                                                                    @PageableDefault(page= 0, value = 200) Pageable pageable) {
        return produitDoseReferenceService.findProduitsDosesReferenceByCampagneAndCultureAndProduitAndNumeroAmmAndCible(campagneIdMetier, cultureIdMetier, produitLibelle, numeroAmmIdMetier, cibleIdMetier, typeDoseReference, biocontrole, pageable);
    }
}
