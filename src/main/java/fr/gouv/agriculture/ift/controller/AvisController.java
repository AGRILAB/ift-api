package fr.gouv.agriculture.ift.controller;

import com.fasterxml.jackson.annotation.JsonView;
import fr.gouv.agriculture.ift.Constants;
import fr.gouv.agriculture.ift.controller.form.AvisForm;
import fr.gouv.agriculture.ift.exception.InvalidBindingEntityException;
import fr.gouv.agriculture.ift.model.Avis;
import fr.gouv.agriculture.ift.service.AvisService;
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

@Slf4j
@RestController
@RequestMapping(value = Constants.API_AVIS_ROOT, produces = MediaType.APPLICATION_JSON_VALUE)
@Api(tags = {Constants.AVIS}, description = "Ressources sur les avis")
public class AvisController {

    @Autowired
    AvisService avisService;

    @ApiOperation(value = "findAllAvis", notes = "Retourne la liste des avis")
    @JsonView(Views.Public.class)
    @GetMapping
    public List<Avis> findAllAvis() {
        return avisService.findAllAvis();
    }

    @ApiOperation(value = "findAvisByNote", notes = "Retourne la liste des avis en fonction de la note fournie")
    @JsonView(Views.Public.class)
    @GetMapping("/{note}")
    public List<Avis> findAvisByNote(@ApiParam(value = "Note", required = true)
                               @PathVariable Integer note) {
        return avisService.findAvisByNote(note);
    }

    @ApiOperation(value = "createAvis", notes = "Ajout d'un avis")
    @JsonView(Views.Public.class)
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(code = HttpStatus.CREATED)
    public Avis createAvis(@ApiParam(value = "Avis", required = true)
                                   @RequestBody @Valid AvisForm avisForm,
                               BindingResult result) {
        if (result.hasErrors()) {
            throw new InvalidBindingEntityException(result);
        }
        return avisService.save(avisForm);
    }
}
