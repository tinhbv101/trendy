package net.devlord.trendy.controller.user;

import net.devlord.trendy.model.entity.Trend;
import net.devlord.trendy.service.TrendService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
            @RequestParam(required = false, defaultValue = "popular") String sort,
            @PageableDefault(size = 12) Pageable pageable,
            Model model) {
        
        // Create pageable with sorting
        Pageable sortedPageable = createSortedPageable(pageable, sort);
        
        Page<Trend> trends;
        
        if (search != null && !search.isEmpty()) {
            trends = trendService.searchTrends(search, sortedPageable);
            model.addAttribute("search", search);
        } else if (category != null && !category.isEmpty()) {
            trends = trendService.getTrendsByCategory(category, sortedPageable);
            model.addAttribute("category", category);
        } else {
            trends = trendService.getAllActiveTrends(sortedPageable);
        }
        
        model.addAttribute("trends", trends);
        model.addAttribute("categories", trendService.getAllCategories());
        model.addAttribute("currentSort", sort);
        model.addAttribute("pageTitle", "Browse Trends");
        return "user/trend-list";
    }
    
    private Pageable createSortedPageable(Pageable pageable, String sortBy) {
        Sort sort;
        
        switch (sortBy) {
            case "newest":
                sort = Sort.by(Sort.Direction.DESC, "createdAt");
                break;
            case "oldest":
                sort = Sort.by(Sort.Direction.ASC, "createdAt");
                break;
            case "popular":
                sort = Sort.by(Sort.Direction.DESC, "usageCount");
                break;
            case "least-used":
                sort = Sort.by(Sort.Direction.ASC, "usageCount");
                break;
            default:
                sort = Sort.by(Sort.Direction.DESC, "usageCount");
        }
        
        return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);
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

