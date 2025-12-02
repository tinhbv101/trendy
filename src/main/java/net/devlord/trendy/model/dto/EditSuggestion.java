package net.devlord.trendy.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for AI-powered image editing suggestions
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EditSuggestion {
    
    /**
     * Type of suggestion (e.g., "composition", "color", "style", "technical")
     */
    private String type;
    
    /**
     * The actual suggestion text
     */
    private String suggestion;
    
    /**
     * Reason/explanation for this suggestion
     */
    private String reason;
    
    /**
     * Priority level (e.g., "High", "Medium", "Low")
     */
    private String priority;
    
    /**
     * Expected impact (e.g., "high", "medium", "low")
     */
    private String impact;
    
    /**
     * Difficulty to implement (e.g., "easy", "moderate", "difficult")
     */
    private String difficulty;
    
    /**
     * Specific action to take (e.g., "Crop to 16:9 ratio", "Increase saturation by 20%")
     */
    private String action;
}
