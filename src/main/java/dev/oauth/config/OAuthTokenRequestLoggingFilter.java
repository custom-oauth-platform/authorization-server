package dev.oauth.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class OAuthTokenRequestLoggingFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(OAuthTokenRequestLoggingFilter.class);

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return !"/oauth2/token".equals(request.getServletPath());
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        String grantType = request.getParameter("grant_type");
        String clientId = request.getParameter("client_id");
        String codeVerifier = request.getParameter("code_verifier");
        String authorizationCode = request.getParameter("code");

        log.info(
                "OAuth token request received: grant_type={}, client_id={}, code_present={}, code_verifier_present={}, code_verifier={}",
                grantType,
                clientId,
                authorizationCode != null,
                codeVerifier != null,
                codeVerifier
        );

        filterChain.doFilter(request, response);
    }
}
