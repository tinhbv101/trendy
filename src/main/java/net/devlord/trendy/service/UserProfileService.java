package net.devlord.trendy.service;

import net.devlord.trendy.model.entity.User;
import net.devlord.trendy.model.entity.UserProfile;
import net.devlord.trendy.repository.UserProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserProfileService {
    
    private final UserProfileRepository userProfileRepository;
    private final AchievementService achievementService;
    
    @Transactional
    public UserProfile getOrCreateProfile(User user) {
        return userProfileRepository.findByUser(user)
            .orElseGet(() -> createProfile(user));
    }
    
    @Transactional
    public UserProfile getOrCreateProfile(Long userId) {
        return userProfileRepository.findByUserId(userId)
            .orElseGet(() -> {
                UserProfile profile = new UserProfile();
                User user = new User();
                user.setId(userId);
                profile.setUser(user);
                return userProfileRepository.save(profile);
            });
    }
    
    private UserProfile createProfile(User user) {
        UserProfile profile = new UserProfile();
        profile.setUser(user);
        profile.setLevel(1);
        profile.setCurrentXp(0);
        profile.setTotalXp(0);
        return userProfileRepository.save(profile);
    }
    
    @Transactional
    public void addXp(Long userId, int xp, String reason) {
        UserProfile profile = getOrCreateProfile(userId);
        boolean leveledUp = profile.addXp(xp);
        userProfileRepository.save(profile);
        
        if (leveledUp) {
            log.info("User {} leveled up to level {}", userId, profile.getLevel());
            // Could trigger level up notification here
        }
        
        log.info("User {} gained {} XP for: {}", userId, xp, reason);
    }
    
    @Transactional
    public void incrementGenerations(Long userId) {
        UserProfile profile = getOrCreateProfile(userId);
        profile.setTotalGenerations(profile.getTotalGenerations() + 1);
        updateStreak(profile);
        userProfileRepository.save(profile);
        
        // Check achievements
        achievementService.checkGenerationAchievements(userId, profile.getTotalGenerations());
    }
    
    @Transactional
    public void incrementLikesReceived(Long userId) {
        UserProfile profile = getOrCreateProfile(userId);
        profile.setTotalLikesReceived(profile.getTotalLikesReceived() + 1);
        userProfileRepository.save(profile);
        
        // Check achievements
        achievementService.checkLikesAchievements(userId, profile.getTotalLikesReceived());
    }
    
    @Transactional
    public void incrementShares(Long userId) {
        UserProfile profile = getOrCreateProfile(userId);
        profile.setTotalShares(profile.getTotalShares() + 1);
        userProfileRepository.save(profile);
        
        // Check achievements
        achievementService.checkShareAchievements(userId, profile.getTotalShares());
    }
    
    @Transactional
    public void incrementFavorites(Long userId) {
        UserProfile profile = getOrCreateProfile(userId);
        profile.setTotalFavorites(profile.getTotalFavorites() + 1);
        userProfileRepository.save(profile);
        
        // Check achievements
        achievementService.checkFavoriteAchievements(userId, profile.getTotalFavorites());
    }
    
    @Transactional
    public void incrementUniqueTrends(Long userId) {
        UserProfile profile = getOrCreateProfile(userId);
        profile.setUniqueTrendsUsed(profile.getUniqueTrendsUsed() + 1);
        userProfileRepository.save(profile);
        
        // Check achievements
        achievementService.checkTrendAchievements(userId, profile.getUniqueTrendsUsed());
    }
    
    @Transactional
    public void incrementUniqueModels(Long userId) {
        UserProfile profile = getOrCreateProfile(userId);
        profile.setUniqueModelsUsed(profile.getUniqueModelsUsed() + 1);
        userProfileRepository.save(profile);
    }
    
    private void updateStreak(UserProfile profile) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime lastActivity = profile.getLastActivityDate();
        
        if (lastActivity == null) {
            profile.setCurrentStreak(1);
            profile.setLongestStreak(1);
        } else {
            long daysBetween = ChronoUnit.DAYS.between(lastActivity.toLocalDate(), now.toLocalDate());
            
            if (daysBetween == 0) {
                // Same day, no change
            } else if (daysBetween == 1) {
                // Consecutive day
                profile.setCurrentStreak(profile.getCurrentStreak() + 1);
                if (profile.getCurrentStreak() > profile.getLongestStreak()) {
                    profile.setLongestStreak(profile.getCurrentStreak());
                }
                
                // Check streak achievements
                achievementService.checkStreakAchievements(profile.getUser().getId(), profile.getCurrentStreak());
            } else {
                // Streak broken
                profile.setCurrentStreak(1);
            }
        }
        
        profile.setLastActivityDate(now);
    }
    
    @Transactional(readOnly = true)
    public Page<UserProfile> getLeaderboard(Pageable pageable) {
        return userProfileRepository.findTopByTotalXp(pageable);
    }
    
    @Transactional(readOnly = true)
    public long getUserRank(Long userId) {
        UserProfile profile = userProfileRepository.findByUserId(userId)
            .orElse(null);
        
        if (profile == null) {
            return -1;
        }
        
        return userProfileRepository.countUsersWithHigherXp(profile.getTotalXp()) + 1;
    }
}
