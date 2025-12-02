package net.devlord.trendy.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO for image analysis results from Gemini AI
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImageAnalysisResult {
    
    /**
     * Detailed description of the image content
     */
    private String description;
    
    /**
     * AI-generated tags for the image
     */
    private List<String> tags;
    
    /**
     * Style analysis (art style, mood, technique)
     */
    private StyleAnalysis style;
    
    /**
     * Extracted color palette
     */
    private ColorPalette colors;
    
    /**
     * Detected objects in the image
     */
    private List<DetectedObject> objects;
    
    /**
     * AI-powered edit suggestions
     */
    private List<EditSuggestion> suggestions;
    
    /**
     * Confidence score of the analysis (0.0 to 1.0)
     */
    private Double confidence;
}
