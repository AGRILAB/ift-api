package fr.gouv.agriculture.ift.controller;

import com.fasterxml.jackson.annotation.JsonView;
import fr.gouv.agriculture.ift.Constants;
import fr.gouv.agriculture.ift.model.Cible;
import fr.gouv.agriculture.ift.service.CibleService;
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

@Slf4j
@RestController
@RequestMapping(value = Constants.API_CIBLES_ROOT, produces = MediaType.APPLICATION_JSON_VALUE)
@Api(tags = {Constants.CIBLES}, description = "Référentiel des cibles des produits phytosanitaires")
public class CibleController {

    @Autowired
    CibleService cibleService;

    @ApiOperation(value = "findAllCibles", notes = "Retourne la liste des cibles")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", dataType = "int", paramType = "query",
                    value = "Page de résultats à récupérer (0 par défaut)."),
            @ApiImplicitParam(name = "size", dataType = "int", paramType = "query",
                    value = "Nombre de résultats par page (200 par défaut, max 2000).")
    })
    @JsonView(Views.Public.class)
    @GetMapping
    @ResponseStatus(code = HttpStatus.PARTIAL_CONTENT)
    public List<Cible> findAllCibles(@ApiParam(value = "Identifiant métier de la campagne")
                                     @RequestParam(value = "campagneIdMetier", required = false) String campagneIdMetier,
                                     @ApiParam(value = "Identifiant métier de la culture")
                                     @RequestParam(value = "cultureIdMetier", required = false) String cultureIdMetier,
                                     @ApiParam(value = "Identifiant métier du numéro Amm")
                                     @RequestParam(value = "numeroAmmIdMetier", required = false) String[] numeroAmmIdMetier,
                                     @ApiParam(value = "Filtre sur le libellé de la cible")
                                         @RequestParam(value = "filtre", required = false) String filtre,
                                     @PageableDefault(page= 0, value = 200) Pageable pageable) {
        return cibleService.findCibles(campagneIdMetier, cultureIdMetier, numeroAmmIdMetier, filtre, pageable);
    }

    @ApiOperation(value = "findAllCiblesAsCSV", notes = "Retourne la liste des cibles au format CSV")
    @JsonView(Views.Public.class)
    @GetMapping(value = CSV, produces = "text/csv")
    public HttpEntity<String> findAllCiblesAsCSV() {

        HttpHeaders header = new HttpHeaders();
        header.set(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=Cibles.csv");

        return new HttpEntity<>(cibleService.findAllCiblesAsCSV(), header);
    }

    @ApiOperation(value = "findCibleByIdMetier", notes = "Retourne la cible par son identifiant métier")
    @ApiResponses(value = {
            @ApiResponse(code = 404, message = "Not Found")
    })
    @JsonView(Views.Public.class)
    @GetMapping("/{cibleIdMetier}")
    public Cible findCibleById(@ApiParam(value = "Identifiant métier de la cible", required = true)
                               @PathVariable String cibleIdMetier) {
        return cibleService.findCibleByIdMetier(cibleIdMetier);
    }
}
