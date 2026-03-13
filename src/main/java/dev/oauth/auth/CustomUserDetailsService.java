package dev.oauth.auth;

import dev.oauth.entity.AuthMember;
import dev.oauth.repository.AuthMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final AuthMemberRepository authMemberRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Auth_DB의 auth_member 테이블에서 조회
        AuthMember authMember = authMemberRepository.findByUserId(username)
                .orElseThrow(() -> new UsernameNotFoundException("아이디가 존재하지 않습니다."));

        return User.withUsername(authMember.getUserId())
                .password(authMember.getPassword())
                .authorities(authMember.getRole())
                .build();
    }
}