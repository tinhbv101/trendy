package net.devlord.trendy.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GenerateImageRequest {
    
    private Long trendId;
    private String[] inputImagePaths;
    private String customPrompt;
}

