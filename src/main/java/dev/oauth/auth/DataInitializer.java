package dev.oauth.auth;

import dev.oauth.user.entity.User;
import dev.oauth.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * 애플리케이션 최초 실행 시 테스트용 사용자를 DB에 삽입합니다.
 * 이미 같은 username이 존재하면 삽입하지 않습니다.
 *
 * ※ 실제 운영 환경에서는 이 클래스를 제거하거나
 * @Profile("dev") 로 제한하세요.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        upsertUser("user1", "1234");
        upsertUser("user2", "1234");
    }

    private void upsertUser(String username, String rawPassword) {
        User user = userRepository.findByUsername(username).orElse(null);

        if (user == null) {
            user = new User(username, passwordEncoder.encode(rawPassword));
            userRepository.save(user);
            log.info("✅ 테스트 사용자 생성 완료: {}", username);
        } else {
            log.info("ℹ️  사용자 이미 존재: {}", username);
        }
    }
}
