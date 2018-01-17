package fr.gouv.agriculture.ift.controller;

import com.fasterxml.jackson.annotation.JsonView;
import fr.gouv.agriculture.ift.Constants;
import fr.gouv.agriculture.ift.model.NumeroAmm;
import fr.gouv.agriculture.ift.service.NumeroAmmService;
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
@RequestMapping(value = Constants.API_NUMEROS_AMM_ROOT, produces = MediaType.APPLICATION_JSON_VALUE)
@Api(tags = { Constants.NUMEROS_AMM }, description = "Ressources sur les numéros AMM")
public class NumeroAmmController {

    @Autowired
    NumeroAmmService numeroAmmService;

    @ApiOperation(value = "findAllNumerosAmm", notes = "Retourne la liste des numéros AMM")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", dataType = "integer", paramType = "query",
                    value = "Page de résultats à récupérer (0 par défaut)."),
            @ApiImplicitParam(name = "size", dataType = "integer", paramType = "query",
                    value = "Nombre de résultats par page (200 par défaut).")
    })
    @JsonView(Views.Public.class)
    @GetMapping
    public List<NumeroAmm> findAllNumerosAmm(@ApiParam(value = "Identifiant de la campagne")
                                                 @RequestParam(value = "campagneIdMetier", required = false) String campagneIdMetier,
                                             @ApiParam(value = "Identifiant de la culture")
                                                 @RequestParam(value = "cultureIdMetier", required = false) String cultureIdMetier,
                                             @ApiParam(value = "Identifiant de la cible")
                                                 @RequestParam(value = "cibleIdMetier", required = false) String cibleIdMetier,
                                             @ApiParam(value = "Filtre sur le numéro AMM")
                                                 @RequestParam(value = "filtre", required = false) String filtre,
                                             @PageableDefault(page= 0, value = 200) Pageable pageable) {
        return numeroAmmService.findNumerosAmm(campagneIdMetier, cultureIdMetier, cibleIdMetier, filtre, pageable);
    }

    @ApiOperation(value = "findNumeroAmmByIdMetier", notes = "Retourne le numéro Amm par son identifiant métier")
    @JsonView(Views.Public.class)
    @GetMapping("/{numeroAmmIdMetier}")
    public NumeroAmm findNumeroAmmById(@ApiParam(value = "Identifiant du numéro AMM", required = true)
                               @PathVariable String numeroAmmIdMetier) {
        return numeroAmmService.findNumeroAmmByIdMetier(numeroAmmIdMetier);
    }
}
