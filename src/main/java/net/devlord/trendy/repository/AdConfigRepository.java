package net.devlord.trendy.repository;

import net.devlord.trendy.model.entity.AdConfig;
import net.devlord.trendy.model.enums.AdPosition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AdConfigRepository extends JpaRepository<AdConfig, Long> {
    
    /**
     * Find an active ad configuration by position
     */
    Optional<AdConfig> findByAdPositionAndIsActiveTrue(AdPosition position);
    
    /**
     * Find all active ad configurations
     */
    List<AdConfig> findAllByIsActiveTrue();
    
    /**
     * Find ad configuration by position (regardless of active status)
     */
    Optional<AdConfig> findByAdPosition(AdPosition position);
    
    /**
     * Check if an ad position already exists
     */
    boolean existsByAdPosition(AdPosition position);
}
