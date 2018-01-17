package fr.gouv.agriculture.ift.controller;

import com.fasterxml.jackson.annotation.JsonView;
import fr.gouv.agriculture.ift.Constants;
import fr.gouv.agriculture.ift.controller.form.DoseReferenceForm;
import fr.gouv.agriculture.ift.exception.InvalidBindingEntityException;
import fr.gouv.agriculture.ift.exception.InvalidParameterException;
import fr.gouv.agriculture.ift.exception.NotFoundException;
import fr.gouv.agriculture.ift.model.Campagne;
import fr.gouv.agriculture.ift.model.DoseReference;
import fr.gouv.agriculture.ift.model.enumeration.TypeDoseReference;
import fr.gouv.agriculture.ift.service.CampagneService;
import fr.gouv.agriculture.ift.service.DoseReferenceService;
import fr.gouv.agriculture.ift.util.Views;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.UUID;

import static fr.gouv.agriculture.ift.Constants.*;

@Slf4j
@RestController
@RequestMapping(value = Constants.API_ADMIN_ROOT, produces = MediaType.APPLICATION_JSON_VALUE)
@Api(tags = {Constants.ADMIN}, description = "Ressources sur les doses de référence pour les administrateurs")
public class AdminDoseReferenceController {

    @Autowired
    CampagneService campagneService;

    @Autowired
    DoseReferenceService doseReferenceService;

    @ApiOperation(hidden = true, value = "createDoseReference", notes = "Ajout d'une dose de référence")
    @JsonView(Views.ExtendedPublic.class)
    @PostMapping(value = DOSES_REFERENCE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(code = HttpStatus.CREATED)
    public DoseReference createDoseReferenceCible(@ApiParam(value = "DoseReference", required = true)
                                                  @RequestBody @Valid DoseReferenceForm doseReferenceForm,
                                                  BindingResult result) {
        if (result.hasErrors()) {
            throw new InvalidBindingEntityException(result);
        }
        return doseReferenceService.save(doseReferenceForm);
    }

    @ApiOperation(hidden = true, value = "updateDoseReference", notes = "Modification d'une dose de référence")
    @JsonView(Views.ExtendedPublic.class)
    @PutMapping(value = DOSES_REFERENCE + "/{doseReferenceId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public DoseReference updateDoseReferenceCible(@ApiParam(value = "Identification de la dose de référence", required = true)
                                                  @PathVariable UUID doseReferenceId,
                                                  @ApiParam(value = "DoseReference", required = true)
                                                  @RequestBody @Valid DoseReferenceForm doseReferenceForm,
                                                  BindingResult result) {
        if (result.hasErrors()) {
            throw new InvalidBindingEntityException(result);
        }
        return doseReferenceService.updateById(doseReferenceId, doseReferenceForm);
    }

    @ApiOperation(hidden = true, value = "deleteDoseReference", notes = "Suppression d'une dose de référence définie à la cible")
    @JsonView(Views.ExtendedPublic.class)
    @DeleteMapping(DOSES_REFERENCE + "/{doseReferenceId}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void deleteDoseReferenceCible(@ApiParam(value = "Identification de la dose de référence", required = true)
                                         @PathVariable UUID doseReferenceId) {
        doseReferenceService.delete(doseReferenceId);
    }

    @ApiOperation(hidden = true, value = "addDosesReferenceCible", notes = "Ajout de doses de référence définies à la cible avec un fichier CSV")
    @JsonView(Views.ExtendedPublic.class)
    @PostMapping(value = DOSES_REFERENCE_CIBLE + CSV)
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void addDosesReferenceCible(@ApiParam(value = "Identification de la campagne", required = true)
                                           @RequestParam(value = "campagneIdMetier") String campagneIdMetier,
                                                           HttpServletRequest request) throws IOException, ServletException {
        InputStream inputStream = request.getPart("file").getInputStream();
        try {
            Campagne campagne = campagneService.findCampagneByIdMetier(campagneIdMetier);
            doseReferenceService.deleteDoseReferenceCible(campagne);
            doseReferenceService.addDosesReferenceCible(campagne, inputStream);
        } catch (NotFoundException ex) {
            throw new InvalidParameterException("La campagne ayant pour id métier " + campagneIdMetier + " n'existe pas.");
        }
    }

    @ApiOperation(hidden = true, value = "addDosesReferenceCulture", notes = "Ajout de doses de référence définies à la culture avec un fichier CSV")
    @JsonView(Views.ExtendedPublic.class)
    @PostMapping(value = DOSES_REFERENCE_CULTURE + CSV)
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void addDosesReferenceCulture(@ApiParam(value = "Identification de la campagne", required = true)
                                             @RequestParam(value = "campagneIdMetier") String campagneIdMetier,
                                                               HttpServletRequest request) throws IOException, ServletException {
        InputStream inputStream = request.getPart("file").getInputStream();
        try {
            Campagne campagne = campagneService.findCampagneByIdMetier(campagneIdMetier);
            doseReferenceService.deleteDoseReferenceCulture(campagne);
            doseReferenceService.addDosesReferenceCulture(campagne, inputStream);
        } catch (NotFoundException ex) {
            throw new InvalidParameterException("La campagne ayant pour id métier " + campagneIdMetier + " n'existe pas.");
        }
    }

    @ApiOperation(hidden = true, value = "findAllDosesReference", notes = "Retourne la liste des doses de référence")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", dataType = "integer", paramType = "query",
                    value = "Page de résultats à récupérer (0 par défaut)."),
            @ApiImplicitParam(name = "size", dataType = "integer", paramType = "query",
                    value = "Nombre de résultats par page (200 par défaut).")
    })
    @JsonView(Views.ExtendedPublic.class)
    @GetMapping(DOSES_REFERENCE)
    public List<DoseReference> findAllDosesReference(@ApiParam(value = "Identifiant de la campagne")
                                                     @RequestParam(value = "campagneIdMetier", required = false) String campagneIdMetier,
                                                     @ApiParam(value = "Identifiant de la culture")
                                                     @RequestParam(value = "cultureIdMetier", required = false) String cultureIdMetier,
                                                     @ApiParam(value = "Identifiant du numero Amm")
                                                     @RequestParam(value = "numeroAmmIdMetier", required = false) String numeroAmmIdMetier,
                                                     @ApiParam(value = "Identifiant de la cible")
                                                     @RequestParam(value = "cibleIdMetier", required = false) String cibleIdMetier,
                                                     @ApiParam(value = "Type de dose de référence (culture ou cible)")
                                                     @RequestParam(value = "type", required = false) TypeDoseReference typeDoseReference,
                                                     @PageableDefault(page= 0, value = 200) Pageable pageable
    ) {

        if (TypeDoseReference.culture.equals(typeDoseReference) && cibleIdMetier != null){
            throw new InvalidParameterException("Le paramètre cibleIdMetier n'est pas compatible avec une recherche de dose de référence à la culture");
        }
        return doseReferenceService.findDosesReference(campagneIdMetier, cultureIdMetier, numeroAmmIdMetier, cibleIdMetier, typeDoseReference, pageable);
    }
}
