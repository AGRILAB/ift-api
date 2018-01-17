package fr.gouv.agriculture.ift.controller;

import fr.gouv.agriculture.ift.Constants;
import fr.gouv.agriculture.ift.dto.JwtToken;
import fr.gouv.agriculture.ift.exception.InvalidParameterException;
import fr.gouv.agriculture.ift.exception.NotFoundException;
import fr.gouv.agriculture.ift.helper.SpringControllerHelper;
import fr.gouv.agriculture.ift.model.Agent;
import fr.gouv.agriculture.ift.repository.AgentRepository;
import fr.gouv.agriculture.ift.security.TokenAuthenticationService;
import lombok.extern.slf4j.Slf4j;
import org.jasig.cas.client.validation.Assertion;
import org.jasig.cas.client.validation.TicketValidationException;
import org.jasig.cas.client.validation.TicketValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static fr.gouv.agriculture.ift.Constants.ROLE_ADMIN;

@Slf4j
@RestController
@RequestMapping(value = Constants.AUTH, produces = MediaType.APPLICATION_JSON_VALUE)
public class AuthController {

    @Value("${auth.eap.endpoint.url}")
    private String EAPEndPointURL;

    @Autowired
    private TicketValidator casTicketValidator;

    @Autowired
    private TokenAuthenticationService tokenAuthenticationService;

    @Autowired
    private AgentRepository agentRepository;

    @RequestMapping(value = "/eap/validate", method = RequestMethod.GET)
    public JwtToken handleAuthValidateEAP(HttpServletRequest request, HttpServletResponse response) throws Exception {

        final String ticket = request.getParameter("ticket");

        if (StringUtils.isEmpty(ticket)) {
            throw new InvalidParameterException("Le ticket fourni est vide.");
        }

        final String service = request.getParameter("serviceUrl");

        try {

            final Assertion validation = casTicketValidator.validate(ticket, service);
            // expiration data not set by EAP  -> do not check validation.isValid()
            final String login = validation.getPrincipal().getName();

            if (StringUtils.isEmpty(login)) {
                throw new NotFoundException();
            }

            Agent agent = agentRepository.findAgentByLogin(login);
            if (agent == null) {
                throw new NotFoundException();
            }

            return tokenAuthenticationService.getAuthentication(agent, ROLE_ADMIN);

        } catch (TicketValidationException ex) {
            throw new InvalidParameterException(ex.getMessage());
        }
    }

    @RequestMapping(value = "/cas/login", method = RequestMethod.GET)
    public void handleAuthLoginFakeCAS(HttpServletRequest request, HttpServletResponse response) throws Exception {

        if (EAPEndPointURL == null || !EAPEndPointURL.contains("localhost")) {
            throw new NotFoundException();
        }

        String service = request.getParameter("service");
        SpringControllerHelper.redirect(response, service + "&ticket=eap-fake-ticket");
    }

    @RequestMapping(value = "/cas/serviceValidate", method = RequestMethod.GET, produces = "application/xml;charset=UTF-8")
    public String handleAuthValidateFakeCAS(HttpServletRequest request) throws Exception {

        if (EAPEndPointURL == null || !EAPEndPointURL.contains("localhost")) {
            throw new NotFoundException();
        }

        Pattern regexp = Pattern.compile("eap-fake-ticket-?(\\d+)?");
        String ticket = request.getParameter("ticket");
        Matcher matcher = regexp.matcher(ticket);

        if (matcher.matches()) {
            String suffix = matcher.group(1);
            String principal = "hermione.granger" + ((suffix == null) ? "" : suffix);
            log.warn("Validating EAP Fake ticket with principal " + principal);
            return "<ns0:serviceResponse xmlns:ns0=\"http://www.yale.edu/tp/cas\">\n" +
                    "    <ns0:authenticationSuccess>\n" +
                    "        <ns0:user>" + principal + "</ns0:user>\n" +
                    "    </ns0:authenticationSuccess>\n" +
                    "</ns0:serviceResponse>\n";
        } else {
            return "<ns0:serviceResponse xmlns:ns0=\"http://www.yale.edu/tp/cas\">\n" +
                    "    <ns0:authenticationFailure code=\"INVALID_TICKET\">\n" +
                    "\t\tle ticket 'eap-fake-ticket' est inconnu\n" +
                    "\t</ns0:authenticationFailure>\n" +
                    "</ns0:serviceResponse>";
        }
    }
}
