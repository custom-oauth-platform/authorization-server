package dev.oauth.consent.controller;

import dev.oauth.client.service.ClientLookupService;
import dev.oauth.consent.dto.ConsentScopeView;
import dev.oauth.consent.service.ConsentViewService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsent;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsentService;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class ConsentController {

    private final ClientLookupService clientLookupService;
    private final ConsentViewService consentViewService;
    private final OAuth2AuthorizationConsentService authorizationConsentService;

    @GetMapping("/oauth2/consent")
    public String consentPage(
            Principal principal,
            @RequestParam("client_id") String clientId,
            @RequestParam(value = "scope", required = false) String scope,
            @RequestParam(value = "state", required = false) String state,
            Model model
    ) {
        RegisteredClient client = clientLookupService.getByClientId(clientId);
        Set<String> approvedScopes = getApprovedScopes(client.getId(), principal.getName());
        List<ConsentScopeView> scopeViews = consentViewService.parseRequestedScopes(scope).stream()
                .filter(item -> !approvedScopes.contains(item.scope()))
                .toList();

        model.addAttribute("principalName", principal.getName());
        model.addAttribute("clientId", clientId);
        model.addAttribute("clientName",
                client.getClientName() != null ? client.getClientName() : clientId);
        model.addAttribute("state", state);
        model.addAttribute("scopes", scopeViews);

        return "consent";
    }

    private Set<String> getApprovedScopes(String registeredClientId, String principalName) {
        OAuth2AuthorizationConsent consent =
                authorizationConsentService.findById(registeredClientId, principalName);

        if (consent == null) {
            return Set.of();
        }

        return consent.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .filter(authority -> authority.startsWith("SCOPE_"))
                .map(authority -> authority.substring("SCOPE_".length()))
                .collect(Collectors.toSet());
    }
}
