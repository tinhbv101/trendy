package net.devlord.trendy.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "shared_images", indexes = {
    @Index(name = "idx_share_token", columnList = "share_token"),
    @Index(name = "idx_expires_at", columnList = "expires_at")
})
@NamedEntityGraph(
    name = "SharedImage.withDetails",
    attributeNodes = {
        @NamedAttributeNode(value = "generatedImage", subgraph = "generatedImageSubgraph"),
        @NamedAttributeNode("user")
    },
    subgraphs = {
        @NamedSubgraph(
            name = "generatedImageSubgraph",
            attributeNodes = @NamedAttributeNode("trend")
        )
    }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class SharedImage {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "generated_image_id", nullable = false)
    private GeneratedImage generatedImage;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Column(name = "share_token", nullable = false, unique = true, length = 64)
    private String shareToken;
    
    @Column(name = "expires_at")
    private LocalDateTime expiresAt;
    
    @Column(name = "view_count", nullable = false, columnDefinition = "INT DEFAULT 0")
    private Integer viewCount = 0;
    
    @Column(name = "is_active", nullable = false, columnDefinition = "BOOLEAN DEFAULT true")
    private Boolean isActive = true;
    
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    /**
     * Check if this shared link is still valid
     */
    public boolean isExpired() {
        return expiresAt != null && LocalDateTime.now().isAfter(expiresAt);
    }
    
    /**
     * Check if this shared link is accessible
     */
    public boolean isAccessible() {
        return isActive && !isExpired();
    }
    
    /**
     * Increment view count
     */
    public void incrementViewCount() {
        this.viewCount = (this.viewCount == null ? 0 : this.viewCount) + 1;
    }
}

