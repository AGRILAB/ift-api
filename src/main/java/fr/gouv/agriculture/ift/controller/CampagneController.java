package fr.gouv.agriculture.ift.controller;

import com.fasterxml.jackson.annotation.JsonView;
import fr.gouv.agriculture.ift.Constants;
import fr.gouv.agriculture.ift.model.Campagne;
import fr.gouv.agriculture.ift.service.CampagneService;
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
@RequestMapping(value = Constants.API_CAMPAGNES_ROOT, produces = MediaType.APPLICATION_JSON_VALUE)
@Api(tags = {Constants.CAMPAGNES}, description = "Ressources sur les campagnes")
public class CampagneController {

    @Autowired
    CampagneService campagneService;

    @ApiOperation(value = "findAllCampagnes", notes = "Retourne la liste des campagnes")
    @JsonView(Views.Public.class)
    @GetMapping
    public List<Campagne> findAllCampagnes() {
        return campagneService.findAllCampagnes();
    }

    @ApiOperation(value = "findByIdMetier", notes = "Retourne la campagne par son identifiant métier")
    @JsonView(Views.Public.class)
    @GetMapping("/{campagneIdMetier}")
    public Campagne findById(@ApiParam(value = "Identifiant métier de la campagne", required = true)
                               @PathVariable String campagneIdMetier) {
        return campagneService.findCampagneByIdMetier(campagneIdMetier);
    }

    @ApiOperation(value = "getCurrentCampagne", notes = "Retourne la campagne courante")
    @JsonView(Views.Public.class)
    @GetMapping("/courante")
    public Campagne getCurrentCampagne() {
        return campagneService.getCurrentCampagne();
    }
}
