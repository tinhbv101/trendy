package net.devlord.trendy.controller.admin;

import net.devlord.trendy.model.entity.AdConfig;
import net.devlord.trendy.model.enums.AdPosition;
import net.devlord.trendy.service.AdConfigService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin/ads")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
@Slf4j
public class AdminAdController {
    
    private final AdConfigService adConfigService;
    
    /**
     * List all ad configurations
     */
    @GetMapping
    public String listAds(Model model) {
        List<AdConfig> ads = adConfigService.getAllAds();
        model.addAttribute("ads", ads);
        model.addAttribute("pageTitle", "Ad Management");
        return "admin/ad-list";
    }
    
    /**
     * Show form to create new ad
     */
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("ad", new AdConfig());
        model.addAttribute("adPositions", AdPosition.values());
        model.addAttribute("pageTitle", "Create New Ad");
        model.addAttribute("isEdit", false);
        return "admin/ad-form";
    }
    
    /**
     * Create new ad configuration
     */
    @PostMapping
    public String createAd(@ModelAttribute AdConfig adConfig, RedirectAttributes redirectAttributes) {
        try {
            adConfigService.createAd(adConfig);
            redirectAttributes.addFlashAttribute("success", "Ad configuration created successfully!");
            log.info("Ad configuration created: {}", adConfig.getAdName());
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            log.error("Error creating ad configuration: {}", e.getMessage());
            return "redirect:/admin/ads/new";
        }
        return "redirect:/admin/ads";
    }
    
    /**
     * Show form to edit existing ad
     */
    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        return adConfigService.getAdById(id)
                .map(ad -> {
                    model.addAttribute("ad", ad);
                    model.addAttribute("adPositions", AdPosition.values());
                    model.addAttribute("pageTitle", "Edit Ad");
                    model.addAttribute("isEdit", true);
                    return "admin/ad-form";
                })
                .orElseGet(() -> {
                    redirectAttributes.addFlashAttribute("error", "Ad not found!");
                    return "redirect:/admin/ads";
                });
    }
    
    /**
     * Update existing ad configuration
     */
    @PostMapping("/{id}")
    public String updateAd(@PathVariable Long id, @ModelAttribute AdConfig adConfig, 
                          RedirectAttributes redirectAttributes) {
        try {
            adConfigService.updateAd(id, adConfig);
            redirectAttributes.addFlashAttribute("success", "Ad configuration updated successfully!");
            log.info("Ad configuration updated: {}", id);
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            log.error("Error updating ad configuration: {}", e.getMessage());
            return "redirect:/admin/ads/" + id + "/edit";
        }
        return "redirect:/admin/ads";
    }
    
    /**
     * Delete ad configuration
     */
    @PostMapping("/{id}/delete")
    public String deleteAd(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            adConfigService.deleteAd(id);
            redirectAttributes.addFlashAttribute("success", "Ad configuration deleted successfully!");
            log.info("Ad configuration deleted: {}", id);
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            log.error("Error deleting ad configuration: {}", e.getMessage());
        }
        return "redirect:/admin/ads";
    }
    
    /**
     * Toggle ad active status
     */
    @PostMapping("/{id}/toggle")
    public String toggleAdStatus(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            AdConfig ad = adConfigService.toggleAdStatus(id);
            String status = ad.getIsActive() ? "enabled" : "disabled";
            redirectAttributes.addFlashAttribute("success", "Ad configuration " + status + " successfully!");
            log.info("Ad configuration status toggled: {} - {}", id, status);
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            log.error("Error toggling ad status: {}", e.getMessage());
        }
        return "redirect:/admin/ads";
    }
}
