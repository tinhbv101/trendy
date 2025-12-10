package net.devlord.trendy.controller.user;

import net.devlord.trendy.model.dto.UserProfileDTO;
import net.devlord.trendy.model.entity.User;
import net.devlord.trendy.model.entity.UserProfile;
import net.devlord.trendy.service.AchievementService;
import net.devlord.trendy.service.UserProfileService;
import net.devlord.trendy.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/profile")
@RequiredArgsConstructor
public class ProfileController {
    
    private final UserProfileService profileService;
    private final AchievementService achievementService;
    private final UserService userService;
    
    @GetMapping
    public String viewProfile(Authentication authentication, Model model) {
        String username = authentication.getName();
        User user = userService.findByUsername(username)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));
        
        UserProfile profile = profileService.getOrCreateProfile(user);
        UserProfileDTO dto = convertToDTO(profile);
        model.addAttribute("profile", dto);
        model.addAttribute("pageTitle", "My Profile");
        return "user/profile";
    }
    
    @GetMapping("/{userId}")
    public String viewUserProfile(@PathVariable Long userId, Model model) {
        UserProfile profile = profileService.getOrCreateProfile(userId);
        UserProfileDTO dto = convertToDTO(profile);
        model.addAttribute("profile", dto);
        model.addAttribute("pageTitle", profile.getUser().getUsername() + "'s Profile");
        return "user/profile";
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
