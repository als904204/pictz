package online.pictz.api.user.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UserPageController {

    @GetMapping("/login")
    public String login() {
        return "/login";
    }

    @GetMapping("/profile")
    public String profile() {
        return "/profile";
    }

}
