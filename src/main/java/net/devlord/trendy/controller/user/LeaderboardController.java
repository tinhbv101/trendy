package net.devlord.trendy.controller.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class LeaderboardController {
    
    @GetMapping("/leaderboard")
    public String viewLeaderboard(Model model) {
        model.addAttribute("pageTitle", "Leaderboard");
        return "user/leaderboard";
    }
}
