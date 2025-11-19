package net.devlord.trendy.repository;

import net.devlord.trendy.model.entity.Rating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RatingRepository extends JpaRepository<Rating, Long> {
    
    Optional<Rating> findByUserIdAndTrendId(Long userId, Long trendId);
    
    List<Rating> findByTrendIdOrderByCreatedAtDesc(Long trendId);
    
    @Query("SELECT AVG(r.rating) FROM Rating r WHERE r.trend.id = :trendId")
    Double findAverageRatingByTrendId(@Param("trendId") Long trendId);
    
    Long countByTrendId(Long trendId);
}

