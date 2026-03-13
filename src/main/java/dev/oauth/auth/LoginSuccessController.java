package dev.oauth.auth;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginSuccessController {

    @GetMapping("/login/success")
    public String loginSuccess() {
        return "redirect:/login?error=missing_authorization_request";
    }
}
