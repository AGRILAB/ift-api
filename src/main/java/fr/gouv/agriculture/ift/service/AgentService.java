package fr.gouv.agriculture.ift.service;

import fr.gouv.agriculture.ift.controller.form.AgentForm;
import fr.gouv.agriculture.ift.model.Agent;

import java.util.List;
import java.util.UUID;

public interface AgentService {

    Agent save(AgentForm agentForm);
    List<Agent> findAllAgents();
    Agent findAgentByLogin(String login);
    Agent updateById(UUID id, AgentForm agentForm);
    void delete(UUID id);

}
