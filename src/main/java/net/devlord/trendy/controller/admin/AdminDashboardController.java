package net.devlord.trendy.controller.admin;

import net.devlord.trendy.repository.GeneratedImageRepository;
import net.devlord.trendy.repository.TrendRepository;
import net.devlord.trendy.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminDashboardController {
    
    private final TrendRepository trendRepository;
    private final UserRepository userRepository;
    private final GeneratedImageRepository generatedImageRepository;
    
    @GetMapping
    public String dashboard(Model model) {
        return "redirect:/admin/dashboard";
    }
    
    @GetMapping("/dashboard")
    public String adminDashboard(Model model) {
        // Get statistics
        long totalTrends = trendRepository.count();
        long totalUsers = userRepository.count();
        long totalGeneratedImages = generatedImageRepository.count();
        
        model.addAttribute("totalTrends", totalTrends);
        model.addAttribute("totalUsers", totalUsers);
        model.addAttribute("totalGeneratedImages", totalGeneratedImages);
        model.addAttribute("pageTitle", "Admin Dashboard");
        
        return "admin/dashboard";
    }
}

