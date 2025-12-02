package net.devlord.trendy.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO for color palette extracted from an image
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ColorPalette {
    
    /**
     * List of dominant colors in hex format (e.g., "#FF5733", "#3498DB")
     */
    private List<String> dominantColors;
    
    /**
     * Color scheme type (e.g., "monochromatic", "complementary", "analogous", "triadic")
     */
    private String colorScheme;
    
    /**
     * Color temperature (e.g., "warm", "cool", "neutral")
     */
    private String temperature;
    
    /**
     * Overall saturation level (e.g., "vibrant", "muted", "pastel")
     */
    private String saturation;
    
    /**
     * Brightness/value level (e.g., "bright", "dark", "balanced")
     */
    private String brightness;
    
    /**
     * Accent colors (secondary colors that stand out)
     */
    private List<String> accentColors;
}
