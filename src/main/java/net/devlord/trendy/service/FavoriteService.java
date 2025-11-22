package net.devlord.trendy.service;

import net.devlord.trendy.model.entity.FavoriteImage;
import net.devlord.trendy.model.entity.FavoriteTrend;
import net.devlord.trendy.model.entity.GeneratedImage;
import net.devlord.trendy.model.entity.Trend;
import net.devlord.trendy.model.entity.User;
import net.devlord.trendy.repository.FavoriteImageRepository;
import net.devlord.trendy.repository.FavoriteTrendRepository;
import net.devlord.trendy.repository.GeneratedImageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class FavoriteService {
    
    private final FavoriteTrendRepository favoriteTrendRepository;
    private final FavoriteImageRepository favoriteImageRepository;
    private final GeneratedImageRepository generatedImageRepository;
    private final UserService userService;
    private final TrendService trendService;
    
    @Transactional
    public boolean toggleFavorite(String username, Long trendId) {
        User user = userService.findByUsername(username)
            .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));
        
        Trend trend = trendService.getTrendById(trendId);
        
        // Check if already favorited
        if (favoriteTrendRepository.existsByUserIdAndTrendId(user.getId(), trendId)) {
            // Remove from favorites
            favoriteTrendRepository.deleteByUserIdAndTrendId(user.getId(), trendId);
            log.info("Removed trend {} from favorites for user {}", trendId, username);
            return false; // Removed
        } else {
            // Add to favorites
            FavoriteTrend favoriteTrend = new FavoriteTrend();
            favoriteTrend.setUser(user);
            favoriteTrend.setTrend(trend);
            favoriteTrendRepository.save(favoriteTrend);
            log.info("Added trend {} to favorites for user {}", trendId, username);
            return true; // Added
        }
    }
    
    @Transactional(readOnly = true)
    public boolean isFavorite(String username, Long trendId) {
        User user = userService.findByUsername(username)
            .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));
        
        return favoriteTrendRepository.existsByUserIdAndTrendId(user.getId(), trendId);
    }
    
    @Transactional(readOnly = true)
    public Page<FavoriteTrend> getUserFavorites(String username, Pageable pageable) {
        User user = userService.findByUsername(username)
            .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));
        
        return favoriteTrendRepository.findByUserIdOrderByCreatedAtDesc(user.getId(), pageable);
    }
    
    @Transactional(readOnly = true)
    public List<Long> getUserFavoriteTrendIds(String username) {
        User user = userService.findByUsername(username)
            .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));
        
        return favoriteTrendRepository.findTrendIdsByUserId(user.getId());
    }
    
    @Transactional(readOnly = true)
    public long countUserFavorites(String username) {
        User user = userService.findByUsername(username)
            .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));
        
        return favoriteTrendRepository.countByUserId(user.getId());
    }
    
    // ============= Favorite Images Methods =============
    
    @Transactional
    public boolean toggleFavoriteImage(String username, Long imageId) {
        User user = userService.findByUsername(username)
            .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));
        
        GeneratedImage image = generatedImageRepository.findById(imageId)
            .orElseThrow(() -> new IllegalArgumentException("Generated image not found: " + imageId));
        
        // Verify ownership
        if (!image.getUser().getId().equals(user.getId())) {
            throw new IllegalStateException("Cannot favorite another user's image");
        }
        
        // Check if already favorited
        if (favoriteImageRepository.existsByUserIdAndGeneratedImageId(user.getId(), imageId)) {
            // Remove from favorites
            favoriteImageRepository.deleteByUserIdAndGeneratedImageId(user.getId(), imageId);
            log.info("Removed image {} from favorites for user {}", imageId, username);
            return false; // Removed
        } else {
            // Add to favorites
            FavoriteImage favoriteImage = new FavoriteImage();
            favoriteImage.setUser(user);
            favoriteImage.setGeneratedImage(image);
            favoriteImageRepository.save(favoriteImage);
            log.info("Added image {} to favorites for user {}", imageId, username);
            return true; // Added
        }
    }
    
    @Transactional(readOnly = true)
    public boolean isFavoriteImage(String username, Long imageId) {
        User user = userService.findByUsername(username)
            .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));
        
        return favoriteImageRepository.existsByUserIdAndGeneratedImageId(user.getId(), imageId);
    }
    
    @Transactional(readOnly = true)
    public Page<FavoriteImage> getUserFavoriteImages(String username, Pageable pageable) {
        User user = userService.findByUsername(username)
            .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));
        
        return favoriteImageRepository.findByUserIdOrderByCreatedAtDesc(user.getId(), pageable);
    }
    
    @Transactional(readOnly = true)
    public List<Long> getUserFavoriteImageIds(String username) {
        User user = userService.findByUsername(username)
            .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));
        
        return favoriteImageRepository.findImageIdsByUserId(user.getId());
    }
    
    @Transactional(readOnly = true)
    public long countUserFavoriteImages(String username) {
        User user = userService.findByUsername(username)
            .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));
        
        return favoriteImageRepository.countByUserId(user.getId());
    }
}

