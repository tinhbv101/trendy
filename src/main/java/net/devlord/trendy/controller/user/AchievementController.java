package net.devlord.trendy.controller.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class AchievementController {
    
    @GetMapping("/achievements")
    public String viewAchievements(Model model) {
        model.addAttribute("pageTitle", "Achievements");
        return "user/achievements";
    }
}
