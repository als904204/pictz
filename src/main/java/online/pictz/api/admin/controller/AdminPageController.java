package online.pictz.api.admin.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
@Controller
public class AdminPageController {

    @GetMapping("/settings")
    public String adminPage() {
        return "admin/settings";
    }

    @GetMapping("/suggests/{id}")
    public String suggestDetailPage(@PathVariable("id") Long id) {
        return "admin/suggest-detail";
    }
}
