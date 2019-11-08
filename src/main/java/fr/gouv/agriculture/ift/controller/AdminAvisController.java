package fr.gouv.agriculture.ift.controller;

import com.fasterxml.jackson.annotation.JsonView;
import fr.gouv.agriculture.ift.Constants;
import fr.gouv.agriculture.ift.model.Avis;
import fr.gouv.agriculture.ift.service.AvisService;
import fr.gouv.agriculture.ift.util.Views;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

import static fr.gouv.agriculture.ift.Constants.AVIS;

@Slf4j
@RestController
@RequestMapping(value = Constants.API_ADMIN_ROOT, produces = MediaType.APPLICATION_JSON_VALUE)
@Api(hidden = true, tags = {Constants.ADMIN}, description = "Ressources sur les avis pour les administrateurs")
public class AdminAvisController {

    @Autowired
    AvisService avisService;

    @ApiOperation(hidden = true, value = "findAllAvis", notes = "Retourne la liste des avis")
    @JsonView(Views.Internal.class)
    @GetMapping(value = AVIS)
    public List<Avis> findAllAvis() {
        return avisService.findAllAvis();
    }

    @ApiOperation(hidden = true, value = "findAvisByNote", notes = "Retourne la liste des avis en fonction de la note fournie")
    @JsonView(Views.Internal.class)
    @GetMapping("/{note}")
    public List<Avis> findAvisByNote(@ApiParam(value = "Note", required = true)
                                     @PathVariable Integer note) {
        return avisService.findAvisByNote(note);
    }
    
    @ApiOperation(value = "deleteAvis", notes = "Suppression d'un avis")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "OK."),
            @ApiResponse(code = 404, message = "Not Found Id."),
            @ApiResponse(code = 400, message = "Bad Request, Invalid Id.")
    })
    @JsonView(Views.Public.class)
    @DeleteMapping("/avis/{avisId}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> deleteAvis(@ApiParam(value = "avisId", required = true) @PathVariable String avisId) {
        UUID id;
    	try {
            id = UUID.fromString(avisId);
            avisService.delete(id);
        } catch (IllegalArgumentException ex) {
            return new ResponseEntity<>(null, null, HttpStatus.BAD_REQUEST);
        } catch (fr.gouv.agriculture.ift.exception.NotFoundException ex) {
            return new ResponseEntity<>(null, null, HttpStatus.NOT_FOUND);
        }
    	return new ResponseEntity<>(null, null, HttpStatus.NO_CONTENT);
    }
}
