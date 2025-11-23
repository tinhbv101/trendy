package net.devlord.trendy.model.dto;

import net.devlord.trendy.model.enums.TrendStatus;
import net.devlord.trendy.model.enums.AspectRatio;
import net.devlord.trendy.model.enums.AIModel;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TrendDTO {
    
    private Long id;
    
    @NotBlank(message = "Trend name is required")
    @Size(max = 100, message = "Trend name must not exceed 100 characters")
    private String trendName;
    
    @Size(max = 1000, message = "Description must not exceed 1000 characters")
    private String description;
    
    @NotBlank(message = "Prompt template is required")
    private String promptTemplate;
    
    @Size(max = 50, message = "Category must not exceed 50 characters")
    private String category;
    
    @NotNull(message = "Max input images is required")
    private Integer maxInputImages = 1;

    @NotNull(message = "Aspect ratio is required")
    private AspectRatio aspectRatio = AspectRatio.SQUARE;

    @NotNull(message = "AI Model is required")
    private AIModel aiModel = AIModel.GEMINI_2_5_FLASH;
    
    private String thumbnailPath;
    
    @NotNull(message = "Status is required")
    private TrendStatus status = TrendStatus.TESTING;
    
    private Integer usageCount = 0;
    
    private LocalDateTime createdAt;
}

