package fr.gouv.agriculture.ift.controller;

import com.fasterxml.jackson.annotation.JsonView;
import fr.gouv.agriculture.ift.Constants;
import fr.gouv.agriculture.ift.model.Unite;
import fr.gouv.agriculture.ift.service.UniteService;
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
@RequestMapping(value = Constants.API_UNITES_ROOT, produces = MediaType.APPLICATION_JSON_VALUE)
@Api(tags = {Constants.UNITES}, description = "Ressources sur les unités")
public class UniteController {

    @Autowired
    UniteService uniteService;

    @ApiOperation(value = "findAllUnites", notes = "Retourne la liste des unités")
    @JsonView(Views.Public.class)
    @GetMapping
    public List<Unite> findAllUnites() {
        return uniteService.findAllUnites();
    }

    @ApiOperation(value = "findUniteByIdMetier", notes = "Retourne l'unité par son identifiant métier")
    @JsonView(Views.Public.class)
    @GetMapping("/{uniteIdMetier}")
    public Unite findUniteById(@ApiParam(value = "Identifiant de l'unité", required = true)
                               @PathVariable String uniteIdMetier) {
        return uniteService.findUniteByIdMetier(uniteIdMetier);
    }
}
