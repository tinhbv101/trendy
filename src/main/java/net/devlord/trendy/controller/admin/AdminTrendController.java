package net.devlord.trendy.controller.admin;

import net.devlord.trendy.model.dto.TrendDTO;
import net.devlord.trendy.model.entity.Trend;
import net.devlord.trendy.service.FileStorageService;
import net.devlord.trendy.service.TrendService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/trends")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminTrendController {
    
    private final TrendService trendService;
    private final FileStorageService fileStorageService;
    
    @GetMapping
    public String listTrends(
            @PageableDefault(size = 10) Pageable pageable,
            Model model) {
        
        Page<Trend> trends = trendService.getAllTrends(pageable);
        model.addAttribute("trends", trends);
        model.addAttribute("pageTitle", "Manage Trends");
        return "admin/trend-list";
    }
    
    @GetMapping("/new")
    public String newTrendForm(Model model) {
        model.addAttribute("trend", new TrendDTO());
        model.addAttribute("pageTitle", "Create New Trend");
        model.addAttribute("isEdit", false);
        return "admin/trend-form";
    }
    
    @PostMapping
    public String createTrend(
            @Valid @ModelAttribute("trend") TrendDTO trendDTO,
            BindingResult result,
            @RequestParam(value = "thumbnailFile", required = false) MultipartFile thumbnailFile,
            @RequestParam(value = "exampleFiles", required = false) MultipartFile[] files,
            RedirectAttributes redirectAttributes) {
        
        if (result.hasErrors()) {
            return "admin/trend-form";
        }
        
        try {
            // Upload thumbnail to MinIO if provided
            if (thumbnailFile != null && !thumbnailFile.isEmpty()) {
                String thumbnailPath = fileStorageService.storeFile(thumbnailFile, "trends");
                trendDTO.setThumbnailPath(thumbnailPath);
            }
            
            Trend trend = trendService.createTrendFromDTO(trendDTO);
            
            // Upload example images to MinIO
            if (files != null && files.length > 0 && !files[0].isEmpty()) {
                fileStorageService.saveExampleImages(trend.getId(), files);
            }
            
            redirectAttributes.addFlashAttribute("success", "Trend created successfully!");
            return "redirect:/admin/trends";
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to create trend: " + e.getMessage());
            return "redirect:/admin/trends/new";
        }
    }
    
    @GetMapping("/{id}/edit")
    public String editTrendForm(@PathVariable Long id, Model model) {
        Trend trend = trendService.getTrendById(id);
        
        TrendDTO trendDTO = new TrendDTO();
        trendDTO.setId(trend.getId());
        trendDTO.setTrendName(trend.getTrendName());
        trendDTO.setDescription(trend.getDescription());
        trendDTO.setPromptTemplate(trend.getPromptTemplate());
        trendDTO.setCategory(trend.getCategory());
        trendDTO.setMaxInputImages(trend.getMaxInputImages());
        trendDTO.setOutputWidth(trend.getOutputWidth());
        trendDTO.setOutputHeight(trend.getOutputHeight());
        trendDTO.setThumbnailPath(trend.getThumbnailPath());
        trendDTO.setStatus(trend.getStatus());
        
        model.addAttribute("trend", trendDTO);
        model.addAttribute("pageTitle", "Edit Trend");
        model.addAttribute("isEdit", true);
        return "admin/trend-form";
    }
    
    @PostMapping("/{id}")
    public String updateTrend(
            @PathVariable Long id,
            @Valid @ModelAttribute("trend") TrendDTO trendDTO,
            BindingResult result,
            @RequestParam(value = "thumbnailFile", required = false) MultipartFile thumbnailFile,
            RedirectAttributes redirectAttributes) {
        
        if (result.hasErrors()) {
            return "admin/trend-form";
        }
        
        try {
            // Upload new thumbnail to MinIO if provided
            if (thumbnailFile != null && !thumbnailFile.isEmpty()) {
                String thumbnailPath = fileStorageService.storeFile(thumbnailFile, "trends");
                trendDTO.setThumbnailPath(thumbnailPath);
            }
            
            trendService.updateTrendFromDTO(id, trendDTO);
            redirectAttributes.addFlashAttribute("success", "Trend updated successfully!");
            return "redirect:/admin/trends";
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to update trend: " + e.getMessage());
            return "redirect:/admin/trends/" + id + "/edit";
        }
    }
    
    @PostMapping("/{id}/delete")
    public String deleteTrend(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            trendService.deleteTrend(id);
            redirectAttributes.addFlashAttribute("success", "Trend deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to delete trend: " + e.getMessage());
        }
        return "redirect:/admin/trends";
    }
    
    @GetMapping("/{id}/test")
    public String testTrendForm(@PathVariable Long id, Model model) {
        Trend trend = trendService.getTrendById(id);
        model.addAttribute("trend", trend);
        model.addAttribute("pageTitle", "Test Trend - " + trend.getTrendName());
        return "admin/test-trend";
    }
}

