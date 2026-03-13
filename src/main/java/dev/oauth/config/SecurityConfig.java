package dev.oauth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authorize -> authorize
                        .anyRequest().authenticated()
                )
                // 폼 로그인 설정 (스프링 제공 기본 로그인 창)
                .formLogin(Customizer.withDefaults());

        return http.build();
    }

    @Bean
    // TODO: Just for test
    public UserDetailsService userDetailsService() {
        // [영님이 궁금해하신 부분!] 테스트용 유저를 생성합니다.
        // ID: user / PW: password / 권한: ROLE_USER
        var user = User.withUsername("user")
                .password("{noop}password") // {noop}은 암호화하지 않은 평문임을 의미합니다.
                .roles("USER")
                .build();

        return new InMemoryUserDetailsManager(user);
    }
}