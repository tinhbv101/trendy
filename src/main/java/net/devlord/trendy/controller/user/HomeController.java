package net.devlord.trendy.controller.user;

import net.devlord.trendy.model.entity.Trend;
import net.devlord.trendy.service.TrendService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class HomeController {
    
    private final TrendService trendService;
    
    @GetMapping("/")
    public String home(Model model) {
        // Get featured trends (top 6)
        Page<Trend> featuredTrends = trendService.getAllActiveTrends(PageRequest.of(0, 6));
        model.addAttribute("trends", featuredTrends.getContent());
        model.addAttribute("pageTitle", "Home");
        return "user/home";
    }
}

