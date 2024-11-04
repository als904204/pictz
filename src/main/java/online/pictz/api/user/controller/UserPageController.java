package online.pictz.api.user.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UserPageController {

    @GetMapping("/login")
    public String login(@AuthenticationPrincipal OAuth2User user) {
        if (user != null) {
            return "redirect:/";
        }
        return "/login";
    }

    @GetMapping("/profile")
    public String profile() {
        return "/profile";
    }

}
