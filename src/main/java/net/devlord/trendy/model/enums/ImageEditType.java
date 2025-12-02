package net.devlord.trendy.model.enums;

import lombok.Getter;

/**
 * Enum for different types of image editing operations
 * All operations are FREE (0 credits)
 */
@Getter
public enum ImageEditType {
    
    /**
     * Remove background automatically using AI
     */
    BACKGROUND_REMOVAL("Background Removal", "Remove background from image automatically", 0),
    
    /**
     * Transfer artistic style (Van Gogh, Picasso, etc.)
     */
    STYLE_TRANSFER("Style Transfer", "Transform image into different artistic styles", 0),
    
    /**
     * Upscale image resolution (2x, 4x, 8x)
     */
    IMAGE_UPSCALING("Image Upscaling", "Increase image resolution with AI enhancement", 0),
    
    /**
     * Remove or replace objects in image
     */
    INPAINTING("Inpainting", "Remove or replace objects in the image", 0),
    
    /**
     * Adjust colors automatically
     */
    COLOR_GRADING("Color Grading", "Adjust colors and apply cinematic looks", 0),
    
    /**
     * Enhance facial features
     */
    FACE_ENHANCEMENT("Face Enhancement", "Enhance facial features naturally", 0),
    
    /**
     * General AI-guided editing with prompt
     */
    AI_GUIDED_EDIT("AI Guided Edit", "Edit image with custom AI instructions", 0),
    
    /**
     * Restore old or damaged photos
     */
    PHOTO_RESTORATION("Photo Restoration", "Restore old or damaged photos", 0),
    
    /**
     * Convert to different art styles
     */
    ARTISTIC_FILTER("Artistic Filter", "Apply artistic filters and effects", 0),
    
    /**
     * Smart crop and composition
     */
    SMART_CROP("Smart Crop", "Intelligently crop and reframe images", 0);
    
    private final String displayName;
    private final String description;
    private final int creditCost;
    
    ImageEditType(String displayName, String description, int creditCost) {
        this.displayName = displayName;
        this.description = description;
        this.creditCost = creditCost;
    }
}
