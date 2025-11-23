package net.devlord.trendy.model.entity;

import net.devlord.trendy.model.enums.TrendStatus;
import net.devlord.trendy.model.enums.AspectRatio;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "trends")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Trend {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "trend_name", nullable = false, length = 100)
    private String trendName;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "prompt_template", nullable = false, columnDefinition = "TEXT")
    private String promptTemplate;
    
    @Column(length = 50)
    private String category;
    
    @Column(name = "max_input_images")
    private Integer maxInputImages = 1;

    @Enumerated(EnumType.STRING)
    @Column(name = "aspect_ratio", length = 20)
    private AspectRatio aspectRatio = AspectRatio.SQUARE;
    
    @Column(name = "thumbnail_path")
    private String thumbnailPath;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20, nullable = false)
    private TrendStatus status = TrendStatus.TESTING;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private User createdBy;
    
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;
    
    @Column(name = "usage_count")
    private Integer usageCount = 0;
    
    @OneToMany(mappedBy = "trend", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<TrendExample> examples = new ArrayList<>();

    @OneToMany(mappedBy = "trend", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<GeneratedImage> generatedImages = new ArrayList<>();

    // Helper methods for backward compatibility and convenience
    public int getOutputWidth() {
        return aspectRatio != null ? aspectRatio.getWidth() : AspectRatio.SQUARE.getWidth();
    }

    public int getOutputHeight() {
        return aspectRatio != null ? aspectRatio.getHeight() : AspectRatio.SQUARE.getHeight();
    }

    public String getAspectRatioDisplay() {
        return aspectRatio != null ? aspectRatio.getDisplayName() : AspectRatio.SQUARE.getDisplayName();
    }
}

