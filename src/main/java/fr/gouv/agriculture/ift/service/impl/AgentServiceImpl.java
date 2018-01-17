package fr.gouv.agriculture.ift.service.impl;

import fr.gouv.agriculture.ift.controller.form.AgentForm;
import fr.gouv.agriculture.ift.exception.NotFoundException;
import fr.gouv.agriculture.ift.model.Agent;
import fr.gouv.agriculture.ift.repository.AgentRepository;
import fr.gouv.agriculture.ift.service.AgentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@CacheConfig(cacheNames = "agent")
public class AgentServiceImpl implements AgentService {

    @Autowired
    private AgentRepository repository;

    @Override
    @CacheEvict(allEntries = true)
    public Agent save(AgentForm agentForm) {
        Agent newAgent = AgentForm.mapToAgent(agentForm);
        Agent found = repository.findAgentByLogin(newAgent.getLogin());

        if (found == null) {
            newAgent.setId(UUID.randomUUID());
            log.debug("Create Agent: {}", newAgent);
        } else {
            newAgent.setId(found.getId());
            log.debug("Update Agent: {}", newAgent);
        }

        return repository.save(newAgent);
    }

    @Override
    @Cacheable(key = "#root.methodName")
    public List<Agent> findAllAgents() {
        log.debug("Get All Agents");
        return repository.findAll(new Sort(Sort.Direction.ASC, "login"));
    }

    @Override
    @Cacheable(key = "#root.methodName + '_' + #login")
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
    @CacheEvict(allEntries = true)
    public Agent updateById(UUID id, AgentForm agentForm) {
        Agent found = repository.findOne(id);

        if (found == null) {
            throw new NotFoundException();
        } else {
            Agent agent = AgentForm.mapToAgent(agentForm);
            agent.setId(id);
            log.debug("Update Agent: {}", agent);

            return repository.save(agent);
        }
    }

    @Override
    @CacheEvict(allEntries = true)
    public void delete(UUID id) {
        log.debug("Delete Agent: {}", id);
        Agent found = repository.findOne(id);
        if (found == null) {
            throw new NotFoundException();
        } else {
            repository.delete(id);
        }
    }
}
