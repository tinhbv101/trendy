package net.devlord.trendy.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO for artistic style analysis of an image
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StyleAnalysis {
    
    /**
     * Identified art style (e.g., "impressionism", "digital art", "anime", "photorealistic")
     */
    private String artStyle;
    
    /**
     * Overall mood/atmosphere (e.g., "peaceful", "energetic", "melancholic")
     */
    private String mood;
    
    /**
     * Technique used (e.g., "oil painting", "digital illustration", "3D render")
     */
    private String technique;
    
    /**
     * Similar artists or art movements that influenced this style
     */
    private List<String> influences;
    
    /**
     * Genre or category (e.g., "portrait", "landscape", "abstract")
     */
    private String genre;
    
    /**
     * Time period or era (e.g., "modern", "renaissance", "contemporary")
     */
    private String era;
}
