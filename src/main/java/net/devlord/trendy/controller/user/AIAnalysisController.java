package net.devlord.trendy.controller.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controller for AI Image Analysis page
 */
@Controller
@RequestMapping("/user/ai-analysis")
@RequiredArgsConstructor
@Slf4j
public class AIAnalysisController {
    
    @GetMapping
    public String showAnalysisPage(Model model) {
        model.addAttribute("pageTitle", "AI Image Analysis");
        return "user/ai-analysis";
    }
}
