package fr.gouv.agriculture.ift.controller;

import com.fasterxml.jackson.annotation.JsonView;
import fr.gouv.agriculture.ift.Constants;
import fr.gouv.agriculture.ift.controller.form.UniteForm;
import fr.gouv.agriculture.ift.exception.InvalidBindingEntityException;
import fr.gouv.agriculture.ift.model.Unite;
import fr.gouv.agriculture.ift.service.UniteService;
import fr.gouv.agriculture.ift.util.Views;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

import static fr.gouv.agriculture.ift.Constants.UNITES;

@Slf4j
@RestController
@RequestMapping(value = Constants.API_ADMIN_ROOT, produces = MediaType.APPLICATION_JSON_VALUE)
@Api(tags = {Constants.ADMIN}, description = "Ressources sur les unités pour les administrateurs")
public class AdminUniteController {

    @Autowired
    UniteService uniteService;

    @ApiOperation(hidden = true, value = "createUnite", notes = "Ajout d'une unité")
    @JsonView(Views.ExtendedPublic.class)
    @PostMapping(value = UNITES, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(code = HttpStatus.CREATED)
    public Unite createUnite(@ApiParam(value = "Unité", required = true)
                             @RequestBody @Valid UniteForm uniteForm,
                             BindingResult result) {
        if (result.hasErrors()) {
            throw new InvalidBindingEntityException(result);
        }
        return uniteService.save(uniteForm);
    }

    @ApiOperation(hidden = true, value = "updateUnite", notes = "Modification d'une d'unité")
    @JsonView(Views.ExtendedPublic.class)
    @PutMapping(value = UNITES + "/{uniteId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Unite updateUnite(@ApiParam(value = "Identification de l'unité", required = true)
                             @PathVariable UUID uniteId,
                             @ApiParam(value = "Unite", required = true)
                             @RequestBody @Valid UniteForm uniteForm,
                             BindingResult result) {
        if (result.hasErrors()) {
            throw new InvalidBindingEntityException(result);
        }
        return uniteService.updateById(uniteId, uniteForm);
    }

    @ApiOperation(hidden = true, value = "deleteUnite", notes = "Suppression d'une unité")
    @JsonView(Views.ExtendedPublic.class)
    @DeleteMapping(UNITES + "/{uniteId}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void deleteUnite(@ApiParam(value = "Identification de l'unité", required = true)
                            @PathVariable UUID uniteId) {
        uniteService.delete(uniteId);
    }

    @ApiOperation(hidden = true, value = "findAllUnites", notes = "Retourne la liste des unités")
    @JsonView(Views.ExtendedPublic.class)
    @GetMapping(UNITES)
    public List<Unite> findAllUnites() {
        return uniteService.findAllUnites();
    }
}
