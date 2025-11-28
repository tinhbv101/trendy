package net.devlord.trendy.service;

import net.devlord.trendy.model.entity.AdConfig;
import net.devlord.trendy.model.enums.AdPosition;
import net.devlord.trendy.repository.AdConfigRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdConfigService {
    
    private final AdConfigRepository adConfigRepository;
    
    /**
     * Get all ad configurations
     */
    public List<AdConfig> getAllAds() {
        return adConfigRepository.findAll();
    }
    
    /**
     * Get ad configuration by ID
     */
    public Optional<AdConfig> getAdById(Long id) {
        return adConfigRepository.findById(id);
    }
    
    /**
     * Get active ad configuration for a specific position
     */
    public Optional<AdConfig> getActiveAdByPosition(AdPosition position) {
        return adConfigRepository.findByAdPositionAndIsActiveTrue(position);
    }
    
    /**
     * Create new ad configuration
     */
    @Transactional
    public AdConfig createAd(AdConfig adConfig) {
        log.info("Creating new ad configuration: {}", adConfig.getAdName());
        
        // Check if position already exists
        if (adConfigRepository.existsByAdPosition(adConfig.getAdPosition())) {
            throw new IllegalArgumentException("Ad position already exists: " + adConfig.getAdPosition());
        }
        
        return adConfigRepository.save(adConfig);
    }
    
    /**
     * Update existing ad configuration
     */
    @Transactional
    public AdConfig updateAd(Long id, AdConfig updatedAd) {
        log.info("Updating ad configuration with ID: {}", id);
        
        AdConfig existingAd = adConfigRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Ad not found with ID: " + id));
        
        // Check if position is being changed and if new position already exists
        if (!existingAd.getAdPosition().equals(updatedAd.getAdPosition())) {
            if (adConfigRepository.existsByAdPosition(updatedAd.getAdPosition())) {
                throw new IllegalArgumentException("Ad position already exists: " + updatedAd.getAdPosition());
            }
        }
        
        existingAd.setAdName(updatedAd.getAdName());
        existingAd.setAdPosition(updatedAd.getAdPosition());
        existingAd.setAdClient(updatedAd.getAdClient());
        existingAd.setAdSlot(updatedAd.getAdSlot());
        existingAd.setAdFormat(updatedAd.getAdFormat());
        existingAd.setIsActive(updatedAd.getIsActive());
        
        return adConfigRepository.save(existingAd);
    }
    
    /**
     * Delete ad configuration
     */
    @Transactional
    public void deleteAd(Long id) {
        log.info("Deleting ad configuration with ID: {}", id);
        
        if (!adConfigRepository.existsById(id)) {
            throw new IllegalArgumentException("Ad not found with ID: " + id);
        }
        
        adConfigRepository.deleteById(id);
    }
    
    /**
     * Toggle ad active status
     */
    @Transactional
    public AdConfig toggleAdStatus(Long id) {
        log.info("Toggling status for ad configuration with ID: {}", id);
        
        AdConfig ad = adConfigRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Ad not found with ID: " + id));
        
        ad.setIsActive(!ad.getIsActive());
        
        return adConfigRepository.save(ad);
    }
}
