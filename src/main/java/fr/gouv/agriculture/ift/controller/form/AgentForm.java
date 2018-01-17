package fr.gouv.agriculture.ift.controller.form;

import fr.gouv.agriculture.ift.model.Agent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotEmpty;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AgentForm {

    @NotEmpty
    private String login;

    @NotEmpty
    private String email;

    public static Agent mapToAgent(AgentForm agentForm) {
        Agent agent = new Agent();
        agent.setLogin(agentForm.getLogin());
        agent.setEmail(agentForm.getEmail());
        return agent;
    }

}
