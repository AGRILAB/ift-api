package fr.gouv.agriculture.ift.controller;

import com.fasterxml.jackson.annotation.JsonView;
import fr.gouv.agriculture.ift.Constants;
import fr.gouv.agriculture.ift.controller.form.CultureForm;
import fr.gouv.agriculture.ift.dto.WarningDTO;
import fr.gouv.agriculture.ift.exception.InvalidBindingEntityException;
import fr.gouv.agriculture.ift.model.Culture;
import fr.gouv.agriculture.ift.service.CultureService;
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

import static fr.gouv.agriculture.ift.Constants.CSV;
import static fr.gouv.agriculture.ift.Constants.CULTURES;

@Slf4j
@RestController
@RequestMapping(value = Constants.API_ADMIN_ROOT, produces = MediaType.APPLICATION_JSON_VALUE)
@Api(tags = {Constants.ADMIN}, description = "Ressources sur les cultures pour les administrateurs")
public class AdminCultureController {

    @Autowired
    CultureService cultureService;

    @ApiOperation(hidden = true, value = "addCultures", notes = "Ajout de cultures avec un fichier CSV")
    @JsonView(Views.ExtendedPublic.class)
    @PostMapping(CULTURES + CSV)
    public WarningDTO addCultures(HttpServletRequest request) throws IOException, ServletException {
        InputStream inputStream = request.getPart("file").getInputStream();
        String warningMessage = cultureService.addCultures(inputStream);
        return WarningDTO.builder().message(warningMessage).build();
    }

    @ApiOperation(hidden = true, value = "createCulture", notes = "Ajout d'une culture")
    @JsonView(Views.ExtendedPublic.class)
    @PostMapping(value = CULTURES, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(code = HttpStatus.CREATED)
    public Culture createCulture(@ApiParam(value = "Culture", required = true)
                                 @RequestBody @Valid CultureForm cultureForm,
                                 BindingResult result) {
        if (result.hasErrors()) {
            throw new InvalidBindingEntityException(result);
        }
        return cultureService.save(cultureForm);
    }

    @ApiOperation(hidden = true, value = "updateCulture", notes = "Modification d'une culture")
    @JsonView(Views.ExtendedPublic.class)
    @PutMapping(value = CULTURES + "/{cultureId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Culture updateCulture(@ApiParam(value = "Identification de la culture", required = true)
                                 @PathVariable UUID cultureId,
                                 @ApiParam(value = "Culture", required = true)
                                 @RequestBody @Valid CultureForm cultureForm,
                                 BindingResult result) {
        if (result.hasErrors()) {
            throw new InvalidBindingEntityException(result);
        }
        return cultureService.updateById(cultureId, cultureForm);
    }

    @ApiOperation(hidden = true, value = "deleteCulture", notes = "Suppression d'une culture")
    @JsonView(Views.ExtendedPublic.class)
    @DeleteMapping(CULTURES + "/{cultureId}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void deleteCulture(@ApiParam(value = "Identification de la culture", required = true)
                              @PathVariable UUID cultureId) {
        cultureService.delete(cultureId);
    }

    @ApiOperation(hidden = true, value = "findAllCultures", notes = "Retourne la liste des cultures")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", dataType = "int", paramType = "query",
                    value = "Page de résultats à récupérer (0 par défaut)."),
            @ApiImplicitParam(name = "size", dataType = "int", paramType = "query",
                    value = "Nombre de résultats par page (200 par défaut, max 2000).")
    })
    @JsonView(Views.ExtendedPublic.class)
    @GetMapping(CULTURES)
    @ResponseStatus(code = HttpStatus.PARTIAL_CONTENT)
    public List<Culture> findAllCultures(@ApiParam(value = "Identifiant métier de la campagne")
                                         @RequestParam(value = "campagneIdMetier", required = false) String campagneIdMetier,
                                         @ApiParam(value = "Identifiant métier du numéro Amm")
                                         @RequestParam(value = "numeroAmmIdMetier", required = false) String[] numeroAmmIdMetier,
                                         @ApiParam(value = "Identifiant métier de la cible")
                                         @RequestParam(value = "cibleIdMetier", required = false) String cibleIdMetier,
                                         @ApiParam(value = "Filtre sur le libellé de la culture")
                                         @RequestParam(value = "filtre", required = false) String filtre,
                                         @PageableDefault(page= 0, value = 200) Pageable pageable) {
        return cultureService.findCultures(campagneIdMetier, numeroAmmIdMetier, cibleIdMetier, filtre, pageable);

    }
}
