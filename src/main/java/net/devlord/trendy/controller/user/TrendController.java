package net.devlord.trendy.controller.user;

import net.devlord.trendy.model.entity.Trend;
import net.devlord.trendy.service.TrendService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/trends")
@RequiredArgsConstructor
public class TrendController {
    
    private final TrendService trendService;
    
    @GetMapping
    public String listTrends(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String search,
            @PageableDefault(size = 12) Pageable pageable,
            Model model) {
        
        Page<Trend> trends;
        
        if (search != null && !search.isEmpty()) {
            trends = trendService.searchTrends(search, pageable);
            model.addAttribute("search", search);
        } else if (category != null && !category.isEmpty()) {
            trends = trendService.getTrendsByCategory(category, pageable);
            model.addAttribute("category", category);
        } else {
            trends = trendService.getAllActiveTrends(pageable);
        }
        
        model.addAttribute("trends", trends);
        model.addAttribute("categories", trendService.getAllCategories());
        model.addAttribute("pageTitle", "Browse Trends");
        return "user/trend-list";
    }
    
    @GetMapping("/{id}")
    public String trendDetail(@PathVariable Long id, Model model) {
        Trend trend = trendService.getTrendById(id);
        model.addAttribute("trend", trend);
        model.addAttribute("examples", trendService.getTrendExamples(id));
        model.addAttribute("pageTitle", trend.getTrendName());
        return "user/trend-detail";
    }
}

