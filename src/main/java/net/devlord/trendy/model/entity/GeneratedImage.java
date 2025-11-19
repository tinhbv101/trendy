package net.devlord.trendy.model.entity;

import net.devlord.trendy.model.enums.GenerationStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "generated_images")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class GeneratedImage {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trend_id", nullable = false)
    private Trend trend;
    
    @Column(name = "input_images", columnDefinition = "JSON")
    private String inputImages;
    
    @Column(name = "output_image_path")
    private String outputImagePath;
    
    @Column(name = "prompt_used", columnDefinition = "TEXT")
    private String promptUsed;
    
    @Column(name = "ai_parameters", columnDefinition = "JSON")
    private String aiParameters;
    
    @Column(name = "generation_time_seconds", precision = 5, scale = 2)
    private BigDecimal generationTimeSeconds;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20, nullable = false)
    private GenerationStatus status = GenerationStatus.PENDING;
    
    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;
    
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}

