package net.devlord.trendy.repository;

import net.devlord.trendy.model.entity.GeneratedImage;
import net.devlord.trendy.model.enums.GenerationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface GeneratedImageRepository extends JpaRepository<GeneratedImage, Long> {
    
    @Query("SELECT g FROM GeneratedImage g JOIN FETCH g.trend WHERE g.user.id = :userId ORDER BY g.createdAt DESC")
    Page<GeneratedImage> findByUserIdOrderByCreatedAtDesc(@Param("userId") Long userId, Pageable pageable);
    
    Page<GeneratedImage> findByTrendIdOrderByCreatedAtDesc(Long trendId, Pageable pageable);
    
    List<GeneratedImage> findByStatus(GenerationStatus status);
    
    Long countByUserId(Long userId);
    
    Long countByTrendId(Long trendId);
    
    @Query("SELECT g FROM GeneratedImage g JOIN FETCH g.user JOIN FETCH g.trend WHERE g.id = :id")
    Optional<GeneratedImage> findByIdWithUserAndTrend(@Param("id") Long id);
    
    // Advanced filtering methods
    @Query("SELECT g FROM GeneratedImage g JOIN FETCH g.trend WHERE g.user.id = :userId " +
           "AND (:trendId IS NULL OR g.trend.id = :trendId) " +
           "AND (:status IS NULL OR g.status = :status) " +
           "AND (:startDate IS NULL OR g.createdAt >= :startDate) " +
           "AND (:endDate IS NULL OR g.createdAt <= :endDate) " +
           "ORDER BY g.createdAt DESC")
    Page<GeneratedImage> findByUserIdWithFilters(
        @Param("userId") Long userId,
        @Param("trendId") Long trendId,
        @Param("status") GenerationStatus status,
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate,
        Pageable pageable
    );
    
    @Query("SELECT g FROM GeneratedImage g JOIN FETCH g.trend WHERE g.user.id = :userId " +
           "AND LOWER(g.trend.trendName) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "ORDER BY g.createdAt DESC")
    Page<GeneratedImage> searchByUserIdAndTrendName(
        @Param("userId") Long userId,
        @Param("search") String search,
        Pageable pageable
    );
}

