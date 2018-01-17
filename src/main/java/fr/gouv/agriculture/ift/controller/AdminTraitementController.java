package fr.gouv.agriculture.ift.controller;

import com.fasterxml.jackson.annotation.JsonView;
import fr.gouv.agriculture.ift.Constants;
import fr.gouv.agriculture.ift.controller.form.TraitementForm;
import fr.gouv.agriculture.ift.exception.InvalidBindingEntityException;
import fr.gouv.agriculture.ift.model.Traitement;
import fr.gouv.agriculture.ift.service.TraitementService;
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

import static fr.gouv.agriculture.ift.Constants.TRAITEMENTS;

@Slf4j
@RestController
@RequestMapping(value = Constants.API_ADMIN_ROOT, produces = MediaType.APPLICATION_JSON_VALUE)
@Api(tags = {Constants.ADMIN}, description = "Ressources sur les traitements pour les administrateurs")
public class AdminTraitementController {

    @Autowired
    TraitementService traitementService;

    @ApiOperation(hidden = true, value = "createTraitement", notes = "Ajout d'un traitement")
    @JsonView(Views.ExtendedPublic.class)
    @PostMapping(value = TRAITEMENTS, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(code = HttpStatus.CREATED)
    public Traitement createTraitement(@ApiParam(value = "Traitement", required = true)
                                 @RequestBody @Valid TraitementForm traitementForm,
                                 BindingResult result) {
        if (result.hasErrors()) {
            throw new InvalidBindingEntityException(result);
        }
        return traitementService.save(traitementForm);
    }

    @ApiOperation(hidden = true, value = "updateTraitement", notes = "Modification d'un traitement")
    @JsonView(Views.ExtendedPublic.class)
    @PutMapping(value = TRAITEMENTS + "/{traitementId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Traitement updateTraitement(@ApiParam(value = "Identification du traitement", required = true)
                                 @PathVariable UUID traitementId,
                                 @ApiParam(value = "Traitement", required = true)
                                 @RequestBody @Valid TraitementForm traitementForm,
                                 BindingResult result) {
        if (result.hasErrors()) {
            throw new InvalidBindingEntityException(result);
        }
        return traitementService.updateById(traitementId, traitementForm);
    }

    @ApiOperation(hidden = true, value = "deleteTraitement", notes = "Suppression d'un traitement")
    @JsonView(Views.ExtendedPublic.class)
    @DeleteMapping(TRAITEMENTS + "/{traitementId}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void deleteTraitement(@ApiParam(value = "Identification du traitement", required = true)
                              @PathVariable UUID traitementId) {
        traitementService.delete(traitementId);
    }

    @ApiOperation(hidden = true, value = "findAllTraitements", notes = "Retourne la liste des traitements")
    @JsonView(Views.ExtendedPublic.class)
    @GetMapping(TRAITEMENTS)
    public List<Traitement> findAllTraitements() {
        return traitementService.findAllTraitements();
    }
}
