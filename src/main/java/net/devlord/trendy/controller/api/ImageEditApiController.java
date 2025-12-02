package net.devlord.trendy.controller.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devlord.trendy.model.dto.ImageEditRequest;
import net.devlord.trendy.model.dto.ImageEditResult;
import net.devlord.trendy.model.entity.User;
import net.devlord.trendy.service.ImageEditService;
import net.devlord.trendy.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST API Controller for AI Image Editing Tools
 * 
 * Endpoints:
 * - POST /api/image-edit/process - Process image editing request
 * - GET /api/image-edit/types - Get available edit types
 * - GET /api/image-edit/styles - Get available style presets
 * - GET /api/image-edit/color-presets - Get color grading presets
 */
@RestController
@RequestMapping("/api/image-edit")
@RequiredArgsConstructor
@Slf4j
public class ImageEditApiController {
    
    private final ImageEditService imageEditService;
    private final UserService userService;
    
    /**
     * Process image editing request
     * 
     * @param request Image edit request
     * @param authentication User authentication
     * @return Image edit result
     */
    @PostMapping("/process")
    public ResponseEntity<ImageEditResult> processImageEdit(
            @RequestBody ImageEditRequest request,
            Authentication authentication) {
        
        log.info("Processing image edit request: type={}, user={}", 
                request.getEditType(), authentication.getName());
        
        String username = authentication.getName();
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        ImageEditResult result = imageEditService.editImage(request, user);
        
        if (result.getSuccess()) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.badRequest().body(result);
        }
    }
    
    /**
     * Get all available edit types with descriptions and costs
     * 
     * @return List of edit types
     */
    @GetMapping("/types")
    public ResponseEntity<List<Map<String, Object>>> getAvailableEditTypes() {
        return ResponseEntity.ok(imageEditService.getAvailableEditTypes());
    }
    
    /**
     * Get available style presets for style transfer
     * 
     * @return List of style names
     */
    @GetMapping("/styles")
    public ResponseEntity<List<String>> getAvailableStyles() {
        return ResponseEntity.ok(imageEditService.getAvailableStyles());
    }
    
    /**
     * Get available color grading presets
     * 
     * @return List of color grading preset names
     */
    @GetMapping("/color-presets")
    public ResponseEntity<List<String>> getColorGradingPresets() {
        return ResponseEntity.ok(imageEditService.getColorGradingPresets());
    }
    
    /**
     * Health check endpoint
     * 
     * @return Service status
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        return ResponseEntity.ok(Map.of(
                "status", "healthy",
                "service", "Image Edit API",
                "timestamp", System.currentTimeMillis()
        ));
    }
}
