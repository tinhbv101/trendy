package net.devlord.trendy.repository;

import net.devlord.trendy.model.entity.GeneratedImage;
import net.devlord.trendy.model.enums.GenerationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GeneratedImageRepository extends JpaRepository<GeneratedImage, Long> {
    
    @Query("SELECT g FROM GeneratedImage g JOIN FETCH g.trend WHERE g.user.id = :userId ORDER BY g.createdAt DESC")
    Page<GeneratedImage> findByUserIdOrderByCreatedAtDesc(@Param("userId") Long userId, Pageable pageable);
    
    Page<GeneratedImage> findByTrendIdOrderByCreatedAtDesc(Long trendId, Pageable pageable);
    
    List<GeneratedImage> findByStatus(GenerationStatus status);
    
    Long countByUserId(Long userId);
    
    Long countByTrendId(Long trendId);
}

