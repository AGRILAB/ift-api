package fr.gouv.agriculture.ift.controller;

import com.fasterxml.jackson.annotation.JsonView;
import fr.gouv.agriculture.ift.Constants;
import fr.gouv.agriculture.ift.controller.form.CampagneForm;
import fr.gouv.agriculture.ift.exception.InvalidBindingEntityException;
import fr.gouv.agriculture.ift.model.Campagne;
import fr.gouv.agriculture.ift.service.CampagneService;
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

import static fr.gouv.agriculture.ift.Constants.CAMPAGNES;

@Slf4j
@RestController
@RequestMapping(value = Constants.API_ADMIN_ROOT, produces = MediaType.APPLICATION_JSON_VALUE)
@Api(tags = {Constants.ADMIN}, description = "Ressources sur les campagnes pour les administrateurs")
public class AdminCampagneController {

    @Autowired
    CampagneService campagneService;

    @ApiOperation(hidden = true, value = "createCampagne", notes = "Ajout d'une campagne")
    @JsonView(Views.ExtendedPublic.class)
    @PostMapping(value = CAMPAGNES, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(code = HttpStatus.CREATED)
    public Campagne createCampagne(@ApiParam(value = "Campagne", required = true)
                                   @RequestBody @Valid CampagneForm campagneForm,
                                   BindingResult result) {
        if (result.hasErrors()) {
            throw new InvalidBindingEntityException(result);
        }
        return campagneService.save(campagneForm);
    }

    @ApiOperation(hidden = true, value = "updateCampagne", notes = "Modification d'une campagne")
    @JsonView(Views.ExtendedPublic.class)
    @PutMapping(value = CAMPAGNES + "/{campagneId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Campagne updateCampagne(@ApiParam(value = "Identification de la campagne", required = true)
                                   @PathVariable UUID campagneId,
                                   @ApiParam(value = "Campagne", required = true)
                                   @RequestBody @Valid CampagneForm campagneForm,
                                   BindingResult result) {
        if (result.hasErrors()) {
            throw new InvalidBindingEntityException(result);
        }
        return campagneService.updateById(campagneId, campagneForm);
    }

    @ApiOperation(hidden = true, value = "deleteCampagne", notes = "Suppression d'une campagne")
    @JsonView(Views.ExtendedPublic.class)
    @DeleteMapping(CAMPAGNES + "/{campagneId}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void deleteCampagne(@ApiParam(value = "Identification de la campagne", required = true)
                               @PathVariable UUID campagneId) {
        campagneService.delete(campagneId);
    }

    @ApiOperation(hidden = true, value = "findAllCampagnes", notes = "Retourne la liste des campagnes")
    @JsonView(Views.ExtendedPublic.class)
    @GetMapping(CAMPAGNES)
    public List<Campagne> findAllCampagnes() {
        return campagneService.findAllCampagnes();
    }
}
