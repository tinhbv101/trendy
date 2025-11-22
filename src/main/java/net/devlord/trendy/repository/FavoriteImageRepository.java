package net.devlord.trendy.repository;

import net.devlord.trendy.model.entity.FavoriteImage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FavoriteImageRepository extends JpaRepository<FavoriteImage, Long> {
    
    Optional<FavoriteImage> findByUserIdAndGeneratedImageId(Long userId, Long imageId);
    
    boolean existsByUserIdAndGeneratedImageId(Long userId, Long imageId);
    
    void deleteByUserIdAndGeneratedImageId(Long userId, Long imageId);
    
    @Query("SELECT f FROM FavoriteImage f JOIN FETCH f.generatedImage gi JOIN FETCH gi.trend WHERE f.user.id = :userId ORDER BY f.createdAt DESC")
    Page<FavoriteImage> findByUserIdOrderByCreatedAtDesc(@Param("userId") Long userId, Pageable pageable);
    
    @Query("SELECT f.generatedImage.id FROM FavoriteImage f WHERE f.user.id = :userId")
    List<Long> findImageIdsByUserId(@Param("userId") Long userId);
    
    long countByUserId(Long userId);
}

