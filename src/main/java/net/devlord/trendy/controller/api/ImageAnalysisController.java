package net.devlord.trendy.controller.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devlord.trendy.model.dto.*;
import net.devlord.trendy.service.ImageAnalysisService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.LocaleResolver;

import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * REST API controller for AI-powered image analysis
 */
@RestController
@RequestMapping("/api/image")
@RequiredArgsConstructor
@Slf4j
public class ImageAnalysisController {
    
    private final ImageAnalysisService imageAnalysisService;
    private final LocaleResolver localeResolver;
    
    /**
     * Perform comprehensive image analysis
     * POST /api/image/analyze
     */
    @PostMapping("/analyze")
    public ResponseEntity<ImageAnalysisResult> analyzeImage(
            @RequestParam("file") MultipartFile file,
            HttpServletRequest request) {
        
        try {
            log.info("Analyzing image: {}", file.getOriginalFilename());
            
            Locale userLocale = localeResolver.resolveLocale(request);
            log.info("User locale: {}", userLocale);
            
            byte[] imageData = file.getBytes();
            ImageAnalysisResult result = imageAnalysisService.analyzeImage(imageData, userLocale);
            
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            log.error("Error analyzing image", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Generate tags only
     * POST /api/image/tags
     */
    @PostMapping("/tags")
    public ResponseEntity<Map<String, Object>> generateTags(
            @RequestParam("file") MultipartFile file) {
        
        try {
            log.info("Generating tags for image: {}", file.getOriginalFilename());
            
            byte[] imageData = file.getBytes();
            List<String> tags = imageAnalysisService.generateTags(imageData);
            
            Map<String, Object> response = new HashMap<>();
            response.put("tags", tags);
            response.put("count", tags.size());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Error generating tags", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Get image description
     * POST /api/image/describe
     */
    @PostMapping("/describe")
    public ResponseEntity<Map<String, String>> describeImage(
            @RequestParam("file") MultipartFile file) {
        
        try {
            log.info("Describing image: {}", file.getOriginalFilename());
            
            byte[] imageData = file.getBytes();
            String description = imageAnalysisService.getImageDescription(imageData);
            
            Map<String, String> response = new HashMap<>();
            response.put("description", description);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Error describing image", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Analyze artistic style
     * POST /api/image/style
     */
    @PostMapping("/style")
    public ResponseEntity<StyleAnalysis> analyzeStyle(
            @RequestParam("file") MultipartFile file) {
        
        try {
            log.info("Analyzing style for image: {}", file.getOriginalFilename());
            
            byte[] imageData = file.getBytes();
            StyleAnalysis style = imageAnalysisService.analyzeStyle(imageData);
            
            return ResponseEntity.ok(style);
            
        } catch (Exception e) {
            log.error("Error analyzing style", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Extract color palette
     * POST /api/image/colors
     */
    @PostMapping("/colors")
    public ResponseEntity<ColorPalette> extractColors(
            @RequestParam("file") MultipartFile file) {
        
        try {
            log.info("Extracting colors from image: {}", file.getOriginalFilename());
            
            byte[] imageData = file.getBytes();
            ColorPalette colors = imageAnalysisService.extractColors(imageData);
            
            return ResponseEntity.ok(colors);
            
        } catch (Exception e) {
            log.error("Error extracting colors", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Get edit suggestions
     * POST /api/image/suggestions
     */
    @PostMapping("/suggestions")
    public ResponseEntity<Map<String, Object>> getEditSuggestions(
            @RequestParam("file") MultipartFile file) {
        
        try {
            log.info("Getting edit suggestions for image: {}", file.getOriginalFilename());
            
            byte[] imageData = file.getBytes();
            List<EditSuggestion> suggestions = imageAnalysisService.getEditSuggestions(imageData);
            
            Map<String, Object> response = new HashMap<>();
            response.put("suggestions", suggestions);
            response.put("count", suggestions.size());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Error getting edit suggestions", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Health check
     * GET /api/image/health
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "ok");
        response.put("service", "image-analysis");
        response.put("available", imageAnalysisService.isAvailable());
        
        return ResponseEntity.ok(response);
    }
}
