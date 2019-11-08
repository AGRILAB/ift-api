package fr.gouv.agriculture.ift.service.impl;

import fr.gouv.agriculture.ift.controller.form.AgentForm;
import fr.gouv.agriculture.ift.exception.ConflictException;
import fr.gouv.agriculture.ift.exception.NotFoundException;
import fr.gouv.agriculture.ift.model.Agent;
import fr.gouv.agriculture.ift.repository.AgentRepository;
import fr.gouv.agriculture.ift.service.AgentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class AgentServiceImpl implements AgentService {

    @Autowired
    private AgentRepository repository;

    @Override
    public Agent save(AgentForm agentForm) {
        Agent newAgent = AgentForm.mapToAgent(agentForm);
        Agent found = repository.findAgentByLogin(newAgent.getLogin());

        if (found == null) {
            newAgent.setId(UUID.randomUUID());
            log.debug("Create Agent: {}", newAgent);
        } else {
            throw newConflictException(newAgent);
        }

        try{
            return repository.save(newAgent);
        } catch (DataIntegrityViolationException e) {
            throw newConflictException(newAgent);
        }
    }

    @Override
    public List<Agent> findAllAgents() {
        log.debug("Get All Agents");
        return repository.findAll(new Sort(Sort.Direction.ASC, "login"));
    }

    @Override
    public Agent findAgentByLogin(String login) {
        log.debug("Get Agent by Login: {}", login);
        Agent found = repository.findAgentByLogin(login);

        if (found == null) {
            throw new NotFoundException();
        } else {
            return found;
        }
    }

    @Override
    public Agent updateById(UUID id, AgentForm agentForm) {
        Agent found = repository.findOne(id);

        if (found == null) {
            throw new NotFoundException();
        } else {
            Agent agent = AgentForm.mapToAgent(agentForm);
            agent.setId(id);
            log.debug("Update Agent: {}", agent);

            try {
                return repository.save(agent);
            } catch (DataIntegrityViolationException e) {
                throw newConflictException(agent);
            }
        }
    }

    @Override
    public void delete(UUID id) {
        log.debug("Delete Agent: {}", id);
        Agent found = repository.findOne(id);
        if (found == null) {
            throw new NotFoundException();
        } else {
            repository.delete(id);
        }
    }

    private ConflictException newConflictException(Agent agent){
        return new ConflictException("L'agent avec le login " + agent.getLogin() + " ou l'adresse email " + agent.getEmail() + " existe déjà.");
    }
}
