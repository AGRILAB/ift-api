package fr.gouv.agriculture.ift.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class JWTAuthenticatedFilter extends GenericFilterBean {

    private TokenAuthenticationService tokenAuthenticationService;
    private List<RequestMatcher> protectedRequestMatchers = new ArrayList<>();

    /**
     * Filter which is charge to assert that request against protected endpoints contains valid JWT token.
     *
     * @param tokenAuthenticationService Service used to generate and validate JWT authentication token.
     * @param protectedRequestURIs        Request URI which must not be catched by this filter. For instance : Register,
     *                                   ForgotPassword endpoints.
     */
    public JWTAuthenticatedFilter(TokenAuthenticationService tokenAuthenticationService, List<String> protectedRequestURIs) {
        this.tokenAuthenticationService = tokenAuthenticationService;
        protectedRequestURIs.forEach(pattern -> protectedRequestMatchers.add(new AntPathRequestMatcher(pattern)));
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {

        boolean isProtected = protectedRequestMatchers.stream().anyMatch(requestMatcher -> requestMatcher.matches((HttpServletRequest) request));

        if (isProtected) {
            Authentication authentication = tokenAuthenticationService.getAuthentication((HttpServletRequest) request);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }
}