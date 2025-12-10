package net.devlord.trendy.model.dto;

import net.devlord.trendy.model.enums.AchievementType;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AchievementDTO {
    private Long id;
    
    @JsonProperty("type")
    private AchievementType type;
    
    // Add a getter that returns the enum name as string for JSON serialization
    public String getTypeName() {
        return type != null ? type.name() : null;
    }
    
    private String name;
    private String description;
    private String icon;
    private Integer xpReward;
    private Integer progress;
    private Boolean completed;
    private LocalDateTime completedAt;
    private Boolean notified;
}
