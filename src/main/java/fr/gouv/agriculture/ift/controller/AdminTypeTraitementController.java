package fr.gouv.agriculture.ift.controller;

import com.fasterxml.jackson.annotation.JsonView;
import fr.gouv.agriculture.ift.Constants;
import fr.gouv.agriculture.ift.controller.form.TypeTraitementForm;
import fr.gouv.agriculture.ift.exception.InvalidBindingEntityException;
import fr.gouv.agriculture.ift.model.TypeTraitement;
import fr.gouv.agriculture.ift.service.TypeTraitementService;
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

import static fr.gouv.agriculture.ift.Constants.TYPES_TRAITEMENTS;

@Slf4j
@RestController
@RequestMapping(value = Constants.API_ADMIN_ROOT, produces = MediaType.APPLICATION_JSON_VALUE)
@Api(tags = {Constants.ADMIN}, description = "Ressources sur les types de traitements pour les administrateurs")
public class AdminTypeTraitementController {

    @Autowired
    TypeTraitementService typeTraitementService;

    @ApiOperation(hidden = true, value = "updateTypeTraitement", notes = "Modification d'un type de traitement")
    @JsonView(Views.ExtendedPublic.class)
    @PutMapping(value = TYPES_TRAITEMENTS + "/{typeTraitementId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public TypeTraitement updateTypeTraitement(@ApiParam(value = "Identification du type de traitement", required = true)
                                 @PathVariable UUID typeTraitementId,
                                           @ApiParam(value = "Type de traitement", required = true)
                                 @RequestBody @Valid TypeTraitementForm typeTraitementForm,
                                           BindingResult result) {
        if (result.hasErrors()) {
            throw new InvalidBindingEntityException(result);
        }
        return typeTraitementService.updateById(typeTraitementId, typeTraitementForm);
    }

    @ApiOperation(hidden = true, value = "findAllTypesTraitements", notes = "Retourne la liste des types de traitements")
    @JsonView(Views.ExtendedPublic.class)
    @GetMapping(TYPES_TRAITEMENTS)
    public List<TypeTraitement> findAllTypesTraitements() {
        return typeTraitementService.findAllTypesTraitements();
    }
}
