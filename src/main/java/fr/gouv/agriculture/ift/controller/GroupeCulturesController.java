package fr.gouv.agriculture.ift.controller;

import com.fasterxml.jackson.annotation.JsonView;
import fr.gouv.agriculture.ift.Constants;
import fr.gouv.agriculture.ift.model.GroupeCultures;
import fr.gouv.agriculture.ift.service.GroupeCulturesService;
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
@RequestMapping(value = Constants.API_GROUPES_CULTURES_ROOT, produces = MediaType.APPLICATION_JSON_VALUE)
@Api(tags = {Constants.GROUPES_CULTURES}, description = "Référentiel des groupes de cultures")
public class GroupeCulturesController {

    @Autowired
    GroupeCulturesService groupeCulturesService;

    @ApiOperation(value = "findAllGroupesCultures", notes = "Retourne la liste des groupes de cultures")
    @JsonView(Views.Public.class)
    @GetMapping
    public List<GroupeCultures> findAllGroupesCultures() {
        return groupeCulturesService.findAllGroupesCultures();
    }

    @ApiOperation(value = "findGroupeCulturesByIdMetier", notes = "Retourne le groupe de cultures par son identifiant métier")
    @ApiResponses(value = {
            @ApiResponse(code = 404, message = "Not Found")
    })
    @JsonView(Views.Public.class)
    @GetMapping("/{groupeCulturesIdMetier}")
    public GroupeCultures findGroupeCulturesById(@ApiParam(value = "Identifiant métier du groupe de cultures", required = true)
                               @PathVariable String groupeCulturesIdMetier) {
        return groupeCulturesService.findGroupeCulturesByIdMetier(groupeCulturesIdMetier);
    }
}
