package fr.gouv.agriculture.ift.controller;

import com.fasterxml.jackson.annotation.JsonView;
import fr.gouv.agriculture.ift.Constants;
import fr.gouv.agriculture.ift.model.Culture;
import fr.gouv.agriculture.ift.service.CultureService;
import fr.gouv.agriculture.ift.util.Views;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static fr.gouv.agriculture.ift.Constants.CSV;
import static fr.gouv.agriculture.ift.Constants.GROUPES_CULTURES;

@Slf4j
@RestController
@RequestMapping(value = Constants.API_CULTURES_ROOT, produces = MediaType.APPLICATION_JSON_VALUE)
@Api(tags = {Constants.CULTURES}, description = "Référentiel des cultures agricoles")
public class CultureController {

    @Autowired
    CultureService cultureService;

    @ApiOperation(value = "findAllCultures", notes = "Retourne la liste des cultures")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", dataType = "int", paramType = "query",
                    value = "Page de résultats à récupérer (0 par défaut)."),
            @ApiImplicitParam(name = "size", dataType = "int", paramType = "query",
                    value = "Nombre de résultats par page (200 par défaut, max 2000).")
    })
    @JsonView(Views.Public.class)
    @GetMapping
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

    @ApiOperation(value = "findAllCulturesAsCSV", notes = "Retourne la liste des cultures au format CSV")
    @JsonView(Views.Public.class)
    @GetMapping(value = CSV, produces = "text/csv")
    public HttpEntity<String> findAllCulturesAsCSV() {
        HttpHeaders header = new HttpHeaders();
        header.set(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=Cultures.csv");

        return new HttpEntity<>(cultureService.findAllCulturesAsCSV(), header);
    }

    @ApiOperation(value = "findCulturesByGroupeCultures", notes = "Retourne la liste des cultures pour un groupe de cultures donné")
    @JsonView(Views.Public.class)
    @GetMapping(GROUPES_CULTURES + "/{groupeCulturesIdMetier}")
    public List<Culture> findCulturesByGroupeCultures(@ApiParam(value = "Identifiant métier du groupe de cultures", required = true)
                                                      @PathVariable String groupeCulturesIdMetier) {
        return cultureService.findCulturesByGroupeCultures(groupeCulturesIdMetier);
    }

    @ApiOperation(value = "findCultureByIdMetier", notes = "Retourne la culture par son identifiant métier")
    @ApiResponses(value = {
            @ApiResponse(code = 404, message = "Not Found")
    })
    @JsonView(Views.Public.class)
    @GetMapping("/{cultureIdMetier}")
    public Culture findCultureById(@ApiParam(value = "Identifiant métier de la culture", required = true)
                                   @PathVariable String cultureIdMetier) {
        return cultureService.findCultureByIdMetier(cultureIdMetier);
    }
}
