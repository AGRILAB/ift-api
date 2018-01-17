package fr.gouv.agriculture.ift.controller;

import com.fasterxml.jackson.annotation.JsonView;
import fr.gouv.agriculture.ift.Constants;
import fr.gouv.agriculture.ift.controller.form.AgentForm;
import fr.gouv.agriculture.ift.exception.InvalidBindingEntityException;
import fr.gouv.agriculture.ift.model.Agent;
import fr.gouv.agriculture.ift.service.AgentService;
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

import static fr.gouv.agriculture.ift.Constants.AGENTS;

@Slf4j
@RestController
@RequestMapping(value = Constants.API_ADMIN_ROOT, produces = MediaType.APPLICATION_JSON_VALUE)
@Api(tags = {Constants.ADMIN}, description = "Ressources sur les agents pour les administrateurs")
public class AdminAgentController {

    @Autowired
    AgentService agentService;

    @ApiOperation(hidden = true, value = "createAgent", notes = "Ajout d'un agent")
    @JsonView(Views.ExtendedPublic.class)
    @PostMapping(value = AGENTS, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(code = HttpStatus.CREATED)
    public Agent createAgent(@ApiParam(value = "Agent", required = true)
                                   @RequestBody @Valid AgentForm agentForm,
                                   BindingResult result) {
        if (result.hasErrors()) {
            throw new InvalidBindingEntityException(result);
        }
        return agentService.save(agentForm);
    }

    @ApiOperation(hidden = true, value = "updateAgent", notes = "Modification d'un agent")
    @JsonView(Views.ExtendedPublic.class)
    @PutMapping(value = AGENTS + "/{agentId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Agent updateAgent(@ApiParam(value = "Identification de l'agent", required = true)
                                   @PathVariable UUID agentId,
                                   @ApiParam(value = "Agent", required = true)
                                   @RequestBody @Valid AgentForm agentForm,
                                   BindingResult result) {
        if (result.hasErrors()) {
            throw new InvalidBindingEntityException(result);
        }
        return agentService.updateById(agentId, agentForm);
    }

    @ApiOperation(hidden = true, value = "deleteAgent", notes = "Suppression d'un agent")
    @JsonView(Views.ExtendedPublic.class)
    @DeleteMapping(AGENTS + "/{agentId}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void deleteAgent(@ApiParam(value = "Identification de l'agent", required = true)
                               @PathVariable UUID agentId) {
        agentService.delete(agentId);
    }

    @ApiOperation(hidden = true, value = "findAllAgents", notes = "Retourne la liste des agents")
    @JsonView(Views.ExtendedPublic.class)
    @GetMapping(AGENTS)
    public List<Agent> findAllAgents() {
        return agentService.findAllAgents();
    }
}
