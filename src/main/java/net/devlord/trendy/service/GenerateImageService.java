package net.devlord.trendy.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.devlord.trendy.exception.ImageGenerationException;
import net.devlord.trendy.model.entity.GeneratedImage;
import net.devlord.trendy.model.entity.Trend;
import net.devlord.trendy.model.entity.User;
import net.devlord.trendy.model.enums.GenerationStatus;
import net.devlord.trendy.repository.GeneratedImageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class GenerateImageService {
    
    private final GeneratedImageRepository generatedImageRepository;
    private final TrendService trendService;
    private final UserService userService;
    private final FileStorageService fileStorageService;
    private final AIService aiService;
    private final ObjectMapper objectMapper;
    
    @Transactional
    public GeneratedImage generateImage(Long trendId, MultipartFile[] files, String username) {
        // Get trend and user
        Trend trend = trendService.getTrendById(trendId);
        User user = userService.findByUsername(username)
            .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));
        
        // Validate input images count
        if (files.length > trend.getMaxInputImages()) {
            throw new ImageGenerationException(
                String.format("Too many input images. Maximum allowed: %d", trend.getMaxInputImages())
            );
        }
        
        // Store input files to MinIO (in "user-uploads" folder)
        List<String> inputImagePaths = fileStorageService.storeFiles(files, "user-uploads");
        
        // Convert to JSON array
        String inputImagesJson;
        try {
            inputImagesJson = objectMapper.writeValueAsString(inputImagePaths);
        } catch (JsonProcessingException e) {
            throw new ImageGenerationException("Failed to serialize input images", e);
        }
        
        // Create GeneratedImage entity
        GeneratedImage generatedImage = new GeneratedImage();
        generatedImage.setUser(user);
        generatedImage.setTrend(trend);
        generatedImage.setInputImages(inputImagesJson);
        generatedImage.setPromptUsed(trend.getPromptTemplate());
        generatedImage.setStatus(GenerationStatus.PENDING);
        
        // Save to database
        GeneratedImage savedImage = generatedImageRepository.save(generatedImage);
        log.info("Created generation request: {}", savedImage.getId());
        
        // Increment trend usage count
        trendService.incrementUsageCount(trendId);
        
        // Process generation asynchronously (in real implementation)
        processGeneration(savedImage);
        
        return savedImage;
    }
    
    private void processGeneration(GeneratedImage generatedImage) {
        try {
            // Update status to processing
            generatedImage.setStatus(GenerationStatus.PROCESSING);
            generatedImageRepository.save(generatedImage);
            
            long startTime = System.currentTimeMillis();
            
            // Call AI service (mock implementation for now)
            String outputImagePath = aiService.generateImage(
                generatedImage.getPromptUsed(),
                generatedImage.getInputImages()
            );
            
            long endTime = System.currentTimeMillis();
            BigDecimal generationTime = BigDecimal.valueOf((endTime - startTime) / 1000.0);
            
            // Update with result
            generatedImage.setOutputImagePath(outputImagePath);
            generatedImage.setGenerationTimeSeconds(generationTime);
            generatedImage.setStatus(GenerationStatus.COMPLETED);
            generatedImageRepository.save(generatedImage);
            
            log.info("Image generation completed: {}", generatedImage.getId());
            
        } catch (Exception e) {
            log.error("Image generation failed: {}", generatedImage.getId(), e);
            generatedImage.setStatus(GenerationStatus.FAILED);
            generatedImage.setErrorMessage(e.getMessage());
            generatedImageRepository.save(generatedImage);
        }
    }
    
    @Transactional(readOnly = true)
    public GeneratedImage getGeneratedImage(Long imageId) {
        GeneratedImage image = generatedImageRepository.findById(imageId)
            .orElseThrow(() -> new ImageGenerationException("Generated image not found: " + imageId));
        
        // Force initialization of lazy-loaded associations
        image.getTrend().getTrendName(); // Initialize trend
        image.getUser().getUsername();   // Initialize user
        
        return image;
    }
    
    @Transactional(readOnly = true)
    public Page<GeneratedImage> getUserImages(Long userId, Pageable pageable) {
        return generatedImageRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
    }
    
    @Transactional(readOnly = true)
    public Page<GeneratedImage> getUserImagesByUsername(String username, Pageable pageable) {
        User user = userService.findByUsername(username)
            .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));
        return getUserImages(user.getId(), pageable);
    }
}

