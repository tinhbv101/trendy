package net.devlord.trendy.controller.user;

import net.devlord.trendy.model.entity.FavoriteImage;
import net.devlord.trendy.model.entity.FavoriteTrend;
import net.devlord.trendy.service.FavoriteService;
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
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@Slf4j
public class FavoriteController {
    
    private final FavoriteService favoriteService;
    
    @PostMapping("/favorite/{trendId}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> toggleFavorite(
            @PathVariable Long trendId,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        try {
            boolean isFavorited = favoriteService.toggleFavorite(userDetails.getUsername(), trendId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("favorited", isFavorited);
            response.put("message", isFavorited ? "Added to favorites" : "Removed from favorites");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error toggling favorite for trend {}", trendId, e);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to update favorite: " + e.getMessage());
            
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @GetMapping("/favorites")
    public String viewFavorites(
            @AuthenticationPrincipal UserDetails userDetails,
            @PageableDefault(size = 12) Pageable pageable,
            Model model) {
        
        Page<FavoriteTrend> favorites = favoriteService.getUserFavorites(
            userDetails.getUsername(), pageable);
        
        long totalFavorites = favoriteService.countUserFavorites(userDetails.getUsername());
        
        model.addAttribute("favorites", favorites);
        model.addAttribute("totalFavorites", totalFavorites);
        model.addAttribute("pageTitle", "My Favorites");
        
        return "user/favorites";
    }
    
    @GetMapping("/api/favorites/ids")
    @ResponseBody
    public ResponseEntity<List<Long>> getFavoriteTrendIds(
            @AuthenticationPrincipal UserDetails userDetails) {
        
        try {
            List<Long> favoriteTrendIds = favoriteService.getUserFavoriteTrendIds(
                userDetails.getUsername());
            return ResponseEntity.ok(favoriteTrendIds);
        } catch (Exception e) {
            log.error("Error getting favorite trend IDs", e);
            return ResponseEntity.badRequest().build();
        }
    }
    
    // ============= Favorite Images Endpoints =============
    
    @PostMapping("/api/favorites/image/{imageId}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> toggleFavoriteImage(
            @PathVariable Long imageId,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        try {
            boolean isFavorited = favoriteService.toggleFavoriteImage(
                userDetails.getUsername(), imageId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("favorited", isFavorited);
            response.put("message", isFavorited ? "Added to favorites" : "Removed from favorites");
            
            return ResponseEntity.ok(response);
        } catch (IllegalStateException e) {
            log.warn("Unauthorized favorite attempt for image {}: {}", imageId, e.getMessage());
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            
            return ResponseEntity.status(403).body(response);
        } catch (Exception e) {
            log.error("Error toggling favorite for image {}", imageId, e);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to update favorite: " + e.getMessage());
            
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @GetMapping("/api/favorites/images/ids")
    @ResponseBody
    public ResponseEntity<List<Long>> getFavoriteImageIds(
            @AuthenticationPrincipal UserDetails userDetails) {
        
        try {
            List<Long> favoriteImageIds = favoriteService.getUserFavoriteImageIds(
                userDetails.getUsername());
            return ResponseEntity.ok(favoriteImageIds);
        } catch (Exception e) {
            log.error("Error getting favorite image IDs", e);
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/favorites/images")
    public String viewFavoriteImages(
            @AuthenticationPrincipal UserDetails userDetails,
            @PageableDefault(size = 12) Pageable pageable,
            Model model) {
        
        Page<FavoriteImage> favoriteImages = favoriteService.getUserFavoriteImages(
            userDetails.getUsername(), pageable);
        
        long totalFavoriteImages = favoriteService.countUserFavoriteImages(
            userDetails.getUsername());
        
        model.addAttribute("favoriteImages", favoriteImages);
        model.addAttribute("totalFavoriteImages", totalFavoriteImages);
        model.addAttribute("pageTitle", "My Favorite Images");
        
        return "user/favorite-images";
    }
}

