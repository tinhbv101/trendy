package net.devlord.trendy.model.entity;

import net.devlord.trendy.model.enums.AdPosition;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "ad_configs")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class AdConfig {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "ad_name", nullable = false, length = 100)
    private String adName;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "ad_position", nullable = false, length = 50, unique = true)
    private AdPosition adPosition;
    
    @Column(name = "ad_client", nullable = false, length = 100)
    private String adClient;
    
    @Column(name = "ad_slot", nullable = false, length = 50)
    private String adSlot;
    
    @Column(name = "ad_format", nullable = false, length = 50)
    private String adFormat = "auto";
    
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
    
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
