package net.devlord.trendy.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for object position in an image
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ObjectPosition {
    
    /**
     * X coordinate (normalized 0.0 to 1.0)
     */
    private Double x;
    
    /**
     * Y coordinate (normalized 0.0 to 1.0)
     */
    private Double y;
}

