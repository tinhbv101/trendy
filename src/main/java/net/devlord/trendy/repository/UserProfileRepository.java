package net.devlord.trendy.repository;

import net.devlord.trendy.model.entity.User;
import net.devlord.trendy.model.entity.UserProfile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {
    
    Optional<UserProfile> findByUser(User user);
    
    Optional<UserProfile> findByUserId(Long userId);
    
    @Query("SELECT up FROM UserProfile up ORDER BY up.totalXp DESC")
    Page<UserProfile> findTopByTotalXp(Pageable pageable);
    
    @Query("SELECT up FROM UserProfile up ORDER BY up.level DESC, up.currentXp DESC")
    Page<UserProfile> findTopByLevel(Pageable pageable);
    
    @Query("SELECT up FROM UserProfile up WHERE up.level >= :minLevel ORDER BY up.totalXp DESC")
    List<UserProfile> findByLevelGreaterThanEqual(@Param("minLevel") int minLevel);
    
    @Query("SELECT COUNT(up) FROM UserProfile up WHERE up.totalXp > :totalXp")
    long countUsersWithHigherXp(@Param("totalXp") int totalXp);
}
