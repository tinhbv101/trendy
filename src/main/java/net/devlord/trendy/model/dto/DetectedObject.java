package net.devlord.trendy.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO for objects detected in an image
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class DetectedObject {
    
    /**
     * Name/label of the detected object (e.g., "person", "tree", "building")
     */
    private String name;
    
    /**
     * Confidence score of detection (0.0 to 1.0)
     */
    private Float confidence;
    
    /**
     * Position description (e.g., "Center", "Center, upper body")
     */
    private String position;
    
    /**
     * Size relative to image (e.g., "large", "medium", "small")
     */
    private String relativeSize;
    
    /**
     * Additional attributes (e.g., "Female", "young adult", "blonde hair")
     */
    private List<String> attributes;
}
