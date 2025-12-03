package net.devlord.trendy.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devlord.trendy.exception.ImageGenerationException;
import net.devlord.trendy.model.dto.ImageEditRequest;
import net.devlord.trendy.model.dto.ImageEditResult;
import net.devlord.trendy.model.entity.GeneratedImage;
import net.devlord.trendy.model.entity.Trend;
import net.devlord.trendy.model.entity.User;
import net.devlord.trendy.model.enums.ImageEditType;
import net.devlord.trendy.model.enums.GenerationStatus;
import net.devlord.trendy.model.enums.TrendStatus;
import net.devlord.trendy.model.enums.AIModel;
import net.devlord.trendy.model.enums.AspectRatio;
import net.devlord.trendy.repository.GeneratedImageRepository;
import net.devlord.trendy.repository.TrendRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Service for AI-powered image editing operations
 * 
 * Supports:
 * - Background Removal
 * - Style Transfer
 * - Image Upscaling
 * - Inpainting
 * - Color Grading
 * - Face Enhancement
 * - AI-Guided Editing
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class ImageEditService {
    
    private final GeminiService geminiService;
    private final MinioService minioService;
    private final GeneratedImageRepository generatedImageRepository;
    private final TrendRepository trendRepository;
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    @Value("${gemini.api.key}")
    private String geminiApiKey;
    
    @Value("${gemini.api.url:https://generativelanguage.googleapis.com/v1beta}")
    private String geminiApiUrl;
    
    /**
     * Process image editing request
     */
    public ImageEditResult editImage(ImageEditRequest request, User user) {
        long startTime = System.currentTimeMillis();
        
        try {
            log.info("Processing image edit request: type={}, user={}", 
                    request.getEditType(), user.getUsername());
            
            // Validate request
            validateRequest(request);
            
            // Get source image
            String sourceImagePath = getSourceImagePath(request);
            
            // Process based on edit type
            ImageEditResult result = switch (request.getEditType()) {
                case BACKGROUND_REMOVAL -> removeBackground(sourceImagePath, request);
                case STYLE_TRANSFER -> transferStyle(sourceImagePath, request);
                case IMAGE_UPSCALING -> upscaleImage(sourceImagePath, request);
                case INPAINTING -> inpaintImage(sourceImagePath, request);
                case COLOR_GRADING -> gradeColors(sourceImagePath, request);
                case FACE_ENHANCEMENT -> enhanceFace(sourceImagePath, request);
                case AI_GUIDED_EDIT -> aiGuidedEdit(sourceImagePath, request);
                case PHOTO_RESTORATION -> restorePhoto(sourceImagePath, request);
                case ARTISTIC_FILTER -> applyArtisticFilter(sourceImagePath, request);
                case SMART_CROP -> smartCrop(sourceImagePath, request);
            };
            
            // Calculate processing time
            long processingTime = System.currentTimeMillis() - startTime;
            result.setProcessingTimeMs(processingTime);
            result.setCompletedAt(LocalDateTime.now());
            result.setSuccess(true);
            result.setCreditCost(request.getEditType().getCreditCost());
            
            // Save to database
            saveEditedImage(result, request, user);
            
            log.info("Image edit completed successfully in {}ms", processingTime);
            return result;
            
        } catch (Exception e) {
            log.error("Error processing image edit: {}", e.getMessage(), e);
            long processingTime = System.currentTimeMillis() - startTime;
            
            return ImageEditResult.builder()
                    .success(false)
                    .errorMessage(e.getMessage())
                    .processingTimeMs(processingTime)
                    .editType(request.getEditType())
                    .build();
        }
    }
    
    /**
     * Remove background from image
     */
    private ImageEditResult removeBackground(String sourceImagePath, ImageEditRequest request) {
        log.info("Removing background from image: {}", sourceImagePath);
        
        // Detect aspect ratio from source image
        AspectRatio aspectRatio = detectAspectRatio(sourceImagePath);
        
        // Use Gemini AI to remove background
        String prompt = "Remove the background from this image, keeping only the main subject. " +
                       "Make the background transparent or white. Preserve all details of the main subject.";
        
        if (request.getPrompt() != null && !request.getPrompt().isEmpty()) {
            prompt += " Additional instructions: " + request.getPrompt();
        }
        
        String editedImageUrl = geminiService.generateImageWithInput(
                prompt, 
                "[\"" + sourceImagePath + "\"]",
                aspectRatio,
                AIModel.GEMINI_3_PRO
        );
        
        return ImageEditResult.builder()
                .imageUrl(editedImageUrl)
                .editType(ImageEditType.BACKGROUND_REMOVAL)
                .originalImageUrl(sourceImagePath)
                .metadata("{\"operation\":\"background_removal\"}")
                .build();
    }
    
    /**
     * Transfer artistic style to image
     */
    private ImageEditResult transferStyle(String sourceImagePath, ImageEditRequest request) {
        log.info("Transferring style '{}' to image: {}", request.getStyleName(), sourceImagePath);
        
        // Detect aspect ratio from source image
        AspectRatio aspectRatio = detectAspectRatio(sourceImagePath);
        
        String styleName = request.getStyleName() != null ? request.getStyleName() : "Van Gogh";
        
        String prompt = String.format(
                "Transform this image into the artistic style of %s. " +
                "Maintain the composition and subject matter, but apply the distinctive " +
                "brushstrokes, colors, and techniques characteristic of %s's art style.",
                styleName, styleName
        );
        
        if (request.getPrompt() != null && !request.getPrompt().isEmpty()) {
            prompt += " " + request.getPrompt();
        }
        
        String editedImageUrl = geminiService.generateImageWithInput(
                prompt,
                "[\"" + sourceImagePath + "\"]",
                aspectRatio,
                AIModel.GEMINI_3_PRO
        );
        
        return ImageEditResult.builder()
                .imageUrl(editedImageUrl)
                .editType(ImageEditType.STYLE_TRANSFER)
                .originalImageUrl(sourceImagePath)
                .metadata("{\"style\":\"" + styleName + "\"}")
                .build();
    }
    
    /**
     * Upscale image resolution
     */
    private ImageEditResult upscaleImage(String sourceImagePath, ImageEditRequest request) {
        int upscaleFactor = request.getUpscaleFactor() != null ? request.getUpscaleFactor() : 2;
        log.info("Upscaling image {}x: {}", upscaleFactor, sourceImagePath);
        
        // Detect aspect ratio from source image
        AspectRatio aspectRatio = detectAspectRatio(sourceImagePath);
        
        String prompt = String.format(
                "Upscale this image to %dx the resolution. " +
                "Enhance details, sharpen edges, and improve overall quality. " +
                "Use AI super-resolution to add realistic details while maintaining the original style.",
                upscaleFactor
        );
        
        String editedImageUrl = geminiService.generateImageWithInput(
                prompt,
                "[\"" + sourceImagePath + "\"]",
                aspectRatio,
                AIModel.GEMINI_3_PRO
        );
        
        return ImageEditResult.builder()
                .imageUrl(editedImageUrl)
                .editType(ImageEditType.IMAGE_UPSCALING)
                .originalImageUrl(sourceImagePath)
                .metadata("{\"upscale_factor\":" + upscaleFactor + "}")
                .build();
    }
    
    /**
     * Inpaint image (remove/replace objects)
     */
    private ImageEditResult inpaintImage(String sourceImagePath, ImageEditRequest request) {
        log.info("Inpainting image: {}", sourceImagePath);
        
        // Detect aspect ratio from source image
        AspectRatio aspectRatio = detectAspectRatio(sourceImagePath);
        
        String prompt = request.getPrompt() != null ? request.getPrompt() : 
                "Remove unwanted objects from this image and fill in the area naturally.";
        
        String editedImageUrl = geminiService.generateImageWithInput(
                prompt,
                "[\"" + sourceImagePath + "\"]",
                aspectRatio,
                AIModel.GEMINI_3_PRO
        );
        
        return ImageEditResult.builder()
                .imageUrl(editedImageUrl)
                .editType(ImageEditType.INPAINTING)
                .originalImageUrl(sourceImagePath)
                .metadata("{\"operation\":\"inpainting\"}")
                .build();
    }
    
    /**
     * Apply color grading to image
     */
    private ImageEditResult gradeColors(String sourceImagePath, ImageEditRequest request) {
        String preset = request.getColorGradingPreset() != null ? 
                request.getColorGradingPreset() : "vibrant";
        
        log.info("Applying color grading '{}' to image: {}", preset, sourceImagePath);
        
        // Detect aspect ratio from source image
        AspectRatio aspectRatio = detectAspectRatio(sourceImagePath);
        
        Map<String, String> presetPrompts = Map.of(
                "warm", "Apply warm color grading with golden and orange tones, like a sunset",
                "cool", "Apply cool color grading with blue and teal tones, like a winter scene",
                "vibrant", "Enhance colors to be more vibrant and saturated, making them pop",
                "vintage", "Apply vintage color grading with faded colors and film-like quality",
                "cinematic", "Apply cinematic color grading with rich contrast and movie-like tones",
                "moody", "Apply moody color grading with dark shadows and dramatic atmosphere"
        );
        
        String prompt = presetPrompts.getOrDefault(preset.toLowerCase(), 
                "Adjust the colors of this image to look more appealing");
        
        if (request.getPrompt() != null && !request.getPrompt().isEmpty()) {
            prompt += ". " + request.getPrompt();
        }
        
        String editedImageUrl = geminiService.generateImageWithInput(
                prompt,
                "[\"" + sourceImagePath + "\"]",
                aspectRatio,
                AIModel.GEMINI_3_PRO
        );
        
        return ImageEditResult.builder()
                .imageUrl(editedImageUrl)
                .editType(ImageEditType.COLOR_GRADING)
                .originalImageUrl(sourceImagePath)
                .metadata("{\"preset\":\"" + preset + "\"}")
                .build();
    }
    
    /**
     * Enhance facial features
     */
    private ImageEditResult enhanceFace(String sourceImagePath, ImageEditRequest request) {
        double strength = request.getEnhancementStrength() != null ? 
                request.getEnhancementStrength() : 0.7;
        
        log.info("Enhancing face with strength {}: {}", strength, sourceImagePath);
        
        // Detect aspect ratio from source image
        AspectRatio aspectRatio = detectAspectRatio(sourceImagePath);
        
        String prompt = String.format(
                "Enhance the facial features in this image. " +
                "Improve skin texture, brighten eyes, enhance facial symmetry, " +
                "and make the face look more attractive while keeping it natural. " +
                "Enhancement strength: %.1f (0=subtle, 1=strong)",
                strength
        );
        
        if (request.getPrompt() != null && !request.getPrompt().isEmpty()) {
            prompt += " " + request.getPrompt();
        }
        
        String editedImageUrl = geminiService.generateImageWithInput(
                prompt,
                "[\"" + sourceImagePath + "\"]",
                aspectRatio,
                AIModel.GEMINI_3_PRO
        );
        
        return ImageEditResult.builder()
                .imageUrl(editedImageUrl)
                .editType(ImageEditType.FACE_ENHANCEMENT)
                .originalImageUrl(sourceImagePath)
                .metadata("{\"strength\":" + strength + "}")
                .build();
    }
    
    /**
     * AI-guided image editing with custom prompt
     */
    private ImageEditResult aiGuidedEdit(String sourceImagePath, ImageEditRequest request) {
        log.info("AI-guided editing: {}", sourceImagePath);
        
        if (request.getPrompt() == null || request.getPrompt().isEmpty()) {
            throw new ImageGenerationException("Prompt is required for AI-guided editing");
        }
        
        // Detect aspect ratio from source image
        AspectRatio aspectRatio = detectAspectRatio(sourceImagePath);
        
        String editedImageUrl = geminiService.generateImageWithInput(
                request.getPrompt(),
                "[\"" + sourceImagePath + "\"]",
                aspectRatio,
                AIModel.GEMINI_3_PRO
        );
        
        return ImageEditResult.builder()
                .imageUrl(editedImageUrl)
                .editType(ImageEditType.AI_GUIDED_EDIT)
                .originalImageUrl(sourceImagePath)
                .metadata("{\"prompt\":\"" + request.getPrompt() + "\"}")
                .build();
    }
    
    /**
     * Restore old or damaged photos
     */
    private ImageEditResult restorePhoto(String sourceImagePath, ImageEditRequest request) {
        log.info("Restoring photo: {}", sourceImagePath);
        
        // Detect aspect ratio from source image
        AspectRatio aspectRatio = detectAspectRatio(sourceImagePath);
        
        String prompt = "Restore this old or damaged photo. " +
                       "Remove scratches, stains, and damage. " +
                       "Enhance clarity, fix faded colors, and improve overall quality. " +
                       "Make it look like a professionally restored photograph while maintaining authenticity.";
        
        if (request.getPrompt() != null && !request.getPrompt().isEmpty()) {
            prompt += " " + request.getPrompt();
        }
        
        String editedImageUrl = geminiService.generateImageWithInput(
                prompt,
                "[\"" + sourceImagePath + "\"]",
                aspectRatio,
                AIModel.GEMINI_3_PRO
        );
        
        return ImageEditResult.builder()
                .imageUrl(editedImageUrl)
                .editType(ImageEditType.PHOTO_RESTORATION)
                .originalImageUrl(sourceImagePath)
                .metadata("{\"operation\":\"photo_restoration\"}")
                .build();
    }
    
    /**
     * Apply artistic filters and effects
     */
    private ImageEditResult applyArtisticFilter(String sourceImagePath, ImageEditRequest request) {
        log.info("Applying artistic filter to image: {}", sourceImagePath);
        
        // Detect aspect ratio from source image
        AspectRatio aspectRatio = detectAspectRatio(sourceImagePath);
        
        String filterType = request.getStyleName() != null ? request.getStyleName() : "artistic";
        
        String prompt = String.format(
                "Apply an artistic filter to this image. " +
                "Transform it with creative effects, enhanced colors, and artistic touches. " +
                "Style: %s. Make it visually striking and artistic.",
                filterType
        );
        
        if (request.getPrompt() != null && !request.getPrompt().isEmpty()) {
            prompt += " " + request.getPrompt();
        }
        
        String editedImageUrl = geminiService.generateImageWithInput(
                prompt,
                "[\"" + sourceImagePath + "\"]",
                aspectRatio,
                AIModel.GEMINI_3_PRO
        );
        
        return ImageEditResult.builder()
                .imageUrl(editedImageUrl)
                .editType(ImageEditType.ARTISTIC_FILTER)
                .originalImageUrl(sourceImagePath)
                .metadata("{\"filter\":\"" + filterType + "\"}")
                .build();
    }
    
    /**
     * Smart crop and composition
     */
    private ImageEditResult smartCrop(String sourceImagePath, ImageEditRequest request) {
        log.info("Smart cropping image: {}", sourceImagePath);
        
        // Detect aspect ratio from source image
        AspectRatio aspectRatio = detectAspectRatio(sourceImagePath);
        
        String prompt = "Intelligently crop and reframe this image. " +
                       "Focus on the main subject, apply rule of thirds, " +
                       "and create a well-balanced composition. " +
                       "Remove unnecessary elements and improve the overall framing.";
        
        if (request.getPrompt() != null && !request.getPrompt().isEmpty()) {
            prompt += " " + request.getPrompt();
        }
        
        String editedImageUrl = geminiService.generateImageWithInput(
                prompt,
                "[\"" + sourceImagePath + "\"]",
                aspectRatio,
                AIModel.GEMINI_3_PRO
        );
        
        return ImageEditResult.builder()
                .imageUrl(editedImageUrl)
                .editType(ImageEditType.SMART_CROP)
                .originalImageUrl(sourceImagePath)
                .metadata("{\"operation\":\"smart_crop\"}")
                .build();
    }
    
    /**
     * Validate edit request
     */
    private void validateRequest(ImageEditRequest request) {
        if (request.getEditType() == null) {
            throw new ImageGenerationException("Edit type is required");
        }
        
        if (request.getSourceImageId() == null && 
            (request.getSourceImageUrl() == null || request.getSourceImageUrl().isEmpty())) {
            throw new ImageGenerationException("Source image ID or URL is required");
        }
        
        // Validate specific requirements for each edit type
        if (request.getEditType() == ImageEditType.STYLE_TRANSFER && 
            request.getStyleName() == null) {
            request.setStyleName("Van Gogh"); // Default style
        }
        
        if (request.getEditType() == ImageEditType.IMAGE_UPSCALING && 
            request.getUpscaleFactor() == null) {
            request.setUpscaleFactor(2); // Default 2x
        }
    }
    
    /**
     * Get source image path from request
     */
    private String getSourceImagePath(ImageEditRequest request) {
        if (request.getSourceImageId() != null) {
            GeneratedImage image = generatedImageRepository.findById(request.getSourceImageId())
                    .orElseThrow(() -> new ImageGenerationException("Source image not found"));
            return image.getOutputImagePath();
        }
        return request.getSourceImageUrl();
    }
    
    /**
     * Save edited image to database
     */
    private void saveEditedImage(ImageEditResult result, ImageEditRequest request, User user) {
        try {
            Trend trend = null;
            
            // Try to get trend from original image if available
            if (request.getSourceImageId() != null) {
                GeneratedImage originalImage = generatedImageRepository.findById(request.getSourceImageId()).orElse(null);
                if (originalImage != null) {
                    trend = originalImage.getTrend();
                }
            }
            
            // If no trend from original image, get or create default "AI Image Editing" trend
            if (trend == null) {
                trend = getOrCreateDefaultTrend();
            }
            
            GeneratedImage editedImage = new GeneratedImage();
            editedImage.setUser(user);
            editedImage.setTrend(trend);
            editedImage.setOutputImagePath(result.getImageUrl());
            editedImage.setPromptUsed("Edit: " + request.getEditType().getDisplayName());
            editedImage.setStatus(GenerationStatus.COMPLETED);
            editedImage.setCreatedAt(LocalDateTime.now());
            
            // Store edit metadata in aiParameters field
            Map<String, Object> metadata = new HashMap<>();
            metadata.put("editType", request.getEditType().name());
            metadata.put("originalImage", result.getOriginalImageUrl());
            metadata.put("processingTimeMs", result.getProcessingTimeMs());
            
            editedImage.setAiParameters(objectMapper.writeValueAsString(metadata));
            
            GeneratedImage saved = generatedImageRepository.save(editedImage);
            result.setImageId(saved.getId());
            
            log.info("Saved edited image with ID: {}", saved.getId());
            
        } catch (Exception e) {
            log.error("Error saving edited image: {}", e.getMessage(), e);
        }
    }
    
    /**
     * Get or create default trend for AI image editing
     */
    private Trend getOrCreateDefaultTrend() {
        // Try to find existing "AI Image Editing" trend
        List<Trend> trends = trendRepository.findByStatusAndDeletedAtIsNull(TrendStatus.ACTIVE);
        for (Trend trend : trends) {
            if ("AI Image Editing".equals(trend.getTrendName())) {
                return trend;
            }
        }
        
        // Create new default trend if not found
        Trend defaultTrend = new Trend();
        defaultTrend.setTrendName("AI Image Editing");
        defaultTrend.setDescription("Images edited using AI-powered tools");
        defaultTrend.setPromptTemplate("Edit this image using AI: {prompt}");
        defaultTrend.setCategory("AI Tools");
        defaultTrend.setStatus(TrendStatus.ACTIVE);
        defaultTrend.setUsageCount(0);
        defaultTrend.setMaxInputImages(1);
        defaultTrend.setAspectRatio(AspectRatio.SQUARE);
        defaultTrend.setAiModel(AIModel.GEMINI_3_PRO);
        defaultTrend.setCreatedAt(LocalDateTime.now());
        
        Trend saved = trendRepository.save(defaultTrend);
        log.info("Created default AI Image Editing trend with ID: {}", saved.getId());
        
        return saved;
    }
    
    /**
     * Get available edit types with descriptions
     */
    public List<Map<String, Object>> getAvailableEditTypes() {
        List<Map<String, Object>> editTypes = new ArrayList<>();
        
        for (ImageEditType type : ImageEditType.values()) {
            Map<String, Object> info = new HashMap<>();
            info.put("type", type.name());
            info.put("displayName", type.getDisplayName());
            info.put("description", type.getDescription());
            info.put("creditCost", type.getCreditCost());
            
            // Add i18n keys for localization
            String typeKey = type.name().toLowerCase();
            info.put("nameKey", "image_edit.tool." + typeKey + ".name");
            info.put("descriptionKey", "image_edit.tool." + typeKey + ".description");
            
            editTypes.add(info);
        }
        
        return editTypes;
    }
    
    /**
     * Get available style presets for style transfer
     */
    public List<String> getAvailableStyles() {
        return Arrays.asList(
                "Van Gogh", "Picasso", "Monet", "Dali", "Anime", "Manga",
                "Watercolor", "Oil Painting", "Sketch", "Comic Book",
                "Cyberpunk", "Fantasy", "Realistic", "Abstract"
        );
    }
    
    /**
     * Get available color grading presets
     */
    public List<String> getColorGradingPresets() {
        return Arrays.asList(
                "warm", "cool", "vibrant", "vintage", "cinematic", "moody",
                "black_and_white", "sepia", "pastel", "neon"
        );
    }
    
    /**
     * Detect aspect ratio from source image path
     * Returns the closest AspectRatio enum value based on image dimensions
     */
    private AspectRatio detectAspectRatio(String sourceImagePath) {
        InputStream inputStream = null;
        try {
            // Get image from MinIO
            inputStream = minioService.getFile(sourceImagePath);
            
            // Read image dimensions using ImageIO
            java.awt.image.BufferedImage image = javax.imageio.ImageIO.read(inputStream);
            
            if (image == null) {
                log.warn("Could not read image dimensions for: {}, using SQUARE as default", sourceImagePath);
                return AspectRatio.SQUARE;
            }
            
            int width = image.getWidth();
            int height = image.getHeight();
            double ratio = (double) width / height;
            
            log.info("Detected image dimensions: {}x{}, ratio: {}", width, height, ratio);
            
            // Match to closest aspect ratio
            // SQUARE: 1:1 (ratio = 1.0)
            // LANDSCAPE: 16:9 (ratio ≈ 1.78)
            // PORTRAIT: 9:16 (ratio ≈ 0.56)
            
            if (Math.abs(ratio - 1.0) < 0.15) {
                return AspectRatio.SQUARE;  // 1:1
            } else if (ratio > 1.3) {
                return AspectRatio.LANDSCAPE;  // 16:9 or wider
            } else {
                return AspectRatio.PORTRAIT;  // 9:16 or taller
            }
            
        } catch (Exception e) {
            log.error("Error detecting aspect ratio for image: {}, using SQUARE as default", sourceImagePath, e);
            return AspectRatio.SQUARE;
        } finally {
            // Close the input stream
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (Exception e) {
                    log.warn("Failed to close input stream: {}", e.getMessage());
                }
            }
        }
    }
}
