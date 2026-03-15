package dev.oauth.consent.service;

import dev.oauth.consent.dto.ConsentScopeView;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

@Service
public class ConsentViewService {

    public List<ConsentScopeView> parseRequestedScopes(String scope) {
        if (scope == null || scope.isBlank()) {
            return List.of();
        }

        return Arrays.stream(scope.split(" "))
                .filter(s -> !s.isBlank())
                .filter(s -> !"openid".equals(s))
                .distinct()
                .map(this::toView)
                .sorted(Comparator.comparingInt(this::sortOrder))
                .toList();
    }

    private ConsentScopeView toView(String scope) {
        return switch (scope) {
            case "name" -> new ConsentScopeView(scope, "이름",
                    "[필수] 마이페이지에서 이름 정보를 조회합니다.", true);
            case "gender" -> new ConsentScopeView(scope, "성별",
                    "마이페이지에서 성별 정보를 조회합니다.", false);
            case "birthdate" -> new ConsentScopeView(scope, "생일",
                    "마이페이지에서 생일 정보를 조회합니다.", false);
            case "email" -> new ConsentScopeView(scope, "이메일",
                    "마이페이지에서 이메일 정보를 조회합니다.", false);
            default -> new ConsentScopeView(scope, scope, "요청 권한입니다.", false);
        };
    }

    private int sortOrder(ConsentScopeView item) {
        return switch (item.scope()) {
            case "name" -> 0;
            case "gender" -> 1;
            case "birthdate" -> 2;
            case "email" -> 3;
            default -> 100;
        };
    }
}
