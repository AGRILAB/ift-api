package fr.gouv.agriculture.ift.controller;

import com.fasterxml.jackson.annotation.JsonView;
import fr.gouv.agriculture.ift.Constants;
import fr.gouv.agriculture.ift.model.Avertissement;
import fr.gouv.agriculture.ift.service.AvertissementService;
import fr.gouv.agriculture.ift.util.Views;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(value = Constants.API_AVERTISSEMENTS_ROOT, produces = MediaType.APPLICATION_JSON_VALUE)
@Api(tags = {Constants.AVERTISSEMENTS}, description = "Ressources sur les avertissements")
public class AvertissementController {

    @Autowired
    AvertissementService avertissementService;

    @ApiOperation(value = "findAllAvertissements", notes = "Retourne la liste des avertissements")
    @JsonView(Views.Public.class)
    @GetMapping
    public List<Avertissement> findAllAvertissements() {
        return avertissementService.findAllAvertissements();
    }
}
