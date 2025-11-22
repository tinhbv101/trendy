package net.devlord.trendy.controller.user;

import jakarta.servlet.http.HttpServletRequest;
import net.devlord.trendy.model.entity.SharedImage;
import net.devlord.trendy.service.ShareService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@Slf4j
public class ShareController {
    
    private final ShareService shareService;
    
    /**
     * API: Create share link for an image
     */
    @PostMapping("/api/share/create")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> createShareLink(
            @RequestParam Long imageId,
            @RequestParam(required = false) Integer expiryDays,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        try {
            String token = shareService.createShareLink(
                imageId, 
                userDetails.getUsername(), 
                expiryDays
            );
            
            // Build full share URL
            String shareUrl = "/share/" + token;
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("token", token);
            response.put("shareUrl", shareUrl);
            response.put("message", "Share link created successfully");
            
            return ResponseEntity.ok(response);
        } catch (IllegalStateException e) {
            log.warn("Unauthorized share attempt for image {}: {}", imageId, e.getMessage());
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            
            return ResponseEntity.status(403).body(response);
        } catch (Exception e) {
            log.error("Error creating share link for image {}", imageId, e);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to create share link: " + e.getMessage());
            
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * API: Revoke share link
     */
    @PostMapping("/api/share/revoke/{token}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> revokeShareLink(
            @PathVariable String token,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        try {
            boolean revoked = shareService.revokeShareLink(token, userDetails.getUsername());
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", revoked);
            response.put("message", revoked ? "Share link revoked" : "Share link not found");
            
            return ResponseEntity.ok(response);
        } catch (IllegalStateException e) {
            log.warn("Unauthorized revoke attempt for token {}: {}", token, e.getMessage());
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            
            return ResponseEntity.status(403).body(response);
        } catch (Exception e) {
            log.error("Error revoking share link {}", token, e);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to revoke share link");
            
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * Public page: View shared image
     */
    @GetMapping("/share/{token}")
    public String viewSharedImage(
            @PathVariable String token,
            HttpServletRequest request,
            Model model) {
        
        return shareService.getSharedImage(token)
            .map(sharedImage -> {
                // Build absolute URLs for Open Graph
                String shareUrl = request.getRequestURL().toString();
                String baseUrl = request.getRequestURL().toString().replace(request.getRequestURI(), "");
                String imageUrl = baseUrl + "/images/" + sharedImage.getGeneratedImage().getOutputImagePath();
                
                model.addAttribute("sharedImage", sharedImage);
                model.addAttribute("pageTitle", "Shared Image - " + 
                                   sharedImage.getGeneratedImage().getTrend().getTrendName());
                model.addAttribute("shareUrl", shareUrl);
                model.addAttribute("imageUrl", imageUrl);
                return "user/shared-view";
            })
            .orElseGet(() -> {
                model.addAttribute("errorMessage", "This share link is invalid or has expired.");
                return "error/404";
            });
    }
    
    /**
     * User page: View my shared links
     */
    @GetMapping("/my-shares")
    public String viewMyShares(
            @AuthenticationPrincipal UserDetails userDetails,
            @PageableDefault(size = 12) Pageable pageable,
            Model model) {
        
        Page<SharedImage> shares = shareService.getUserSharedImages(
            userDetails.getUsername(), pageable);
        
        long activeShares = shareService.countUserActiveShares(userDetails.getUsername());
        
        model.addAttribute("shares", shares);
        model.addAttribute("activeShares", activeShares);
        model.addAttribute("pageTitle", "My Shared Links");
        
        return "user/my-shares";
    }
}

