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
            @RequestParam(value = "logout", required = false) String logout,
            Model model) {
        if ("true".equals(error)) {
            model.addAttribute("errorMessage", "아이디 또는 비밀번호가 올바르지 않습니다.");
        }
        if ("missing_authorization_request".equals(error)) {
            model.addAttribute("errorMessage", "인가 요청 정보가 만료되었습니다. 클라이언트에서 로그인을 다시 시작해주세요.");
        }
        if (logout != null) {
            model.addAttribute("errorMessage", "로그아웃되었습니다.");
        }
        return "login"; // templates/login.html 렌더링
    }
}
