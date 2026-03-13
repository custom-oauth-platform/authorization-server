package dev.oauth.consent.service;

import dev.oauth.consent.dto.ConsentScopeView;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class ConsentViewService {

    public List<ConsentScopeView> parseRequestedScopes(String scope) {
        if (scope == null || scope.isBlank()) {
            return List.of();
        }

        return Arrays.stream(scope.split(" "))
                .filter(s -> !s.isBlank())
                .distinct()
                .map(this::toView)
                .toList();
    }

    private ConsentScopeView toView(String scope) {
        return switch (scope) {
            case "openid" -> new ConsentScopeView(scope, "사용자 식별 정보에 접근합니다.");
            case "profile" -> new ConsentScopeView(scope, "기본 프로필 정보에 접근합니다.");
            case "read" -> new ConsentScopeView(scope, "데이터 조회 권한입니다.");
            case "write" -> new ConsentScopeView(scope, "데이터 수정 권한입니다.");
            default -> new ConsentScopeView(scope, "요청 권한입니다.");
        };
    }
}