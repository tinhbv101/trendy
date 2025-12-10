package net.devlord.trendy.service;

import net.devlord.trendy.exception.TrendNotFoundException;
import net.devlord.trendy.model.dto.TrendDTO;
import net.devlord.trendy.model.entity.Category;
import net.devlord.trendy.model.entity.Trend;
import net.devlord.trendy.model.entity.TrendExample;
import net.devlord.trendy.model.enums.TrendStatus;
import net.devlord.trendy.repository.CategoryRepository;
import net.devlord.trendy.repository.TrendExampleRepository;
import net.devlord.trendy.repository.TrendRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TrendService {
    
    private final TrendRepository trendRepository;
    private final TrendExampleRepository trendExampleRepository;
    private final CategoryRepository categoryRepository;
    
    @Transactional(readOnly = true)
    public Page<Trend> getAllActiveTrends(Pageable pageable) {
        return trendRepository.findByStatusAndDeletedAtIsNull(TrendStatus.ACTIVE, pageable);
    }
    
    @Transactional(readOnly = true)
    public Page<Trend> getAllTrends(Pageable pageable) {
        return trendRepository.findByDeletedAtIsNull(pageable);
    }
    
    @Transactional(readOnly = true)
    public Trend getTrendById(Long id) {
        return trendRepository.findByIdAndDeletedAtIsNull(id)
            .orElseThrow(() -> new TrendNotFoundException("Trend not found with id: " + id));
    }
    
    @Transactional(readOnly = true)
    public Trend getTrendByIdWithCategories(Long id) {
        return trendRepository.findByIdWithCategories(id)
            .orElseThrow(() -> new TrendNotFoundException("Trend not found with id: " + id));
    }
    
    @Transactional(readOnly = true)
    public List<TrendDTO> getAllActiveTrends() {
        List<Trend> trends = trendRepository.findByStatusAndDeletedAtIsNull(TrendStatus.ACTIVE);
        return trends.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public TrendDTO getTrendDTOById(Long id) {
        Trend trend = getTrendById(id);
        return convertToDTO(trend);
    }
    
    @Transactional(readOnly = true)
    public List<TrendDTO> searchTrends(String keyword) {
        List<Trend> trends = trendRepository.searchTrendsByKeyword(TrendStatus.ACTIVE, keyword);
        return trends.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<TrendDTO> getTrendsByCategory(String category) {
        List<Trend> trends = trendRepository.findByStatusAndCategoryAndDeletedAtIsNull(TrendStatus.ACTIVE, category);
        return trends.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<TrendDTO> getTrendingTrends(int limit) {
        List<Trend> trends = trendRepository.findTopTrendsByUsageLimit(TrendStatus.ACTIVE.name(), limit);
        return trends.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    private TrendDTO convertToDTO(Trend trend) {
        TrendDTO dto = new TrendDTO();
        dto.setId(trend.getId());
        dto.setTrendName(trend.getTrendName());
        dto.setDescription(trend.getDescription());
        dto.setPromptTemplate(trend.getPromptTemplate());
        dto.setCategory(trend.getCategory()); // Deprecated, for backward compatibility
        
        // Populate new category fields
        if (trend.getCategories() != null && !trend.getCategories().isEmpty()) {
            dto.setCategoryIds(trend.getCategories().stream()
                .map(Category::getId)
                .collect(Collectors.toList()));
            dto.setCategoryNames(trend.getCategories().stream()
                .map(Category::getName)
                .collect(Collectors.toList()));
        }
        
        dto.setMaxInputImages(trend.getMaxInputImages());
        dto.setAspectRatio(trend.getAspectRatio());
        dto.setAiModel(trend.getAiModel());
        dto.setStatus(trend.getStatus());
        dto.setUsageCount(trend.getUsageCount());
        dto.setCreatedAt(trend.getCreatedAt());
        return dto;
    }
    
    @Transactional(readOnly = true)
    public Page<Trend> getTrendsByCategory(String category, Pageable pageable) {
        return trendRepository.findByStatusAndCategoryAndDeletedAtIsNull(
            TrendStatus.ACTIVE, category, pageable);
    }
    
    @Transactional(readOnly = true)
    public Page<Trend> searchTrends(String keyword, Pageable pageable) {
        return trendRepository.searchTrends(TrendStatus.ACTIVE, keyword, pageable);
    }
    
    @Transactional(readOnly = true)
    public List<String> getAllCategories() {
        return trendRepository.findAllCategories();
    }
    
    @Transactional(readOnly = true)
    public Page<Trend> getTopTrends(Pageable pageable) {
        List<Trend> topTrends = trendRepository.findTopTrendsByUsage(TrendStatus.ACTIVE, pageable);
        return Page.empty(pageable);
    }
    
    @Transactional(readOnly = true)
    public List<Trend> getFeaturedTrends(int limit) {
        return trendRepository.findTopTrendsByUsageLimit(TrendStatus.ACTIVE.name(), limit);
    }
    
    @Transactional(readOnly = true)
    public List<Trend> getAllActivePublicTrends() {
        return trendRepository.findByStatusAndDeletedAtIsNull(TrendStatus.ACTIVE);
    }
    
    @Transactional
    public Trend createTrend(Trend trend) {
        log.info("Creating new trend: {}", trend.getTrendName());
        return trendRepository.save(trend);
    }
    
    @Transactional
    public Trend createTrendFromDTO(TrendDTO dto) {
        Trend trend = new Trend();
        trend.setTrendName(dto.getTrendName());
        trend.setDescription(dto.getDescription());
        trend.setPromptTemplate(dto.getPromptTemplate());
        trend.setCategory(dto.getCategory()); // Deprecated, for backward compatibility
        
        // Handle new categories
        if (dto.getCategoryIds() != null && !dto.getCategoryIds().isEmpty()) {
            List<Category> categories = categoryRepository.findAllById(dto.getCategoryIds());
            trend.setCategories(categories);
        }
        
        trend.setMaxInputImages(dto.getMaxInputImages());
        trend.setAspectRatio(dto.getAspectRatio());
        trend.setAiModel(dto.getAiModel());
        trend.setThumbnailPath(dto.getThumbnailPath());
        trend.setStatus(dto.getStatus());
        trend.setUsageCount(0);
        
        return trendRepository.save(trend);
    }
    
    @Transactional
    public Trend updateTrend(Long id, Trend trendDetails) {
        Trend trend = getTrendById(id);
        
        trend.setTrendName(trendDetails.getTrendName());
        trend.setDescription(trendDetails.getDescription());
        trend.setPromptTemplate(trendDetails.getPromptTemplate());
        trend.setCategory(trendDetails.getCategory());
        trend.setMaxInputImages(trendDetails.getMaxInputImages());
        trend.setAspectRatio(trendDetails.getAspectRatio());
        trend.setAiModel(trendDetails.getAiModel());
        trend.setStatus(trendDetails.getStatus());
        
        log.info("Updated trend: {}", id);
        return trendRepository.save(trend);
    }
    
    @Transactional
    public void updateTrendFromDTO(Long id, TrendDTO dto) {
        Trend trend = getTrendById(id);
        
        trend.setTrendName(dto.getTrendName());
        trend.setDescription(dto.getDescription());
        trend.setPromptTemplate(dto.getPromptTemplate());
        trend.setCategory(dto.getCategory()); // Deprecated, for backward compatibility
        
        // Handle new categories
        if (dto.getCategoryIds() != null) {
            if (dto.getCategoryIds().isEmpty()) {
                trend.getCategories().clear();
            } else {
                List<Category> categories = categoryRepository.findAllById(dto.getCategoryIds());
                trend.getCategories().clear();
                trend.getCategories().addAll(categories);
            }
        }
        
        trend.setMaxInputImages(dto.getMaxInputImages());
        trend.setAspectRatio(dto.getAspectRatio());
        trend.setAiModel(dto.getAiModel());
        trend.setStatus(dto.getStatus());
        
        // Only update thumbnail if a new one was provided
        if (dto.getThumbnailPath() != null) {
            trend.setThumbnailPath(dto.getThumbnailPath());
        }
        
        trendRepository.save(trend);
        log.info("Updated trend from DTO: {}", id);
    }
    
    @Transactional
    public void deleteTrend(Long id) {
        Trend trend = getTrendById(id);
        trend.setDeletedAt(LocalDateTime.now());
        trendRepository.save(trend);
        log.info("Soft deleted trend: {}", id);
    }
    
    @Transactional
    public void incrementUsageCount(Long id) {
        Trend trend = getTrendById(id);
        trend.setUsageCount(trend.getUsageCount() + 1);
        trendRepository.save(trend);
    }
    
    @Transactional(readOnly = true)
    public List<TrendExample> getTrendExamples(Long trendId) {
        return trendExampleRepository.findByTrendIdOrderByDisplayOrderAsc(trendId);
    }
}

