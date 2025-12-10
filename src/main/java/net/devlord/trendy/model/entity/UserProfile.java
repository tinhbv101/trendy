package net.devlord.trendy.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_profiles")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class UserProfile {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;
    
    @Column(name = "level", nullable = false)
    private Integer level = 1;
    
    @Column(name = "current_xp", nullable = false)
    private Integer currentXp = 0;
    
    @Column(name = "total_xp", nullable = false)
    private Integer totalXp = 0;
    
    @Column(name = "total_generations", nullable = false)
    private Integer totalGenerations = 0;
    
    @Column(name = "total_likes_received", nullable = false)
    private Integer totalLikesReceived = 0;
    
    @Column(name = "total_shares", nullable = false)
    private Integer totalShares = 0;
    
    @Column(name = "total_favorites", nullable = false)
    private Integer totalFavorites = 0;
    
    @Column(name = "current_streak", nullable = false)
    private Integer currentStreak = 0;
    
    @Column(name = "longest_streak", nullable = false)
    private Integer longestStreak = 0;
    
    @Column(name = "last_activity_date")
    private LocalDateTime lastActivityDate;
    
    @Column(name = "unique_trends_used", nullable = false)
    private Integer uniqueTrendsUsed = 0;
    
    @Column(name = "unique_models_used", nullable = false)
    private Integer uniqueModelsUsed = 0;
    
    @Column(name = "profile_picture")
    private String profilePicture;
    
    @Column(name = "bio", length = 500)
    private String bio;
    
    @Column(name = "title", length = 100)
    private String title;
    
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // Helper method to calculate XP needed for next level
    public int getXpForNextLevel() {
        return calculateXpForLevel(level + 1);
    }
    
    // Helper method to calculate XP needed for current level
    public int getXpForCurrentLevel() {
        return calculateXpForLevel(level);
    }
    
    // XP formula: level^2 * 100
    private int calculateXpForLevel(int targetLevel) {
        return targetLevel * targetLevel * 100;
    }
    
    // Get progress percentage to next level
    public double getLevelProgress() {
        int xpForCurrent = getXpForCurrentLevel();
        int xpForNext = getXpForNextLevel();
        int xpNeeded = xpForNext - xpForCurrent;
        return (double) currentXp / xpNeeded * 100;
    }
    
    // Add XP and check for level up
    public boolean addXp(int xp) {
        this.currentXp += xp;
        this.totalXp += xp;
        
        boolean leveledUp = false;
        while (currentXp >= getXpForNextLevel() && level < 100) {
            currentXp -= getXpForNextLevel();
            level++;
            leveledUp = true;
        }
        
        return leveledUp;
    }
}
