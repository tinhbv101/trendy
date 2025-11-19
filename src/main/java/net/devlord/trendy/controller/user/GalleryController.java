package net.devlord.trendy.controller.user;

import net.devlord.trendy.model.entity.GeneratedImage;
import net.devlord.trendy.service.GenerateImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/gallery")
@RequiredArgsConstructor
public class GalleryController {
    
    private final GenerateImageService generateImageService;
    
    @GetMapping
    public String userGallery(
            @AuthenticationPrincipal UserDetails userDetails,
            @PageableDefault(size = 12) Pageable pageable,
            Model model) {
        
        Page<GeneratedImage> images = generateImageService.getUserImagesByUsername(
            userDetails.getUsername(), pageable);
        
        model.addAttribute("images", images);
        model.addAttribute("pageTitle", "My Gallery");
        return "user/gallery";
    }
    
    @PostMapping("/delete/{imageId}")
    public String deleteImage(
            @PathVariable Long imageId,
            @AuthenticationPrincipal UserDetails userDetails,
            RedirectAttributes redirectAttributes) {
        
        try {
            generateImageService.deleteGeneratedImage(imageId, userDetails.getUsername());
            redirectAttributes.addFlashAttribute("success", "Image deleted successfully!");
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to delete image: " + e.getMessage());
        }
        
        return "redirect:/gallery";
    }
}

