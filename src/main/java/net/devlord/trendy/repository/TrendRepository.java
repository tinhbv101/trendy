package net.devlord.trendy.repository;

import net.devlord.trendy.model.entity.Trend;
import net.devlord.trendy.model.enums.TrendStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TrendRepository extends JpaRepository<Trend, Long> {
    
    // Tìm trends theo status và chưa bị xóa (loại trừ AI Image Editing)
    @Query("SELECT t FROM Trend t WHERE t.status = :status AND t.deletedAt IS NULL AND t.trendName != 'AI Image Editing'")
    Page<Trend> findByStatusAndDeletedAtIsNull(@Param("status") TrendStatus status, Pageable pageable);
    
    // Tìm tất cả active trends chưa bị xóa (không phân trang, loại trừ AI Image Editing)
    @Query("SELECT t FROM Trend t WHERE t.status = :status AND t.deletedAt IS NULL AND t.trendName != 'AI Image Editing'")
    List<Trend> findByStatusAndDeletedAtIsNull(@Param("status") TrendStatus status);
    
    // Tìm tất cả trends chưa bị xóa
    Page<Trend> findByDeletedAtIsNull(Pageable pageable);
    
    // Tìm trends active theo category (loại trừ AI Image Editing)
    @Query("SELECT t FROM Trend t WHERE t.status = :status AND t.category = :category AND t.deletedAt IS NULL AND t.trendName != 'AI Image Editing'")
    List<Trend> findByStatusAndCategoryAndDeletedAtIsNull(
        @Param("status") TrendStatus status, @Param("category") String category);
    
    // Tìm trend theo id và chưa bị xóa
    Optional<Trend> findByIdAndDeletedAtIsNull(Long id);
    
    // Top trends phổ biến nhất (với Pageable, loại trừ AI Image Editing)
    @Query("SELECT t FROM Trend t WHERE t.status = :status AND t.deletedAt IS NULL AND t.trendName != 'AI Image Editing' ORDER BY t.usageCount DESC")
    List<Trend> findTopTrendsByUsage(@Param("status") TrendStatus status, Pageable pageable);
    
    // Top trends phổ biến nhất (với limit, loại trừ AI Image Editing)
    @Query(value = "SELECT * FROM trends WHERE status = :status AND deleted_at IS NULL AND trend_name != 'AI Image Editing' ORDER BY usage_count DESC LIMIT :limit", 
           nativeQuery = true)
    List<Trend> findTopTrendsByUsageLimit(@Param("status") String status, @Param("limit") int limit);
    
    // Search trends (với phân trang, loại trừ AI Image Editing)
    @Query("SELECT t FROM Trend t WHERE t.status = :status AND t.deletedAt IS NULL " +
           "AND t.trendName != 'AI Image Editing' " +
           "AND (LOWER(t.trendName) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(t.description) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Trend> searchTrends(@Param("status") TrendStatus status, 
                             @Param("keyword") String keyword, 
                             Pageable pageable);
    
    // Search trends (không phân trang, loại trừ AI Image Editing)
    @Query("SELECT t FROM Trend t WHERE t.status = :status AND t.deletedAt IS NULL " +
           "AND t.trendName != 'AI Image Editing' " +
           "AND (LOWER(t.trendName) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(t.description) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    List<Trend> searchTrendsByKeyword(@Param("status") TrendStatus status, 
                                       @Param("keyword") String keyword);
    
    // Get all distinct categories (loại trừ AI Image Editing)
    @Query("SELECT DISTINCT t.category FROM Trend t WHERE t.status = 'ACTIVE' AND t.deletedAt IS NULL AND t.category IS NOT NULL AND t.trendName != 'AI Image Editing'")
    List<String> findAllCategories();
    
    // Find by category (loại trừ AI Image Editing)
    @Query("SELECT t FROM Trend t WHERE t.status = :status AND t.category = :category AND t.deletedAt IS NULL AND t.trendName != 'AI Image Editing'")
    Page<Trend> findByStatusAndCategoryAndDeletedAtIsNull(
        @Param("status") TrendStatus status, @Param("category") String category, Pageable pageable);
}

