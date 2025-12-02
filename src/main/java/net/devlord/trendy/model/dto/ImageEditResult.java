package net.devlord.trendy.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.devlord.trendy.model.enums.ImageEditType;

import java.time.LocalDateTime;

/**
 * DTO for image editing results
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImageEditResult {
    
    /**
     * ID of the edited image
     */
    private Long imageId;
    
    /**
     * URL of the edited image
     */
    private String imageUrl;
    
    /**
     * Type of edit performed
     */
    private ImageEditType editType;
    
    /**
     * Original image URL
     */
    private String originalImageUrl;
    
    /**
     * Processing time in milliseconds
     */
    private Long processingTimeMs;
    
    /**
     * Success status
     */
    private Boolean success;
    
    /**
     * Error message if failed
     */
    private String errorMessage;
    
    /**
     * Metadata about the edit
     */
    private String metadata;
    
    /**
     * Timestamp when edit was completed
     */
    private LocalDateTime completedAt;
    
    /**
     * Cost in credits (if applicable)
     */
    private Integer creditCost;
}
