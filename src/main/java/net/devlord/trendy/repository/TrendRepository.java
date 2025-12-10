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
    
    // Tìm trend theo id với categories (eager fetch để tránh LazyInitializationException)
    @Query("SELECT t FROM Trend t LEFT JOIN FETCH t.categories WHERE t.id = :id AND t.deletedAt IS NULL")
    Optional<Trend> findByIdWithCategories(@Param("id") Long id);
    
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
    
    // New methods using many-to-many relationship
    
    // Find trends by category name (using new categories table)
    @Query("SELECT DISTINCT t FROM Trend t JOIN t.categories c WHERE t.status = :status AND c.name = :categoryName AND t.deletedAt IS NULL AND t.trendName != 'AI Image Editing'")
    List<Trend> findByStatusAndCategoryNameAndDeletedAtIsNull(
        @Param("status") TrendStatus status, @Param("categoryName") String categoryName);
    
    // Find trends by category name with pagination (using new categories table)
    @Query("SELECT DISTINCT t FROM Trend t JOIN t.categories c WHERE t.status = :status AND c.name = :categoryName AND t.deletedAt IS NULL AND t.trendName != 'AI Image Editing'")
    Page<Trend> findByStatusAndCategoryNameAndDeletedAtIsNull(
        @Param("status") TrendStatus status, @Param("categoryName") String categoryName, Pageable pageable);
    
    // Find trends by category ID
    @Query("SELECT DISTINCT t FROM Trend t JOIN t.categories c WHERE t.status = :status AND c.id = :categoryId AND t.deletedAt IS NULL AND t.trendName != 'AI Image Editing'")
    List<Trend> findByStatusAndCategoryIdAndDeletedAtIsNull(
        @Param("status") TrendStatus status, @Param("categoryId") Long categoryId);
    
    // Find trends by category ID with pagination
    @Query("SELECT DISTINCT t FROM Trend t JOIN t.categories c WHERE t.status = :status AND c.id = :categoryId AND t.deletedAt IS NULL AND t.trendName != 'AI Image Editing'")
    Page<Trend> findByStatusAndCategoryIdAndDeletedAtIsNull(
        @Param("status") TrendStatus status, @Param("categoryId") Long categoryId, Pageable pageable);
}

