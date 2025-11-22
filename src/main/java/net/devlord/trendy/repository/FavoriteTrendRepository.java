package net.devlord.trendy.repository;

import net.devlord.trendy.model.entity.FavoriteTrend;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FavoriteTrendRepository extends JpaRepository<FavoriteTrend, Long> {
    
    Optional<FavoriteTrend> findByUserIdAndTrendId(Long userId, Long trendId);
    
    boolean existsByUserIdAndTrendId(Long userId, Long trendId);
    
    void deleteByUserIdAndTrendId(Long userId, Long trendId);
    
    @Query("SELECT f FROM FavoriteTrend f JOIN FETCH f.trend WHERE f.user.id = :userId ORDER BY f.createdAt DESC")
    Page<FavoriteTrend> findByUserIdOrderByCreatedAtDesc(@Param("userId") Long userId, Pageable pageable);
    
    @Query("SELECT f.trend.id FROM FavoriteTrend f WHERE f.user.id = :userId")
    List<Long> findTrendIdsByUserId(@Param("userId") Long userId);
    
    long countByUserId(Long userId);
}

