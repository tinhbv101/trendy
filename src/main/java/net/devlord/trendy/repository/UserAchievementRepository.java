package net.devlord.trendy.repository;

import net.devlord.trendy.model.entity.User;
import net.devlord.trendy.model.entity.UserAchievement;
import net.devlord.trendy.model.enums.AchievementType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserAchievementRepository extends JpaRepository<UserAchievement, Long> {
    
    List<UserAchievement> findByUser(User user);
    
    List<UserAchievement> findByUserId(Long userId);
    
    List<UserAchievement> findByUserAndCompleted(User user, boolean completed);
    
    List<UserAchievement> findByUserIdAndCompleted(Long userId, boolean completed);
    
    Optional<UserAchievement> findByUserAndAchievementType(User user, AchievementType achievementType);
    
    Optional<UserAchievement> findByUserIdAndAchievementType(Long userId, AchievementType achievementType);
    
    @Query("SELECT ua FROM UserAchievement ua WHERE ua.user.id = :userId AND ua.completed = true ORDER BY ua.completedAt DESC")
    List<UserAchievement> findCompletedAchievementsByUserId(@Param("userId") Long userId);
    
    @Query("SELECT ua FROM UserAchievement ua WHERE ua.completed = true AND ua.notified = false")
    List<UserAchievement> findUnnotifiedCompletedAchievements();
    
    @Query("SELECT COUNT(ua) FROM UserAchievement ua WHERE ua.user.id = :userId AND ua.completed = true")
    long countCompletedAchievementsByUserId(@Param("userId") Long userId);
}
