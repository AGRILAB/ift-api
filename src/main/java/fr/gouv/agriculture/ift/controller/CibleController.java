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
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(value = Constants.API_CIBLES_ROOT, produces = MediaType.APPLICATION_JSON_VALUE)
@Api(tags = {Constants.CIBLES}, description = "Ressources sur les cultures")
public class CibleController {

    @Autowired
    CibleService cibleService;

    @ApiOperation(value = "findAllCibles", notes = "Retourne la liste des cibles")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", dataType = "integer", paramType = "query",
                    value = "Page de résultats à récupérer (0 par défaut)."),
            @ApiImplicitParam(name = "size", dataType = "integer", paramType = "query",
                    value = "Nombre de résultats par page (200 par défaut).")
    })
    @JsonView(Views.Public.class)
    @GetMapping
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

    @ApiOperation(value = "findCibleByIdMetier", notes = "Retourne la cible par son identifiant métier")
    @JsonView(Views.Public.class)
    @GetMapping("/{cibleIdMetier}")
    public Cible findCibleById(@ApiParam(value = "Identifiant métier de la cible", required = true)
                               @PathVariable String cibleIdMetier) {
        return cibleService.findCibleByIdMetier(cibleIdMetier);
    }
}
