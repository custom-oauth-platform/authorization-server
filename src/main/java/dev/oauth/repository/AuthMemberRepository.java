package dev.oauth.repository;

import dev.oauth.entity.AuthMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AuthMemberRepository extends JpaRepository<AuthMember, Long> {

    // 로그인 시 입력한 아이디(userId)로 사용자를 찾는 메서드
    Optional<AuthMember> findByUserId(String userId);
}
