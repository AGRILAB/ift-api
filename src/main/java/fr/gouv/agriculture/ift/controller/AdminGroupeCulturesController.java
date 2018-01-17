package fr.gouv.agriculture.ift.controller;

import com.fasterxml.jackson.annotation.JsonView;
import fr.gouv.agriculture.ift.Constants;
import fr.gouv.agriculture.ift.controller.form.GroupeCulturesForm;
import fr.gouv.agriculture.ift.exception.InvalidBindingEntityException;
import fr.gouv.agriculture.ift.model.GroupeCultures;
import fr.gouv.agriculture.ift.service.GroupeCulturesService;
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

import static fr.gouv.agriculture.ift.Constants.GROUPES_CULTURES;

@Slf4j
@RestController
@RequestMapping(value = Constants.API_ADMIN_ROOT, produces = MediaType.APPLICATION_JSON_VALUE)
@Api(tags = {Constants.ADMIN}, description = "Ressources sur les groupes de cultures pour les administrateurs")
public class AdminGroupeCulturesController {

    @Autowired
    GroupeCulturesService groupeCulturesService;

    @ApiOperation(hidden = true, value = "createGroupeCultures", notes = "Ajout d'un groupe de cultures")
    @JsonView(Views.ExtendedPublic.class)
    @PostMapping(value = GROUPES_CULTURES, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(code = HttpStatus.CREATED)
    public GroupeCultures createGroupeCultures(@ApiParam(value = "GroupeCultures", required = true)
                                               @RequestBody @Valid GroupeCulturesForm groupeCulturesForm,
                                               BindingResult result) {
        if (result.hasErrors()) {
            throw new InvalidBindingEntityException(result);
        }
        return groupeCulturesService.save(groupeCulturesForm);
    }

    @ApiOperation(hidden = true, value = "updateGroupeCultures", notes = "Modification d'un groupe de cultures")
    @JsonView(Views.ExtendedPublic.class)
    @PutMapping(value = GROUPES_CULTURES + "/{groupeCulturesId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public GroupeCultures updateGroupeCultures(@ApiParam(value = "Identification du groupe de cultures", required = true)
                                               @PathVariable UUID groupeCulturesId,
                                               @ApiParam(value = "GroupeCultures", required = true)
                                               @RequestBody @Valid GroupeCulturesForm groupeCulturesForm,
                                               BindingResult result) {
        if (result.hasErrors()) {
            throw new InvalidBindingEntityException(result);
        }
        return groupeCulturesService.updateById(groupeCulturesId, groupeCulturesForm);
    }

    @ApiOperation(hidden = true, value = "deleteGroupeCultures", notes = "Suppression d'un groupe de cultures")
    @JsonView(Views.ExtendedPublic.class)
    @DeleteMapping(GROUPES_CULTURES + "/{groupeCulturesId}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void deleteGroupeCultures(@ApiParam(value = "Identification du groupe de cultures", required = true)
                                     @PathVariable UUID groupeCulturesId) {
        groupeCulturesService.delete(groupeCulturesId);
    }

    @ApiOperation(hidden = true, value = "findAllGroupesCultures", notes = "Retourne la liste des groupes de cultures")
    @JsonView(Views.ExtendedPublic.class)
    @GetMapping(GROUPES_CULTURES)
    public List<GroupeCultures> findAllGroupesCultures() {
        return groupeCulturesService.findAllGroupesCultures();
    }
}
