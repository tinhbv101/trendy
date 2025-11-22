package net.devlord.trendy.controller.user;

import net.devlord.trendy.model.entity.GeneratedImage;
import net.devlord.trendy.model.entity.Trend;
import net.devlord.trendy.model.enums.GenerationStatus;
import net.devlord.trendy.service.GenerateImageService;
import net.devlord.trendy.service.TrendService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Controller
@RequestMapping("/gallery")
@RequiredArgsConstructor
public class GalleryController {
    
    private final GenerateImageService generateImageService;
    private final TrendService trendService;
    
    @GetMapping
    public String userGallery(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(required = false) Long trendId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String sort,
            @PageableDefault(size = 12) Pageable pageable,
            Model model) {
        
        Page<GeneratedImage> images;
        
        // Parse status
        GenerationStatus statusEnum = null;
        if (status != null && !status.isEmpty()) {
            try {
                statusEnum = GenerationStatus.valueOf(status.toUpperCase());
            } catch (IllegalArgumentException e) {
                // Invalid status, ignore
            }
        }
        
        // Parse dates
        LocalDateTime startDateTime = (startDate != null) ? startDate.atStartOfDay() : null;
        LocalDateTime endDateTime = (endDate != null) ? endDate.atTime(LocalTime.MAX) : null;
        
        // Apply sorting
        Sort sortBy = Sort.by(Sort.Direction.DESC, "createdAt"); // Default: newest first
        if (sort != null && !sort.isEmpty()) {
            String[] parts = sort.split(":");
            if (parts.length == 2) {
                String field = parts[0];
                Sort.Direction direction = "asc".equalsIgnoreCase(parts[1]) 
                    ? Sort.Direction.ASC 
                    : Sort.Direction.DESC;
                sortBy = Sort.by(direction, field);
            }
        }
        
        Pageable pageableWithSort = PageRequest.of(
            pageable.getPageNumber(), 
            pageable.getPageSize(), 
            sortBy
        );
        
        // Apply search or filters
        if (search != null && !search.trim().isEmpty()) {
            images = generateImageService.searchUserImages(
                userDetails.getUsername(), search, pageableWithSort);
        } else if (trendId != null || statusEnum != null || startDateTime != null || endDateTime != null) {
            images = generateImageService.getUserImagesWithFilters(
                userDetails.getUsername(), trendId, statusEnum, 
                startDateTime, endDateTime, pageableWithSort);
        } else {
            images = generateImageService.getUserImagesByUsername(
                userDetails.getUsername(), pageableWithSort);
        }
        
        // Load all trends for filter dropdown
        List<Trend> allTrends = trendService.getAllActivePublicTrends();
        
        // Add attributes to model
        model.addAttribute("images", images);
        model.addAttribute("allTrends", allTrends);
        model.addAttribute("allStatuses", GenerationStatus.values());
        model.addAttribute("selectedTrendId", trendId);
        model.addAttribute("selectedStatus", status);
        model.addAttribute("selectedStartDate", startDate);
        model.addAttribute("selectedEndDate", endDate);
        model.addAttribute("selectedSearch", search);
        model.addAttribute("selectedSort", sort);
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

