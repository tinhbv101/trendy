package net.devlord.trendy.model.dto;

import net.devlord.trendy.model.enums.GenerationStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GenerateImageResponse {
    
    private Long imageId;
    private GenerationStatus status;
    private String outputImagePath;
    private String message;
    private String errorMessage;
}

