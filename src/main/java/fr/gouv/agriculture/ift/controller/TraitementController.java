package fr.gouv.agriculture.ift.controller;

import com.fasterxml.jackson.annotation.JsonView;
import fr.gouv.agriculture.ift.Constants;
import fr.gouv.agriculture.ift.model.Traitement;
import fr.gouv.agriculture.ift.service.TraitementService;
import fr.gouv.agriculture.ift.util.Views;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(value = Constants.API_TRAITEMENTS_ROOT, produces = MediaType.APPLICATION_JSON_VALUE)
@Api(tags = {Constants.TRAITEMENTS}, description = "Ressources sur les traitements")
public class TraitementController {

    @Autowired
    TraitementService traitementService;

    @ApiOperation(value = "findAllTraitements", notes = "Retourne la liste des traitements")
    @JsonView(Views.Public.class)
    @GetMapping
    public List<Traitement> findAllTraitements() {
        return traitementService.findAllTraitements();
    }

    @ApiOperation(value = "findTraitementByIdMetier", notes = "Retourne le traitement par son identifiant métier")
    @JsonView(Views.Public.class)
    @GetMapping("/{traitementIdMetier}")
    public Traitement findTraitementById(@ApiParam(value = "Identifiant métier du traitement", required = true)
                               @PathVariable String traitementIdMetier) {
        return traitementService.findTraitementByIdMetier(traitementIdMetier);
    }
}
