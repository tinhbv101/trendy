package net.devlord.trendy.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.devlord.trendy.exception.ImageGenerationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.IOException;
import java.util.*;

/**
 * Service for generating images using Google Gemini AI (Native Image Generation)
 * Uses Gemini 3 Pro Image model - supports both text-to-image and image+text-to-image
 *
 * Features:
 * - Native 4K & text rendering
 * - Grounded generation with Google Search
 * - Conversational editing with Thought Signatures
 * - Advanced image generation capabilities
 *
 * Reference: https://ai.google.dev/gemini-api/docs/gemini-3
 */
@Service
@Slf4j
public class GeminiService {
    
    private final String apiKey;
    private final MinioService minioService;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    
    // Gemini 3 Pro Image - Latest image generation model with advanced reasoning
    private static final String GEMINI_IMAGE_MODEL = "gemini-3-pro-image-preview";
    private static final String GEMINI_ENDPOINT = "https://generativelanguage.googleapis.com/v1beta/models/" + GEMINI_IMAGE_MODEL + ":generateContent";
    
    public GeminiService(
            @Value("${gemini.api.key:}") String apiKey,
            MinioService minioService) {
        
        this.apiKey = apiKey;
        this.minioService = minioService;
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
        
        if (isAvailable()) {
            log.info("Google Gemini AI service is configured and ready!");
            log.info("Using model: {}", GEMINI_IMAGE_MODEL);
        } else {
            log.warn("Gemini API key not configured. Set GEMINI_API_KEY environment variable.");
        }
    }
    
    /**
     * Generate image with input image (image + text-to-image editing)
     * Uses Gemini's native image editing capabilities
     */
    public String generateImageWithInput(String prompt, String inputImagesJson) {
        if (!isAvailable()) {
            throw new ImageGenerationException("Gemini service not configured. Please set GEMINI_API_KEY environment variable.");
        }
        
        try {
            // Parse input images
            List<String> imagePaths = objectMapper.readValue(inputImagesJson, 
                    objectMapper.getTypeFactory().constructCollectionType(List.class, String.class));
            
            if (imagePaths.isEmpty()) {
                // No input images, use text-only generation
                return generateImage(prompt);
            }
            
            log.info("Generating image with Gemini (image editing). Prompt: {}, Input images: {}", prompt, imagePaths.size());
            
            // Build request with image + text
            Map<String, Object> requestBody = buildImageEditRequest(prompt, imagePaths);
            
            // Call Gemini API
            String generatedImage = callGeminiAPI(requestBody);
            
            log.info("Gemini image editing successful: {}", generatedImage);
            return generatedImage;
            
        } catch (Exception e) {
            log.error("Failed to generate image with Gemini", e);
            throw new ImageGenerationException("Gemini generation failed: " + e.getMessage(), e);
        }
    }
    
    /**
     * Generate image from text prompt only (text-to-image)
     */
    public String generateImage(String prompt) {
        if (!isAvailable()) {
            throw new ImageGenerationException("Gemini service not configured. Please set GEMINI_API_KEY environment variable.");
        }
        
        log.info("Generating image with Gemini (text-to-image). Prompt: {}", prompt);
        
        try {
            // Build request with text only
            Map<String, Object> requestBody = buildTextToImageRequest(prompt);
            
            // Call Gemini API
            String generatedImage = callGeminiAPI(requestBody);
            
            log.info("Gemini text-to-image successful: {}", generatedImage);
            return generatedImage;
            
        } catch (Exception e) {
            log.error("Failed to generate image with Gemini", e);
            throw new ImageGenerationException("Gemini generation failed: " + e.getMessage(), e);
        }
    }
    
    /**
     * Build request body for text-to-image generation
     */
    private Map<String, Object> buildTextToImageRequest(String prompt) {
        Map<String, Object> requestBody = new HashMap<>();
        
        // Contents
        List<Map<String, Object>> contents = new ArrayList<>();
        Map<String, Object> content = new HashMap<>();
        
        List<Map<String, Object>> parts = new ArrayList<>();
        Map<String, Object> textPart = new HashMap<>();
        textPart.put("text", prompt);
        parts.add(textPart);
        
        content.put("parts", parts);
        contents.add(content);
        
        requestBody.put("contents", contents);
        
        // Generation config for image output
        Map<String, Object> generationConfig = new HashMap<>();
        generationConfig.put("responseModalities", List.of("Image"));
        
        // Image config with aspect ratio and size (Gemini 3 supports 4K)
        Map<String, Object> imageConfig = new HashMap<>();
        imageConfig.put("aspectRatio", "1:1"); // Default to square
        imageConfig.put("imageSize", "2K"); // Use 2K by default (4K available but uses more tokens)
        generationConfig.put("imageConfig", imageConfig);
        
        requestBody.put("generationConfig", generationConfig);
        
        return requestBody;
    }
    
