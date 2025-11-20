package net.devlord.trendy.controller.user;

import net.devlord.trendy.model.entity.Trend;
import net.devlord.trendy.service.TrendService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class HomeController {
    
    private final TrendService trendService;
    
    @GetMapping("/")
    public String home(Model model) {
        // Get featured trends sorted by usage count (most used first)
        List<Trend> featuredTrends = trendService.getFeaturedTrends(6);
        model.addAttribute("trends", featuredTrends);
        model.addAttribute("pageTitle", "Home");
        return "user/home";
    }
}

