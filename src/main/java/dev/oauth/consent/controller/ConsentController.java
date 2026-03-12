package dev.oauth.consent.controller;

import dev.oauth.client.service.ClientLookupService;
import dev.oauth.consent.dto.ConsentScopeView;
import dev.oauth.consent.service.ConsentViewService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class ConsentController {

    private final ClientLookupService clientLookupService;
    private final ConsentViewService consentViewService;

    @GetMapping("/oauth2/consent")
    public String consentPage(
            Principal principal,
            @RequestParam("client_id") String clientId,
            @RequestParam(value = "scope", required = false) String scope,
            @RequestParam(value = "state", required = false) String state,
            Model model
    ) {
        RegisteredClient client = clientLookupService.getByClientId(clientId);
        List<ConsentScopeView> scopeViews = consentViewService.parseRequestedScopes(scope);

        model.addAttribute("principalName", principal.getName());
        model.addAttribute("clientId", clientId);
        model.addAttribute("clientName",
                client.getClientName() != null ? client.getClientName() : clientId);
        model.addAttribute("state", state);
        model.addAttribute("scopes", scopeViews);

        return "consent";
    }
}