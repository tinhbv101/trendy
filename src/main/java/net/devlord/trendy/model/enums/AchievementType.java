package net.devlord.trendy.model.enums;

import lombok.Getter;

@Getter
public enum AchievementType {
    // Generation Achievements
    FIRST_GENERATION("First Steps", "Generate your first image", 10, "🎨"),
    GENERATION_10("Getting Started", "Generate 10 images", 25, "🖼️"),
    GENERATION_50("Image Enthusiast", "Generate 50 images", 50, "🎭"),
    GENERATION_100("Century Club", "Generate 100 images", 100, "💯"),
    GENERATION_500("Image Master", "Generate 500 images", 250, "🏆"),
    GENERATION_1000("Legendary Creator", "Generate 1000 images", 500, "👑"),
    
    // Likes Received Achievements
    LIKES_10("Popular", "Receive 10 likes", 25, "👍"),
    LIKES_50("Well Liked", "Receive 50 likes", 50, "❤️"),
    LIKES_100("Community Favorite", "Receive 100 likes", 100, "💖"),
    LIKES_500("Influencer", "Receive 500 likes", 250, "⭐"),
    LIKES_1000("Superstar", "Receive 1000 likes", 500, "🌟"),
    
    // Sharing Achievements
    FIRST_SHARE("Sharing is Caring", "Share your first image", 10, "📤"),
    SHARE_10("Social Butterfly", "Share 10 images", 25, "🦋"),
    SHARE_50("Content Creator", "Share 50 images", 50, "📱"),
    SHARE_100("Viral Creator", "Share 100 images", 100, "🚀"),
    
    // Trend Usage Achievements
    TREND_EXPLORER("Trend Explorer", "Try 5 different trends", 25, "🔍"),
    TREND_MASTER("Trend Master", "Try 20 different trends", 75, "🎯"),
    TREND_COLLECTOR("Trend Collector", "Try 50 different trends", 150, "📚"),
    
    // Favorite Achievements
    FIRST_FAVORITE("Collector", "Add first favorite", 10, "⭐"),
    FAVORITE_50("Curator", "Add 50 favorites", 50, "🎨"),
    FAVORITE_100("Gallery Owner", "Add 100 favorites", 100, "🖼️"),
    
    // Streak Achievements
    STREAK_3("Dedicated", "3 day streak", 25, "🔥"),
    STREAK_7("Committed", "7 day streak", 50, "🔥🔥"),
    STREAK_30("Unstoppable", "30 day streak", 150, "🔥🔥🔥"),
    STREAK_100("Legendary Streak", "100 day streak", 500, "👑🔥"),
    
    // Time-based Achievements
    EARLY_BIRD("Early Bird", "Generate before 6 AM", 25, "🌅"),
    NIGHT_OWL("Night Owl", "Generate after midnight", 25, "🦉"),
    WEEKEND_WARRIOR("Weekend Warrior", "Generate 50 images on weekends", 50, "🎮"),
    
    // Quality Achievements
    HIGH_QUALITY("Quality First", "Generate 10 high-quality images", 50, "💎"),
    PERFECTIONIST("Perfectionist", "Generate 50 high-quality images", 150, "✨"),
    
    // Special Achievements
    TOP_CREATOR_MONTH("Top Creator", "Be top creator of the month", 200, "🏆"),
    TOP_CREATOR_YEAR("Creator of the Year", "Be top creator of the year", 1000, "👑"),
    BETA_TESTER("Beta Tester", "Join during beta period", 100, "🧪"),
    EARLY_ADOPTER("Early Adopter", "Join in first month", 50, "🚀"),
    
    // Community Achievements
    HELPFUL("Helpful", "Receive 10 thanks", 50, "🤝"),
    MENTOR("Mentor", "Help 50 users", 150, "👨‍🏫"),
    COMMUNITY_LEADER("Community Leader", "Receive 100 thanks", 300, "🌟"),
    
    // Creative Achievements
    STYLE_EXPLORER("Style Explorer", "Use 10 different AI models", 50, "🎨"),
    ASPECT_MASTER("Aspect Master", "Use all aspect ratios", 50, "📐"),
    MULTI_TALENTED("Multi-Talented", "Use all image edit tools", 75, "🛠️");
    
    private final String name;
    private final String description;
    private final int xpReward;
    private final String icon;
    
    AchievementType(String name, String description, int xpReward, String icon) {
        this.name = name;
        this.description = description;
        this.xpReward = xpReward;
        this.icon = icon;
    }
}
