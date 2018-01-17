package fr.gouv.agriculture.ift.controller;

import com.fasterxml.jackson.annotation.JsonView;
import fr.gouv.agriculture.ift.Constants;
import fr.gouv.agriculture.ift.controller.form.CibleForm;
import fr.gouv.agriculture.ift.exception.InvalidBindingEntityException;
import fr.gouv.agriculture.ift.model.Cible;
import fr.gouv.agriculture.ift.service.CibleService;
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

import static fr.gouv.agriculture.ift.Constants.CIBLES;
import static fr.gouv.agriculture.ift.Constants.CSV;

@Slf4j
@RestController
@RequestMapping(value = Constants.API_ADMIN_ROOT, produces = MediaType.APPLICATION_JSON_VALUE)
@Api(tags = {Constants.ADMIN}, description = "Ressources sur les cibles pour les administrateurs")
public class AdminCibleController {

    @Autowired
    CibleService cibleService;

    @ApiOperation(hidden = true, value = "addCibles", notes = "Ajout de cibles avec un fichier CSV")
    @JsonView(Views.ExtendedPublic.class)
    @PostMapping(CIBLES + CSV)
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void addCibles(HttpServletRequest request) throws IOException, ServletException {
        InputStream inputStream = request.getPart("file").getInputStream();
        cibleService.addCibles(inputStream);
    }

    @ApiOperation(hidden = true, value = "createCible", notes = "Ajout d'une cible")
    @JsonView(Views.ExtendedPublic.class)
    @PostMapping(value = CIBLES, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(code = HttpStatus.CREATED)
    public Cible createCible(@ApiParam(value = "Cible", required = true)
                             @RequestBody @Valid CibleForm cibleForm,
                             BindingResult result) {
        if (result.hasErrors()) {
            throw new InvalidBindingEntityException(result);
        }
        return cibleService.save(cibleForm);
    }

    @ApiOperation(hidden = true, value = "updateCible", notes = "Modification d'une cible")
    @JsonView(Views.ExtendedPublic.class)
    @PutMapping(value = CIBLES + "/{cibleId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Cible updateCible(@ApiParam(value = "Identification de la cible", required = true)
                             @PathVariable UUID cibleId,
                             @ApiParam(value = "Cible", required = true)
                             @RequestBody @Valid CibleForm cibleForm,
                             BindingResult result) {
        if (result.hasErrors()) {
            throw new InvalidBindingEntityException(result);
        }
        return cibleService.updateById(cibleId, cibleForm);
    }

    @ApiOperation(hidden = true, value = "deleteCible", notes = "Suppression d'une cible")
    @JsonView(Views.ExtendedPublic.class)
    @DeleteMapping(CIBLES + "/{cibleId}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void deleteCible(@ApiParam(value = "Identification de la cible", required = true)
                            @PathVariable UUID cibleId) {
        cibleService.delete(cibleId);
    }

    @ApiOperation(hidden = true, value = "findAllCibles", notes = "Retourne la liste des cibles")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", dataType = "integer", paramType = "query",
                    value = "Page de résultats à récupérer (0 par défaut)."),
            @ApiImplicitParam(name = "size", dataType = "integer", paramType = "query",
                    value = "Nombre de résultats par page (200 par défaut).")
    })
    @JsonView(Views.ExtendedPublic.class)
    @GetMapping(CIBLES)
    public List<Cible> findAllCibles(@ApiParam(value = "Identifiant métier de la campagne")
                                     @RequestParam(value = "campagneIdMetier", required = false) String campagneIdMetier,
                                     @ApiParam(value = "Identifiant métier de la culture")
                                     @RequestParam(value = "cultureIdMetier", required = false) String cultureIdMetier,
                                     @ApiParam(value = "Identifiant métier du numéro Amm")
                                     @RequestParam(value = "numeroAmmIdMetier", required = false) String numeroAmmIdMetier,
                                     @ApiParam(value = "Filtre sur le libellé de la cible")
                                     @RequestParam(value = "filtre", required = false) String filtre,
                                     @PageableDefault(page= 0, value = 200) Pageable pageable) {
        return cibleService.findCibles(campagneIdMetier, numeroAmmIdMetier, cultureIdMetier, filtre, pageable);
    }
}
