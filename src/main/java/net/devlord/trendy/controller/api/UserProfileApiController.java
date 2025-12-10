package net.devlord.trendy.controller.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import net.devlord.trendy.model.dto.UserProfileDTO;
import net.devlord.trendy.model.entity.User;
import net.devlord.trendy.model.entity.UserProfile;
import net.devlord.trendy.service.AchievementService;
import net.devlord.trendy.service.UserProfileService;
import net.devlord.trendy.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
@Tag(name = "User Profile", description = "User profile and XP management APIs")
public class UserProfileApiController {
    
    private final UserProfileService profileService;
    private final AchievementService achievementService;
    private final UserService userService;
    
    @Operation(summary = "Get current user profile")
    @GetMapping
    public ResponseEntity<UserProfileDTO> getCurrentUserProfile(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated() || 
            "anonymousUser".equals(authentication.getName())) {
            return ResponseEntity.status(401).build();
        }
        
        String username = authentication.getName();
        User user = userService.findByUsername(username)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));
        
        UserProfile profile = profileService.getOrCreateProfile(user);
        return ResponseEntity.ok(convertToDTO(profile));
    }
    
    @Operation(summary = "Get user profile by ID")
    @GetMapping("/{userId}")
    public ResponseEntity<UserProfileDTO> getUserProfile(@PathVariable Long userId) {
        UserProfile profile = profileService.getOrCreateProfile(userId);
        return ResponseEntity.ok(convertToDTO(profile));
    }
    
    @Operation(summary = "Get leaderboard")
    @GetMapping("/leaderboard")
    public ResponseEntity<Page<UserProfileDTO>> getLeaderboard(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        Pageable pageable = PageRequest.of(page, Math.min(size, 100));
        Page<UserProfile> leaderboard = profileService.getLeaderboard(pageable);
        return ResponseEntity.ok(leaderboard.map(this::convertToDTO));
    }
    
    @Operation(summary = "Get user rank")
    @GetMapping("/rank")
    public ResponseEntity<Long> getCurrentUserRank(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated() || 
            "anonymousUser".equals(authentication.getName())) {
            return ResponseEntity.status(401).build();
        }
        
        String username = authentication.getName();
        User user = userService.findByUsername(username)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));
        
        long rank = profileService.getUserRank(user.getId());
        return ResponseEntity.ok(rank);
    }
    
    @Operation(summary = "Get user rank by ID")
    @GetMapping("/{userId}/rank")
    public ResponseEntity<Long> getUserRank(@PathVariable Long userId) {
        long rank = profileService.getUserRank(userId);
        return ResponseEntity.ok(rank);
    }
    
    private UserProfileDTO convertToDTO(UserProfile profile) {
        UserProfileDTO dto = new UserProfileDTO();
        dto.setId(profile.getId());
        dto.setUserId(profile.getUser().getId());
        dto.setUsername(profile.getUser().getUsername());
        dto.setLevel(profile.getLevel());
        dto.setCurrentXp(profile.getCurrentXp());
        dto.setTotalXp(profile.getTotalXp());
        dto.setXpForNextLevel(profile.getXpForNextLevel());
        dto.setLevelProgress(profile.getLevelProgress());
        dto.setTotalGenerations(profile.getTotalGenerations());
        dto.setTotalLikesReceived(profile.getTotalLikesReceived());
        dto.setTotalShares(profile.getTotalShares());
        dto.setTotalFavorites(profile.getTotalFavorites());
        dto.setCurrentStreak(profile.getCurrentStreak());
        dto.setLongestStreak(profile.getLongestStreak());
        dto.setUniqueTrendsUsed(profile.getUniqueTrendsUsed());
        dto.setUniqueModelsUsed(profile.getUniqueModelsUsed());
        dto.setProfilePicture(profile.getProfilePicture());
        dto.setBio(profile.getBio());
        dto.setTitle(profile.getTitle());
        dto.setRank(profileService.getUserRank(profile.getUser().getId()));
        dto.setCompletedAchievements(achievementService.getCompletedAchievementCount(profile.getUser().getId()));
        dto.setTotalAchievements((long) net.devlord.trendy.model.enums.AchievementType.values().length);
        return dto;
    }
}
