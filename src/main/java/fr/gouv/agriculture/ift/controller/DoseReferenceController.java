package fr.gouv.agriculture.ift.controller;

import com.fasterxml.jackson.annotation.JsonView;
import fr.gouv.agriculture.ift.Constants;
import fr.gouv.agriculture.ift.exception.InvalidParameterException;
import fr.gouv.agriculture.ift.model.DoseReference;
import fr.gouv.agriculture.ift.model.enumeration.TypeDoseReference;
import fr.gouv.agriculture.ift.service.DoseReferenceService;
import fr.gouv.agriculture.ift.util.Views;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping(value = Constants.API_DOSES_REFERENCE_ROOT, produces = MediaType.APPLICATION_JSON_VALUE)
@Api(tags = {Constants.DOSES_REFERENCE}, description = "Ressources sur les doses de référence")
public class DoseReferenceController {

    @Autowired
    DoseReferenceService doseReferenceService;


    @ApiOperation(value = "findAllDosesReference", notes = "Retourne la liste des doses de référence")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", dataType = "integer", paramType = "query",
                    value = "Page de résultats à récupérer (0 par défaut)."),
            @ApiImplicitParam(name = "size", dataType = "integer", paramType = "query",
                    value = "Nombre de résultats par page (200 par défaut).")
    })
    @JsonView(Views.Public.class)
    @GetMapping
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

    @ApiOperation(value = "findDoseReferenceById", notes = "Retourne la dose de référence par son identifiant")
    @JsonView(Views.Public.class)
    @GetMapping("/{doseReferenceId}")
    public DoseReference findDoseReferenceCibleById(@ApiParam(value = "Identifiant de la dose de référence", required = true)
                                                         @PathVariable UUID doseReferenceId) {
        return doseReferenceService.findDoseReferenceById(doseReferenceId);
    }
}
