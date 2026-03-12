package dev.oauth.auth;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.ui.Model;

@Controller
public class LoginController {

    /**
     * GET /login
     * 커스텀 로그인 페이지 렌더링
     * Spring Authorization Server가 미인증 사용자를 이 경로로 리다이렉트함
     *
     * @param error 로그인 실패 시 "true" 값으로 넘어옴
     * @param model Thymeleaf 모델 (에러 메시지 전달용)
     */
    @GetMapping("/login")
    public String loginPage(@RequestParam(value = "error", required = false) String error,
            Model model) {
        if (error != null) {
            model.addAttribute("errorMessage", "아이디 또는 비밀번호가 올바르지 않습니다.");
        }
        return "login"; // templates/login.html 렌더링
    }
}
