package net.devlord.trendy.service;

import net.devlord.trendy.model.dto.ShareInfo;
import net.devlord.trendy.model.entity.GeneratedImage;
import net.devlord.trendy.model.entity.SharedImage;
import net.devlord.trendy.model.entity.User;
import net.devlord.trendy.repository.GeneratedImageRepository;
import net.devlord.trendy.repository.SharedImageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ShareService {
    
    private final SharedImageRepository sharedImageRepository;
    private final GeneratedImageRepository generatedImageRepository;
    private final UserService userService;
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    
    /**
     * Create or get existing share link for an image
     * @param imageId Generated image ID
     * @param username User creating the share
     * @param expiryDays Number of days until expiry (null for no expiry)
     * @return ShareInfo containing token, username, trend name, and share URL
     */
    @Transactional
    public ShareInfo createShareLink(Long imageId, String username, Integer expiryDays) {
        User user = userService.findByUsername(username)
            .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));
        
        GeneratedImage image = generatedImageRepository.findByIdWithUserAndTrend(imageId)
            .orElseThrow(() -> new IllegalArgumentException("Image not found: " + imageId));
        
        // Verify ownership
        if (!image.getUser().getId().equals(user.getId())) {
            throw new IllegalStateException("Cannot share another user's image");
        }
        
        // Check if active share already exists
        Optional<SharedImage> existingShare = sharedImageRepository
            .findActiveByGeneratedImageIdAndUserId(imageId, user.getId());
        
        String token;
        if (existingShare.isPresent() && existingShare.get().isAccessible()) {
            log.info("Reusing existing share link for image {} by user {}", imageId, username);
            token = existingShare.get().getShareToken();
        } else {
            // Create new share
            SharedImage sharedImage = new SharedImage();
            sharedImage.setGeneratedImage(image);
            sharedImage.setUser(user);
            sharedImage.setShareToken(generateSecureToken());
            
            if (expiryDays != null && expiryDays > 0) {
                sharedImage.setExpiresAt(LocalDateTime.now().plusDays(expiryDays));
            }
            
            sharedImage.setIsActive(true);
            sharedImage.setViewCount(0);
            
            sharedImageRepository.save(sharedImage);
            
            token = sharedImage.getShareToken();
            log.info("Created share link for image {} by user {}: {}", 
                     imageId, username, token);
        }
        
        // Build share URL
        String shareUrl = "/share/" + token;
        
        // Get trend name (ensure it's loaded)
        String trendName = image.getTrend().getTrendName();
        
        return new ShareInfo(token, username, trendName, shareUrl);
    }
    
    /**
     * Get shared image by token
     */
    @Transactional
    public Optional<SharedImage> getSharedImage(String token) {
        Optional<SharedImage> sharedImage = sharedImageRepository.findByShareTokenWithDetails(token);
        
        if (sharedImage.isPresent()) {
            SharedImage share = sharedImage.get();
            
            // Check if accessible
            if (!share.isAccessible()) {
                log.warn("Attempted to access expired or inactive share: {}", token);
                return Optional.empty();
            }
            
            // Increment view count
            share.incrementViewCount();
            sharedImageRepository.save(share);
            
            log.info("Share link accessed: {} (views: {})", token, share.getViewCount());
        }
        
        return sharedImage;
    }
    
    /**
     * Revoke (deactivate) a share link
     */
    @Transactional
    public boolean revokeShareLink(String token, String username) {
        User user = userService.findByUsername(username)
            .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));
        
        Optional<SharedImage> sharedImage = sharedImageRepository.findByShareToken(token);
        
        if (sharedImage.isEmpty()) {
            return false;
        }
        
        SharedImage share = sharedImage.get();
        
        // Verify ownership
        if (!share.getUser().getId().equals(user.getId())) {
            throw new IllegalStateException("Cannot revoke another user's share link");
        }
        
        share.setIsActive(false);
        sharedImageRepository.save(share);
        
        log.info("Revoked share link: {} by user {}", token, username);
        return true;
    }
    
    /**
     * Get user's shared images
     */
    @Transactional(readOnly = true)
    public Page<SharedImage> getUserSharedImages(String username, Pageable pageable) {
        User user = userService.findByUsername(username)
            .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));
        
        return sharedImageRepository.findByUserIdOrderByCreatedAtDesc(user.getId(), pageable);
    }
    
    /**
     * Count active shares by user
     */
    @Transactional(readOnly = true)
    public long countUserActiveShares(String username) {
        User user = userService.findByUsername(username)
            .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));
        
        return sharedImageRepository.countActiveByUserId(user.getId());
    }
    
    /**
     * Generate a secure random token for sharing
     */
    private String generateSecureToken() {
        byte[] randomBytes = new byte[32]; // 256 bits
        SECURE_RANDOM.nextBytes(randomBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
    }
    
    /**
     * Scheduled task to clean up expired shares (runs daily at 2 AM)
     */
    @Scheduled(cron = "0 0 2 * * *")
    @Transactional
    public void cleanupExpiredShares() {
        int deleted = sharedImageRepository.deleteExpiredShares(LocalDateTime.now());
        if (deleted > 0) {
            log.info("Cleaned up {} expired share links", deleted);
        }
    }
}

