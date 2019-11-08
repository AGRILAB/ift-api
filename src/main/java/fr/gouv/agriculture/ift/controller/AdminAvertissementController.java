package fr.gouv.agriculture.ift.controller;

import com.fasterxml.jackson.annotation.JsonView;
import fr.gouv.agriculture.ift.Constants;
import fr.gouv.agriculture.ift.controller.form.AvertissementForm;
import fr.gouv.agriculture.ift.exception.InvalidBindingEntityException;
import fr.gouv.agriculture.ift.model.Avertissement;
import fr.gouv.agriculture.ift.service.AvertissementService;
import fr.gouv.agriculture.ift.util.Views;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

import static fr.gouv.agriculture.ift.Constants.AVERTISSEMENTS;

@Slf4j
@RestController
@RequestMapping(value = Constants.API_ADMIN_ROOT, produces = MediaType.APPLICATION_JSON_VALUE)
@Api(tags = {Constants.ADMIN}, description = "Ressources sur les avertissements pour les administrateurs")
public class AdminAvertissementController {

    @Autowired
    AvertissementService avertissementService;

    @ApiOperation(hidden = true, value = "updateAvertissement", notes = "Modification d'un avertissement")
    @JsonView(Views.ExtendedPublic.class)
    @PutMapping(value = AVERTISSEMENTS + "/{avertissementId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Avertissement updateAvertissement(@ApiParam(value = "Identification de l'avertissement", required = true)
                                   @PathVariable UUID avertissementId,
                                   @ApiParam(value = "Avertissement", required = true)
                                   @RequestBody @Valid AvertissementForm avertissementForm,
                                   BindingResult result) {
        if (result.hasErrors()) {
            throw new InvalidBindingEntityException(result);
        }
        return avertissementService.updateById(avertissementId, avertissementForm);
    }

    @ApiOperation(hidden = true, value = "findAllAvertissements", notes = "Retourne la liste des avertissements")
    @JsonView(Views.ExtendedPublic.class)
    @GetMapping(AVERTISSEMENTS)
    public List<Avertissement> findAllAvertissements() {
        return avertissementService.findAllAvertissements();
    }
}
