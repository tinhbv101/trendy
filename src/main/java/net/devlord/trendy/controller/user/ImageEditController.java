package net.devlord.trendy.controller.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devlord.trendy.model.dto.ImageEditRequest;
import net.devlord.trendy.model.dto.ImageEditResult;
import net.devlord.trendy.model.entity.GeneratedImage;
import net.devlord.trendy.model.entity.User;
import net.devlord.trendy.repository.GeneratedImageRepository;
import net.devlord.trendy.service.ImageEditService;
import net.devlord.trendy.service.UserService;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Controller for AI Image Editing Tools UI
 */
@Controller
@RequestMapping("/user/image-edit")
@RequiredArgsConstructor
@Slf4j
public class ImageEditController {
    
    private final ImageEditService imageEditService;
    private final GeneratedImageRepository generatedImageRepository;
    private final UserService userService;
    
    /**
     * Show image editing tools page
     */
    @GetMapping
    public String showImageEditPage(Model model, Authentication authentication) {
        try {
            model.addAttribute("editTypes", imageEditService.getAvailableEditTypes());
            model.addAttribute("styles", imageEditService.getAvailableStyles());
            model.addAttribute("colorPresets", imageEditService.getColorGradingPresets());
            
            // Get user's recent images
            String username = authentication.getName();
            User user = userService.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            
            try {
                model.addAttribute("recentImages", 
                        generatedImageRepository.findTop20ByUserOrderByCreatedAtDesc(user, PageRequest.of(0, 20)));
            } catch (Exception e) {
                log.warn("Error loading recent images: {}", e.getMessage());
                model.addAttribute("recentImages", java.util.Collections.emptyList());
            }
            
            return "user/image-edit";
        } catch (Exception e) {
            log.error("Error loading image edit page: {}", e.getMessage(), e);
            model.addAttribute("error", "Error loading page: " + e.getMessage());
            return "error/500";
        }
    }
    
    /**
     * Show edit page for specific image
     */
    @GetMapping("/{imageId}")
    public String showEditImagePage(
            @PathVariable Long imageId,
            Model model,
            Authentication authentication) {
        
        String username = authentication.getName();
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        GeneratedImage image = generatedImageRepository.findById(imageId)
                .orElseThrow(() -> new RuntimeException("Image not found"));
        
        // Check if user owns the image
        if (!image.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Access denied");
        }
        
        model.addAttribute("image", image);
        model.addAttribute("editTypes", imageEditService.getAvailableEditTypes());
        model.addAttribute("styles", imageEditService.getAvailableStyles());
        model.addAttribute("colorPresets", imageEditService.getColorGradingPresets());
        
        return "user/image-edit-single";
    }
    
    /**
     * Process image edit (form submission)
     */
    @PostMapping("/process")
    public String processImageEdit(
            @ModelAttribute ImageEditRequest request,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {
        
        try {
            String username = authentication.getName();
            User user = userService.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            
            ImageEditResult result = imageEditService.editImage(request, user);
            
            if (result.getSuccess()) {
                // Convert path to URL (e.g., "generated/xxx.png" -> "/images/generated/xxx.png")
                String imageUrl = result.getImageUrl();
                if (imageUrl != null && !imageUrl.startsWith("/images/")) {
                    imageUrl = "/images/" + imageUrl;
                }
                
                redirectAttributes.addFlashAttribute("success", 
                        "Image edited successfully! Processing time: " + 
                        result.getProcessingTimeMs() + "ms");
                redirectAttributes.addFlashAttribute("editedImageUrl", imageUrl);
                redirectAttributes.addFlashAttribute("editedImageId", result.getImageId());
            } else {
                redirectAttributes.addFlashAttribute("error", 
                        "Failed to edit image: " + result.getErrorMessage());
            }
            
        } catch (Exception e) {
            log.error("Error processing image edit: {}", e.getMessage(), e);
            redirectAttributes.addFlashAttribute("error", 
                    "Error: " + e.getMessage());
        }
        
        return "redirect:/user/image-edit";
    }
}
