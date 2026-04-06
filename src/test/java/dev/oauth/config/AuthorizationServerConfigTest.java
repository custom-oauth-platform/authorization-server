package dev.oauth.config;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;

import static org.assertj.core.api.Assertions.assertThat;

class AuthorizationServerConfigTest {

    @Test
    void registeredClientRequiresPkce() {
        PasswordEncoder passwordEncoder = new SecurityConfig().passwordEncoder();
        AuthorizationServerConfig config = new AuthorizationServerConfig(
                passwordEncoder,
                "oauth2-client-app",
                "secret",
                "http://localhost:3000/api/auth/callback/custom-oauth",
                "http://localhost:3000",
                "http://localhost:9000",
                30,
                15
        );

        RegisteredClientRepository repository = config.registeredClientRepository();
        RegisteredClient client = repository.findByClientId("oauth2-client-app");

        assertThat(client).isNotNull();
        assertThat(client.getClientSettings().isRequireProofKey()).isTrue();
    }
}