    /**
     * Build request body for image + text-to-image (editing)
     */
    private Map<String, Object> buildImageEditRequest(String prompt, List<String> imagePaths) throws Exception {
        Map<String, Object> requestBody = new HashMap<>();
        
        // Contents with image + text
        List<Map<String, Object>> contents = new ArrayList<>();
        Map<String, Object> content = new HashMap<>();
        
        List<Map<String, Object>> parts = new ArrayList<>();
        
        // Add text prompt first
        Map<String, Object> textPart = new HashMap<>();
        textPart.put("text", prompt);
        parts.add(textPart);
        
        // Add all input images
        int imageCount = 0;
        for (String imagePath : imagePaths) {
            // Get image from MinIO
            if (minioService.fileExists(imagePath)) {
                try {
                    InputStream inputStream = minioService.getFile(imagePath);
                    byte[] imageBytes = inputStream.readAllBytes();
                    inputStream.close();
                    
                    String base64Image = Base64.getEncoder().encodeToString(imageBytes);
                    
                    // Determine MIME type
                    String mimeType = determineMimeType(imagePath);
                    
                    Map<String, Object> imagePart = new HashMap<>();
                    Map<String, Object> inlineData = new HashMap<>();
                    inlineData.put("mimeType", mimeType);
                    inlineData.put("data", base64Image);
                    imagePart.put("inlineData", inlineData);
                    parts.add(imagePart);
                    
                    imageCount++;
                    log.info("Added input image {} to request from MinIO: {}", imageCount, imagePath);
                } catch (Exception e) {
                    log.warn("Failed to load input image from MinIO: {}", imagePath, e);
                }
            } else {
                log.warn("Input image not found in MinIO: {}", imagePath);
            }
        }
        
        if (imageCount == 0) {
            log.warn("No input images found, using text-only generation");
        } else {
            log.info("Total {} input images added to Gemini request", imageCount);
        }
        
        content.put("parts", parts);
        contents.add(content);
        
        requestBody.put("contents", contents);
        
        // Generation config
        Map<String, Object> generationConfig = new HashMap<>();
        generationConfig.put("responseModalities", List.of("Image"));
        
        // Image config - aspect ratio and size (Gemini 3 supports 4K)
        Map<String, Object> imageConfig = new HashMap<>();
        imageConfig.put("aspectRatio", "1:1"); // Default to square
        imageConfig.put("imageSize", "2K"); // Use 2K by default (4K available but uses more tokens)
        generationConfig.put("imageConfig", imageConfig);
        
        requestBody.put("generationConfig", generationConfig);
        
        return requestBody;
    }
    
    /**
     * Call Gemini API and handle response
     */
    private String callGeminiAPI(Map<String, Object> requestBody) throws IOException {
        // Set headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
        
        // Call API with API key in URL
        String url = GEMINI_ENDPOINT + "?key=" + apiKey;
        
        log.debug("Calling Gemini API: {}", url);
        
        ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);
        
        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new ImageGenerationException("Gemini API returned error: " + response.getStatusCode());
        }
        
        Map<String, Object> responseBody = response.getBody();
        if (responseBody == null) {
            throw new ImageGenerationException("Empty response from Gemini API");
        }
        
        // Extract image from response
        // Response structure: candidates[0].content.parts[].inlineData.data (base64)
        List<Map<String, Object>> candidates = (List<Map<String, Object>>) responseBody.get("candidates");
        if (candidates == null || candidates.isEmpty()) {
            throw new ImageGenerationException("No candidates in Gemini response");
        }
        
        Map<String, Object> candidate = candidates.get(0);
        Map<String, Object> content = (Map<String, Object>) candidate.get("content");
        List<Map<String, Object>> parts = (List<Map<String, Object>>) content.get("parts");
        
        // Find the image part
        for (Map<String, Object> part : parts) {
            if (part.containsKey("inlineData")) {
                Map<String, Object> inlineData = (Map<String, Object>) part.get("inlineData");
                String base64Image = (String) inlineData.get("data");
                
                // Save image
                String filename = saveBase64Image(base64Image);
                return filename;
            }
        }
        
        throw new ImageGenerationException("No image found in Gemini response");
    }
    
    /**
     * Save base64 encoded image to MinIO
     */
    private String saveBase64Image(String base64Image) {
        byte[] imageBytes = Base64.getDecoder().decode(base64Image);
        
        String filename = "generated_gemini_" + UUID.randomUUID().toString() + ".png";
        ByteArrayInputStream bais = new ByteArrayInputStream(imageBytes);
        
        String objectName = minioService.uploadFile(bais, "generated", filename, "image/png", imageBytes.length);
        
        log.info("Saved generated image to MinIO: {}", objectName);
        return objectName;
    }
    
    /**
     * Determine MIME type from file extension
     */
    private String determineMimeType(String filename) {
        String lowerFilename = filename.toLowerCase();
        if (lowerFilename.endsWith(".png")) {
            return "image/png";
        } else if (lowerFilename.endsWith(".jpg") || lowerFilename.endsWith(".jpeg")) {
            return "image/jpeg";
        } else if (lowerFilename.endsWith(".gif")) {
            return "image/gif";
        } else if (lowerFilename.endsWith(".webp")) {
            return "image/webp";
        }
        return "image/png"; // Default
    }
    
    /**
     * Check if Gemini service is available
     */
    public boolean isAvailable() {
        return apiKey != null && !apiKey.isEmpty() && !apiKey.equals("your_gemini_api_key_here");
    }
    
    /**
     * Get model information
     */
    public String getModelInfo() {
        return "Google Gemini 3 Pro Image (Native Image Generation with 4K support)";
    }
}
