package net.devlord.trendy.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.devlord.trendy.model.enums.ImageEditType;

/**
 * DTO for image editing requests
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImageEditRequest {
    
    /**
     * Type of edit operation
     */
    private ImageEditType editType;
    
    /**
     * Source image ID (from database)
     */
    private Long sourceImageId;
    
    /**
     * Source image URL (alternative to sourceImageId)
     */
    private String sourceImageUrl;
    
    /**
     * Additional prompt for AI-guided editing
     */
    private String prompt;
    
    /**
     * Style name for style transfer (e.g., "Van Gogh", "Picasso", "Anime")
     */
    private String styleName;
    
    /**
     * Upscale factor (2x, 4x, 8x)
     */
    private Integer upscaleFactor;
    
    /**
     * Mask image for inpainting (base64 or URL)
     */
    private String maskImage;
    
    /**
     * Color grading preset (e.g., "warm", "cool", "vibrant", "vintage")
     */
    private String colorGradingPreset;
    
    /**
     * Face enhancement strength (0.0 to 1.0)
     */
    private Double enhancementStrength;
    
    /**
     * Custom parameters as JSON
     */
    private String customParams;
}
