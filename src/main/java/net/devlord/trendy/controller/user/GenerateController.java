package net.devlord.trendy.controller.user;

import net.devlord.trendy.model.entity.GeneratedImage;
import net.devlord.trendy.model.entity.Trend;
import net.devlord.trendy.model.enums.AIModel;
import net.devlord.trendy.model.enums.AspectRatio;
import net.devlord.trendy.service.GenerateImageService;
import net.devlord.trendy.service.TrendService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/generate")
@RequiredArgsConstructor
public class GenerateController {
    
    private final TrendService trendService;
    private final GenerateImageService generateImageService;
    
    @GetMapping("/{trendId}")
    public String generateForm(@PathVariable Long trendId, Model model) {
        Trend trend = trendService.getTrendById(trendId);
        model.addAttribute("trend", trend);
        model.addAttribute("aspectRatios", AspectRatio.values());
        model.addAttribute("aiModels", AIModel.values());
        model.addAttribute("pageTitle", "Generate Image - " + trend.getTrendName());
        return "user/generate";
    }
    
    @PostMapping("/{trendId}")
    public String generateImage(
            @PathVariable Long trendId,
            @RequestParam("inputImages") MultipartFile[] files,
            @RequestParam(value = "aspectRatio", required = false) String aspectRatioStr,
            @RequestParam(value = "aiModel", required = false) String aiModelStr,
            @AuthenticationPrincipal UserDetails userDetails,
            RedirectAttributes redirectAttributes) {
        
        try {
            // Validate that at least one file is uploaded
            if (files == null || files.length == 0 || files[0].isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Please upload at least one image");
                return "redirect:/generate/" + trendId;
            }
            
            // Parse aspect ratio and AI model (use null if not provided to use trend defaults)
            AspectRatio aspectRatio = aspectRatioStr != null && !aspectRatioStr.isEmpty() 
                ? AspectRatio.valueOf(aspectRatioStr) : null;
            AIModel aiModel = aiModelStr != null && !aiModelStr.isEmpty() 
                ? AIModel.valueOf(aiModelStr) : null;
            
            GeneratedImage image = generateImageService.generateImage(
                trendId, files, userDetails.getUsername(), aspectRatio, aiModel);
            
            redirectAttributes.addFlashAttribute("success", "Image generation started!");
            return "redirect:/generate/result/" + image.getId();
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to generate image: " + e.getMessage());
            return "redirect:/generate/" + trendId;
        }
    }
    
    @GetMapping("/result/{imageId}")
    public String viewResult(
            @PathVariable Long imageId, 
            @AuthenticationPrincipal UserDetails userDetails,
            Model model,
            RedirectAttributes redirectAttributes) {
        try {
            GeneratedImage image = generateImageService.getGeneratedImage(imageId);
            
            // Check if the image belongs to the current user
            if (!image.getUser().getUsername().equals(userDetails.getUsername())) {
                redirectAttributes.addFlashAttribute("error", "You don't have permission to view this image");
                return "redirect:/gallery";
            }
            
            model.addAttribute("image", image);
            model.addAttribute("pageTitle", "Generation Result");
            
            // Extract original image path from aiParameters if this is an edited image
            if (image.getAiParameters() != null && !image.getAiParameters().isEmpty()) {
                try {
                    // Parse JSON to extract originalImage
                    com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
                    com.fasterxml.jackson.databind.JsonNode params = mapper.readTree(image.getAiParameters());
                    
                    if (params.has("originalImage")) {
                        String originalImagePath = params.get("originalImage").asText();
                        model.addAttribute("originalImagePath", originalImagePath);
                    }
                } catch (Exception e) {
                    // If parsing fails, just continue without comparison
                    // This is not a critical error
                }
            }
            
            return "user/result";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Image not found");
            return "redirect:/gallery";
        }
    }
}

