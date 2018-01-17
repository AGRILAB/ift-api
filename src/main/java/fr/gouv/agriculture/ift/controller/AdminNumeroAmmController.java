package fr.gouv.agriculture.ift.controller;

import com.fasterxml.jackson.annotation.JsonView;
import fr.gouv.agriculture.ift.Constants;
import fr.gouv.agriculture.ift.dto.NumeroAmmDTO;
import fr.gouv.agriculture.ift.exception.InvalidBindingEntityException;
import fr.gouv.agriculture.ift.service.NumeroAmmService;
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
import static fr.gouv.agriculture.ift.Constants.NUMEROS_AMM;

@Slf4j
@RestController
@RequestMapping(value = Constants.API_ADMIN_ROOT, produces = MediaType.APPLICATION_JSON_VALUE)
@Api(tags = {Constants.ADMIN}, description = "Ressources sur les numéros AMM pour les administrateurs")
public class AdminNumeroAmmController {

    @Autowired
    NumeroAmmService numeroAmmService;

    @ApiOperation(hidden = true, value = "addNumerosAmm", notes = "Ajout des numéros AMM avec un fichier CSV")
    @JsonView(Views.ExtendedPublic.class)
    @PostMapping(value = NUMEROS_AMM + CSV)
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void addNumerosAmm(HttpServletRequest request) throws IOException, ServletException {
        InputStream inputStream = request.getPart("file").getInputStream();
        numeroAmmService.addNumerosAmm(inputStream);
    }

    @ApiOperation(hidden = true, value = "createNumeroAmm", notes = "Ajout d'un numéro AMM")
    @JsonView(Views.ExtendedPublic.class)
    @PostMapping(value = NUMEROS_AMM, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(code = HttpStatus.CREATED)
    public NumeroAmmDTO createNumeroAmm(@ApiParam(value = "NumeroAmm", required = true)
                                                           @RequestBody @Valid NumeroAmmDTO numeroAmmDTO,
                                                           BindingResult result) {
        if (result.hasErrors()) {
            throw new InvalidBindingEntityException(result);
        }
        return numeroAmmService.save(numeroAmmDTO);
    }

    @ApiOperation(hidden = true, value = "updateNumeroAmm", notes = "Modification d'un numéro AMM")
    @JsonView(Views.ExtendedPublic.class)
    @PutMapping(value = NUMEROS_AMM + "/{numeroAmmId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public NumeroAmmDTO updateNumeroAmm(@ApiParam(value = "Identification du numéro AMM", required = true)
                                                           @PathVariable UUID numeroAmmId,
                                                           @ApiParam(value = "NumeroAmm", required = true)
                                                           @RequestBody @Valid NumeroAmmDTO numeroAmmDTO,
                                                           BindingResult result) {
        if (result.hasErrors()) {
            throw new InvalidBindingEntityException(result);
        }
        return numeroAmmService.updateById(numeroAmmId, numeroAmmDTO);
    }

    @ApiOperation(hidden = true, value = "deleteNumeroAmm", notes = "Suppression d'un numéro AMM")
    @JsonView(Views.ExtendedPublic.class)
    @DeleteMapping(NUMEROS_AMM + "/{numeroAmmId}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void deleteNumeroAmm(@ApiParam(value = "Identification du numéro AMM", required = true)
                                           @PathVariable UUID numeroAmmId) {
        numeroAmmService.delete(numeroAmmId);
    }

    @ApiOperation(hidden = true, value = "findAllNumerosAmm", notes = "Retourne la liste des numéros AMM")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", dataType = "integer", paramType = "query",
                    value = "Page de résultats à récupérer (0 par défaut)."),
            @ApiImplicitParam(name = "size", dataType = "integer", paramType = "query",
                    value = "Nombre de résultats par page (200 par défaut).")
    })
    @JsonView(Views.ExtendedPublic.class)
    @GetMapping(NUMEROS_AMM)
    public List<NumeroAmmDTO> findAllNumerosAmm(@ApiParam(value = "Filtre sur le numéro AMM")
                                             @RequestParam(value = "filtre", required = false) String filtre,
                                                @PageableDefault(page= 0, value = 200) Pageable pageable) {
        return numeroAmmService.findNumerosAmmWithValidities(filtre, pageable);
    }
}
