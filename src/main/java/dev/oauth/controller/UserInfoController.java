package dev.oauth.controller;

import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class UserInfoController {
    @GetMapping("/userinfo")
    public ResponseEntity<Map<String, Object>> getUserInfo(@AuthenticationPrincipal OAuth2ResourceServerProperties.Jwt jwt) {
        if (jwt == null) {
            // 토큰이 제대로 주입되지 않았다면 401을 반환하게 함
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        Map<String, Object> claims = new HashMap<>(jwt.getClass().getModifiers());
        return ResponseEntity.ok(claims);
    }
}