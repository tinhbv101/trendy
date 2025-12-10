package net.devlord.trendy.service;

import net.devlord.trendy.model.entity.User;
import net.devlord.trendy.model.entity.UserAchievement;
import net.devlord.trendy.model.enums.AchievementType;
import net.devlord.trendy.repository.UserAchievementRepository;
import net.devlord.trendy.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AchievementService {
    
    private final UserAchievementRepository achievementRepository;
    private final UserRepository userRepository;
    
    @Transactional
    public void checkGenerationAchievements(Long userId, int totalGenerations) {
        if (totalGenerations == 1) {
            unlockAchievement(userId, AchievementType.FIRST_GENERATION);
        } else if (totalGenerations == 10) {
            unlockAchievement(userId, AchievementType.GENERATION_10);
        } else if (totalGenerations == 50) {
            unlockAchievement(userId, AchievementType.GENERATION_50);
        } else if (totalGenerations == 100) {
            unlockAchievement(userId, AchievementType.GENERATION_100);
        } else if (totalGenerations == 500) {
            unlockAchievement(userId, AchievementType.GENERATION_500);
        } else if (totalGenerations == 1000) {
            unlockAchievement(userId, AchievementType.GENERATION_1000);
        }
    }
    
    @Transactional
    public void checkLikesAchievements(Long userId, int totalLikes) {
        if (totalLikes == 10) {
            unlockAchievement(userId, AchievementType.LIKES_10);
        } else if (totalLikes == 50) {
            unlockAchievement(userId, AchievementType.LIKES_50);
        } else if (totalLikes == 100) {
            unlockAchievement(userId, AchievementType.LIKES_100);
        } else if (totalLikes == 500) {
            unlockAchievement(userId, AchievementType.LIKES_500);
        } else if (totalLikes == 1000) {
            unlockAchievement(userId, AchievementType.LIKES_1000);
        }
    }
    
    @Transactional
    public void checkShareAchievements(Long userId, int totalShares) {
        if (totalShares == 1) {
            unlockAchievement(userId, AchievementType.FIRST_SHARE);
        } else if (totalShares == 10) {
            unlockAchievement(userId, AchievementType.SHARE_10);
        } else if (totalShares == 50) {
            unlockAchievement(userId, AchievementType.SHARE_50);
        } else if (totalShares == 100) {
            unlockAchievement(userId, AchievementType.SHARE_100);
        }
    }
    
    @Transactional
    public void checkFavoriteAchievements(Long userId, int totalFavorites) {
        if (totalFavorites == 1) {
            unlockAchievement(userId, AchievementType.FIRST_FAVORITE);
        } else if (totalFavorites == 50) {
            unlockAchievement(userId, AchievementType.FAVORITE_50);
        } else if (totalFavorites == 100) {
            unlockAchievement(userId, AchievementType.FAVORITE_100);
        }
    }
    
    @Transactional
    public void checkTrendAchievements(Long userId, int uniqueTrends) {
        if (uniqueTrends == 5) {
            unlockAchievement(userId, AchievementType.TREND_EXPLORER);
        } else if (uniqueTrends == 20) {
            unlockAchievement(userId, AchievementType.TREND_MASTER);
        } else if (uniqueTrends == 50) {
            unlockAchievement(userId, AchievementType.TREND_COLLECTOR);
        }
    }
    
    @Transactional
    public void checkStreakAchievements(Long userId, int currentStreak) {
        if (currentStreak == 3) {
            unlockAchievement(userId, AchievementType.STREAK_3);
        } else if (currentStreak == 7) {
            unlockAchievement(userId, AchievementType.STREAK_7);
        } else if (currentStreak == 30) {
            unlockAchievement(userId, AchievementType.STREAK_30);
        } else if (currentStreak == 100) {
            unlockAchievement(userId, AchievementType.STREAK_100);
        }
    }
    
    @Transactional
    public void unlockAchievement(Long userId, AchievementType achievementType) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return;
        }
        
        UserAchievement achievement = achievementRepository
            .findByUserIdAndAchievementType(userId, achievementType)
            .orElseGet(() -> {
                UserAchievement newAchievement = new UserAchievement();
                newAchievement.setUser(user);
                newAchievement.setAchievementType(achievementType);
                return newAchievement;
            });
        
        if (!achievement.getCompleted()) {
            achievement.setCompleted(true);
            achievement.setCompletedAt(LocalDateTime.now());
            achievement.setProgress(100);
            achievementRepository.save(achievement);
            
            log.info("User {} unlocked achievement: {}", userId, achievementType.getName());
            
            // Award XP
            // Note: This would need UserProfileService, but to avoid circular dependency,
            // we'll handle XP awarding in the caller
        }
    }
    
    @Transactional
    public void updateProgress(Long userId, AchievementType achievementType, int progress) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return;
        }
        
        UserAchievement achievement = achievementRepository
            .findByUserIdAndAchievementType(userId, achievementType)
            .orElseGet(() -> {
                UserAchievement newAchievement = new UserAchievement();
                newAchievement.setUser(user);
                newAchievement.setAchievementType(achievementType);
                return newAchievement;
            });
        
        achievement.setProgress(progress);
        achievementRepository.save(achievement);
    }
    
    @Transactional(readOnly = true)
    public List<UserAchievement> getUserAchievements(Long userId) {
        return achievementRepository.findByUserId(userId);
    }
    
    @Transactional(readOnly = true)
    public List<UserAchievement> getCompletedAchievements(Long userId) {
        return achievementRepository.findByUserIdAndCompleted(userId, true);
    }
    
    @Transactional(readOnly = true)
    public long getCompletedAchievementCount(Long userId) {
        return achievementRepository.countCompletedAchievementsByUserId(userId);
    }
    
    @Transactional(readOnly = true)
    public List<UserAchievement> getUnnotifiedAchievements() {
        return achievementRepository.findUnnotifiedCompletedAchievements();
    }
    
    @Transactional
    public void markAsNotified(Long achievementId) {
        achievementRepository.findById(achievementId).ifPresent(achievement -> {
            achievement.setNotified(true);
            achievementRepository.save(achievement);
        });
    }
}
