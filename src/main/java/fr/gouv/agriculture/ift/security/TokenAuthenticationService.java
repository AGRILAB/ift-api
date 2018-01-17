package fr.gouv.agriculture.ift.security;

import fr.gouv.agriculture.ift.dto.JwtToken;
import fr.gouv.agriculture.ift.model.Agent;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static fr.gouv.agriculture.ift.Constants.*;

@Slf4j
public class TokenAuthenticationService {

    private final long expirationDelay;
    private final String secret;

    public TokenAuthenticationService(String secret, long expirationDelay) {
        this.expirationDelay = expirationDelay;
        this.secret = secret;
    }

    /**
     * Generate a valid JWT token in the HTTP response (Header and body) for the given id and list of roles.
     *
     * @param agent provided Agent
     */
    public JwtToken getAuthentication(Agent agent, String role) {
        // We generate a token now.

        String JWT = Jwts.builder()
                .setSubject(agent.getLogin())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationDelay))
                .signWith(SignatureAlgorithm.HS256, secret)
                .claim(ROLE, role)
                .compact();

        return JwtToken.builder().token(JWT).build();
    }
    /**
     * Retrieve authentication from the JWT token if the token is valid.
     * @param request HTTP Servlet resquest from where the token is retrieved in the Header "Authorization"
     *
     * @return Null if the token is not valid or expired or an Valid Authentication object built from the JWT token
     */
    public Authentication getAuthentication(HttpServletRequest request) {
        String token = extractAuthTokenFromRequest(request);

        String id;
        try {
            // parse the token.
            id = Jwts.parser()
                    .setSigningKey(secret)
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();
        } catch (JwtException | IllegalArgumentException e) {
            log.warn(e.getMessage());
            id = null;
        }

        if (id != null) // we managed to retrieve a user
        {
            String roles = (String) Jwts.parser()
                    .setSigningKey(secret)
                    .parseClaimsJws(token)
                    .getBody()
                    .get(ROLE);
            List<GrantedAuthority> authorities = new ArrayList<>();
            for (String role : roles.split(",")) {
                authorities.add(new SimpleGrantedAuthority(role.toUpperCase()));
            }
            return new UsernamePasswordAuthenticationToken(id, null, authorities);
        }

        return null;
    }

    private String extractAuthTokenFromRequest(HttpServletRequest request) {
        String token = request.getHeader(HEADER_STRING);

        if (token == null) {
            token = request.getParameter(TOKEN);
        } else {
            token = token.replace(TOKEN_PREFIX, "");
        }

        return token;
    }
}