package net.devlord.trendy.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileDTO {
    private Long id;
    private Long userId;
    private String username;
    private Integer level;
    private Integer currentXp;
    private Integer totalXp;
    private Integer xpForNextLevel;
    private Double levelProgress;
    private Integer totalGenerations;
    private Integer totalLikesReceived;
    private Integer totalShares;
    private Integer totalFavorites;
    private Integer currentStreak;
    private Integer longestStreak;
    private Integer uniqueTrendsUsed;
    private Integer uniqueModelsUsed;
    private String profilePicture;
    private String bio;
    private String title;
    private Long rank;
    private Long totalAchievements;
    private Long completedAchievements;
}
