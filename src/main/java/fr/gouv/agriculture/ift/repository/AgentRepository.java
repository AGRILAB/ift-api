package fr.gouv.agriculture.ift.repository;

import fr.gouv.agriculture.ift.model.Agent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AgentRepository extends JpaRepository<Agent, UUID> {

    Agent findAgentByLogin(String login);
}
