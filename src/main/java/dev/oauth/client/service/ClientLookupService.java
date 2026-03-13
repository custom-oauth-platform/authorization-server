package dev.oauth.client.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ClientLookupService {

    private final RegisteredClientRepository registeredClientRepository;

    public RegisteredClient getByClientId(String clientId) {
        RegisteredClient client = registeredClientRepository.findByClientId(clientId);
        if (client == null) {
            throw new IllegalArgumentException("등록되지 않은 client_id 입니다: " + clientId);
        }
        return client;
    }
}