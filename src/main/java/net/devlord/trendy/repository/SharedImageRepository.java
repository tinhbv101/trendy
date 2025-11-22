package net.devlord.trendy.repository;

import net.devlord.trendy.model.entity.SharedImage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface SharedImageRepository extends JpaRepository<SharedImage, Long> {
    
    @Query("SELECT s FROM SharedImage s " +
           "JOIN FETCH s.generatedImage gi " +
           "JOIN FETCH gi.trend " +
           "JOIN FETCH s.user " +
           "WHERE s.shareToken = :token")
    Optional<SharedImage> findByShareTokenWithDetails(@Param("token") String token);
    
    Optional<SharedImage> findByShareToken(String shareToken);
    
    @Query("SELECT s FROM SharedImage s WHERE s.generatedImage.id = :imageId AND s.user.id = :userId AND s.isActive = true")
    Optional<SharedImage> findActiveByGeneratedImageIdAndUserId(
        @Param("imageId") Long imageId, 
        @Param("userId") Long userId
    );
    
    @EntityGraph(value = "SharedImage.withDetails", type = EntityGraph.EntityGraphType.FETCH)
    @Query("SELECT s FROM SharedImage s WHERE s.user.id = :userId ORDER BY s.createdAt DESC")
    Page<SharedImage> findByUserIdOrderByCreatedAtDesc(@Param("userId") Long userId, Pageable pageable);
    
    long countByUserId(Long userId);
    
    @Modifying
    @Query("DELETE FROM SharedImage s WHERE s.expiresAt IS NOT NULL AND s.expiresAt < :now")
    int deleteExpiredShares(@Param("now") LocalDateTime now);
    
    @Query("SELECT COUNT(s) FROM SharedImage s WHERE s.user.id = :userId AND s.isActive = true")
    long countActiveByUserId(@Param("userId") Long userId);
}

