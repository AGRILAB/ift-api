package fr.gouv.agriculture.ift.controller;

import com.fasterxml.jackson.annotation.JsonView;
import fr.gouv.agriculture.ift.Constants;
import fr.gouv.agriculture.ift.model.Segment;
import fr.gouv.agriculture.ift.service.SegmentService;
import fr.gouv.agriculture.ift.util.Views;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(value = Constants.API_SEGMENTS_ROOT, produces = MediaType.APPLICATION_JSON_VALUE)
@Api(tags = {Constants.SEGMENTS}, description = "Référentiel des segments utilisés dans le bilan IFT")
public class SegmentController {

    @Autowired
    SegmentService segmentService;

    @ApiOperation(value = "findAllSegments", notes = "Retourne la liste des segments")
    @JsonView(Views.Public.class)
    @GetMapping
    public List<Segment> findAllSegments() {
        return segmentService.findAllSegments();
    }

    @ApiOperation(value = "findSegmentByIdMetier", notes = "Retourne le segment par son identifiant métier")
    @ApiResponses(value = {
            @ApiResponse(code = 404, message = "Not Found")
    })
    @JsonView(Views.Public.class)
    @GetMapping("/{segmentIdMetier}")
    public Segment findSegmentById(@ApiParam(value = "Identifiant métier du segment", required = true)
                               @PathVariable String segmentIdMetier) {
        return segmentService.findSegmentByIdMetier(segmentIdMetier);
    }
}
