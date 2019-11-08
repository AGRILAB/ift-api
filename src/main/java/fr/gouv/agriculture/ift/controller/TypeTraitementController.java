package fr.gouv.agriculture.ift.controller;

import com.fasterxml.jackson.annotation.JsonView;
import fr.gouv.agriculture.ift.Constants;
import fr.gouv.agriculture.ift.model.TypeTraitement;
import fr.gouv.agriculture.ift.service.TypeTraitementService;
import fr.gouv.agriculture.ift.util.Views;
import io.swagger.annotations.*;
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
@RequestMapping(value = Constants.API_TYPES_TRAITEMENTS_ROOT, produces = MediaType.APPLICATION_JSON_VALUE)
@Api(tags = {Constants.TYPES_TRAITEMENTS}, description = "Référentiel des types de traitements phytosanitaires")
public class TypeTraitementController {

    @Autowired
    TypeTraitementService typeTraitementService;

    @ApiOperation(value = "findAllTypesTraitements", notes = "Retourne la liste des types de traitements")
    @JsonView(Views.Public.class)
    @GetMapping
    public List<TypeTraitement> findAllTypesTraitements() {
        return typeTraitementService.findAllTypesTraitements();
    }

    @ApiOperation(value = "findTypeTraitementByIdMetier", notes = "Retourne le type de traitement par son identifiant métier")
    @ApiResponses(value = {
            @ApiResponse(code = 404, message = "Not Found")
    })
    @JsonView(Views.Public.class)
    @GetMapping("/{typeTraitementIdMetier}")
    public TypeTraitement findTypeTraitementById(@ApiParam(value = "Identifiant métier du type de traitement", required = true)
                               @PathVariable String typeTraitementIdMetier) {
        return typeTraitementService.findTypeTraitementByIdMetier(typeTraitementIdMetier);
    }
}
