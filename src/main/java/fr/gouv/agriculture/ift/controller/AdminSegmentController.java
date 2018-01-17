package fr.gouv.agriculture.ift.controller;

import com.fasterxml.jackson.annotation.JsonView;
import fr.gouv.agriculture.ift.Constants;
import fr.gouv.agriculture.ift.controller.form.SegmentForm;
import fr.gouv.agriculture.ift.exception.InvalidBindingEntityException;
import fr.gouv.agriculture.ift.model.Segment;
import fr.gouv.agriculture.ift.service.CampagneService;
import fr.gouv.agriculture.ift.service.SegmentService;
import fr.gouv.agriculture.ift.util.Views;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

import static fr.gouv.agriculture.ift.Constants.SEGMENTS;

@Slf4j
@RestController
@RequestMapping(value = Constants.API_ADMIN_ROOT, produces = MediaType.APPLICATION_JSON_VALUE)
@Api(tags = {Constants.ADMIN}, description = "Ressources sur les segments pour les administrateurs")
public class AdminSegmentController {

    @Autowired
    CampagneService campagneService;

    @Autowired
    SegmentService segmentService;

    @ApiOperation(hidden = true, value = "createSegment", notes = "Ajout d'un segment")
    @JsonView(Views.ExtendedPublic.class)
    @PostMapping(value = SEGMENTS, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(code = HttpStatus.CREATED)
    public Segment createSegment(@ApiParam(value = "Segment", required = true)
                                 @RequestBody @Valid SegmentForm segmentForm,
                                 BindingResult result) {
        if (result.hasErrors()) {
            throw new InvalidBindingEntityException(result);
        }
        return segmentService.save(segmentForm);
    }

    @ApiOperation(hidden = true, value = "updateSegment", notes = "Modification d'un segment")
    @JsonView(Views.ExtendedPublic.class)
    @PutMapping(value = SEGMENTS + "/{segmentId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Segment updateSegment(@ApiParam(value = "Identification du segment", required = true)
                                 @PathVariable UUID segmentId,
                                 @ApiParam(value = "Segment", required = true)
                                 @RequestBody @Valid SegmentForm segmentForm,
                                 BindingResult result) {
        if (result.hasErrors()) {
            throw new InvalidBindingEntityException(result);
        }
        return segmentService.updateById(segmentId, segmentForm);
    }

    @ApiOperation(hidden = true, value = "deleteSegment", notes = "Suppression d'un segment")
    @JsonView(Views.ExtendedPublic.class)
    @DeleteMapping(SEGMENTS + "/{segmentId}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void deleteSegment(@ApiParam(value = "Identification du segment", required = true)
                              @PathVariable UUID segmentId) {
        segmentService.delete(segmentId);
    }

    @ApiOperation(hidden = true, value = "findAllSegments", notes = "Retourne la liste des segments")
    @JsonView(Views.ExtendedPublic.class)
    @GetMapping(SEGMENTS)
    public List<Segment> findAllSegments() {
        return segmentService.findAllSegments();
    }
}
