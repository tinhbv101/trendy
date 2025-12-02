package net.devlord.trendy.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devlord.trendy.model.dto.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Service for AI-powered image analysis using Google Gemini
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ImageAnalysisService {
    
    @Value("${gemini.api.key:}")
    private String apiKey;
    
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    private static final String GEMINI_API_URL = "https://generativelanguage.googleapis.com/v1/models/gemini-2.5-flash:generateContent";
    
    /**
     * Perform comprehensive image analysis
     */
    public ImageAnalysisResult analyzeImage(byte[] imageData, java.util.Locale locale) {
        log.info("Performing comprehensive image analysis in locale: {}", locale);
        
        try {
            String analysisPrompt = buildComprehensiveAnalysisPrompt(locale);
            String response = callGeminiVisionAPI(imageData, analysisPrompt);
            
            return parseAnalysisResponse(response);
            
        } catch (Exception e) {
            log.error("Error analyzing image", e);
            throw new RuntimeException("Failed to analyze image: " + e.getMessage(), e);
        }
    }
    
    /**
     * Generate tags for an image
     */
    public List<String> generateTags(byte[] imageData) {
        log.info("Generating tags for image");
        
        try {
            String tagPrompt = "Analyze this image and generate 10-15 relevant tags. " +
                    "Include: subject, style, mood, colors, composition, technique. " +
                    "Return ONLY a comma-separated list of tags, nothing else.";
            
            String response = callGeminiVisionAPI(imageData, tagPrompt);
            
            // Parse comma-separated tags
            return Arrays.stream(response.split(","))
                    .map(String::trim)
                    .filter(tag -> !tag.isEmpty())
                    .collect(Collectors.toList());
            
        } catch (Exception e) {
            log.error("Error generating tags", e);
            return Collections.emptyList();
        }
    }
    
    /**
     * Get detailed image description
     */
    public String getImageDescription(byte[] imageData) {
        log.info("Getting image description");
        
        try {
            String descPrompt = "Provide a detailed description of this image including: " +
                    "main subject and composition, art style and technique, " +
                    "color palette and mood, notable details. " +
                    "Keep it concise (2-3 sentences).";
            
            return callGeminiVisionAPI(imageData, descPrompt);
            
        } catch (Exception e) {
            log.error("Error getting description", e);
            return "Unable to generate description";
        }
    }
    
    /**
     * Analyze artistic style
     */
    public StyleAnalysis analyzeStyle(byte[] imageData) {
        log.info("Analyzing artistic style");
        
        try {
            String stylePrompt = "Analyze the artistic style of this image. " +
                    "Return a JSON object with these fields: " +
                    "artStyle (e.g., 'impressionism', 'digital art', 'anime'), " +
                    "mood (e.g., 'peaceful', 'energetic'), " +
                    "technique (e.g., 'oil painting', 'digital illustration'), " +
                    "influences (array of similar artists/movements), " +
                    "genre (e.g., 'portrait', 'landscape'), " +
                    "era (e.g., 'modern', 'contemporary'). " +
                    "Return ONLY valid JSON, no markdown formatting.";
            
            String response = callGeminiVisionAPI(imageData, stylePrompt);
            
            // Clean response (remove markdown code blocks if present)
            String cleanJson = response.replaceAll("```json\\s*", "").replaceAll("```\\s*", "").trim();
            
            return objectMapper.readValue(cleanJson, StyleAnalysis.class);
            
        } catch (Exception e) {
            log.error("Error analyzing style", e);
            return StyleAnalysis.builder()
                    .artStyle("unknown")
                    .mood("neutral")
                    .technique("unknown")
                    .build();
        }
    }
    
    /**
     * Extract color palette
     */
    public ColorPalette extractColors(byte[] imageData) {
        log.info("Extracting color palette");
        
        try {
            String colorPrompt = "Analyze the colors in this image. " +
                    "Return a JSON object with these fields: " +
                    "dominantColors (array of hex colors like '#FF5733'), " +
                    "colorScheme (e.g., 'monochromatic', 'complementary'), " +
                    "temperature (e.g., 'warm', 'cool', 'neutral'), " +
                    "saturation (e.g., 'vibrant', 'muted', 'pastel'), " +
                    "brightness (e.g., 'bright', 'dark', 'balanced'), " +
                    "accentColors (array of accent hex colors). " +
                    "Return ONLY valid JSON, no markdown formatting.";
            
            String response = callGeminiVisionAPI(imageData, colorPrompt);
            
            // Clean response
            String cleanJson = response.replaceAll("```json\\s*", "").replaceAll("```\\s*", "").trim();
            
            return objectMapper.readValue(cleanJson, ColorPalette.class);
            
        } catch (Exception e) {
            log.error("Error extracting colors", e);
            return ColorPalette.builder()
                    .dominantColors(Collections.emptyList())
                    .colorScheme("unknown")
                    .temperature("neutral")
                    .build();
        }
    }
    
    /**
     * Get AI-powered edit suggestions
     */
    public List<EditSuggestion> getEditSuggestions(byte[] imageData) {
        log.info("Getting edit suggestions");
        
        try {
            String suggestPrompt = "As a professional image editor, suggest 3-5 improvements for this image. " +
                    "For each suggestion, provide: " +
                    "type (e.g., 'composition', 'color', 'style', 'technical'), " +
                    "suggestion (the actual suggestion), " +
                    "reason (why this would improve the image), " +
                    "priority (1-5, where 1 is highest), " +
                    "impact ('high', 'medium', 'low'), " +
                    "difficulty ('easy', 'moderate', 'difficult'), " +
                    "action (specific action to take). " +
                    "Return as a JSON array. Return ONLY valid JSON, no markdown formatting.";
            
            String response = callGeminiVisionAPI(imageData, suggestPrompt);
            
            // Clean response
            String cleanJson = response.replaceAll("```json\\s*", "").replaceAll("```\\s*", "").trim();
            
            EditSuggestion[] suggestions = objectMapper.readValue(cleanJson, EditSuggestion[].class);
            return Arrays.asList(suggestions);
            
        } catch (Exception e) {
            log.error("Error getting edit suggestions", e);
            return Collections.emptyList();
        }
    }
    
    /**
     * Call Gemini Vision API with image and prompt
     */
    private String callGeminiVisionAPI(byte[] imageData, String prompt) {
        try {
            // Build request body
            Map<String, Object> requestBody = new HashMap<>();
            
            // Contents
            List<Map<String, Object>> contents = new ArrayList<>();
            Map<String, Object> content = new HashMap<>();
            
            List<Map<String, Object>> parts = new ArrayList<>();
            
            // Add text part
            Map<String, Object> textPart = new HashMap<>();
            textPart.put("text", prompt);
            parts.add(textPart);
            
            // Add image part
            Map<String, Object> imagePart = new HashMap<>();
            Map<String, Object> inlineData = new HashMap<>();
            inlineData.put("mimeType", "image/jpeg");
            inlineData.put("data", Base64.getEncoder().encodeToString(imageData));
            imagePart.put("inlineData", inlineData);
            parts.add(imagePart);
            
            content.put("parts", parts);
            contents.add(content);
            
            requestBody.put("contents", contents);
            
            // Generation config
            Map<String, Object> generationConfig = new HashMap<>();
            generationConfig.put("temperature", 0.4);
            generationConfig.put("topK", 32);
            generationConfig.put("topP", 1);
            generationConfig.put("maxOutputTokens", 16384);
            requestBody.put("generationConfig", generationConfig);
            
            // Disable reasoning for faster responses (if supported)
            // Note: Some models may not support this parameter
            
            // Make API call
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            String url = GEMINI_API_URL + "?key=" + apiKey;
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
            
            log.debug("Calling Gemini API with prompt: {}", prompt.substring(0, Math.min(100, prompt.length())));
            
            ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
            
            String responseBody = response.getBody();
            log.debug("Gemini API response status: {}", response.getStatusCode());
            
            if (responseBody == null || responseBody.isEmpty()) {
                throw new RuntimeException("Empty response from Gemini API");
            }
            
            // Parse response
            JsonNode root = objectMapper.readTree(responseBody);
            
            // Check for error in response
            if (root.has("error")) {
                String errorMessage = root.path("error").path("message").asText();
                log.error("Gemini API error: {}", errorMessage);
                throw new RuntimeException("Gemini API error: " + errorMessage);
            }
            
            JsonNode candidates = root.path("candidates");
            
            if (candidates.isArray() && candidates.size() > 0) {
                JsonNode firstCandidate = candidates.get(0);
                
                // Check finish reason
                String finishReason = firstCandidate.path("finishReason").asText();
                if (!finishReason.isEmpty()) {
                    log.debug("Gemini finish reason: {}", finishReason);
                    if ("MAX_TOKENS".equals(finishReason)) {
                        log.warn("Response was truncated due to MAX_TOKENS limit. Consider increasing maxOutputTokens.");
                    }
                }
                
                JsonNode content1 = firstCandidate.path("content");
                
                // Log content structure for debugging
                if (log.isDebugEnabled()) {
                    log.debug("Content structure: {}", content1.toString());
                }
                
                JsonNode parts1 = content1.path("parts");
                
                if (parts1.isArray() && parts1.size() > 0) {
                    String text = parts1.get(0).path("text").asText();
                    if (text != null && !text.isEmpty()) {
                        log.debug("Gemini response text length: {}", text.length());
                        return text;
                    } else {
                        log.warn("Parts array exists but text is empty. Parts: {}", parts1.toString());
                    }
                } else {
                    List<String> keys = new ArrayList<>();
                    content1.fieldNames().forEachRemaining(keys::add);
                    log.warn("No parts found in content. Content keys: {}", 
                        keys.isEmpty() ? "none" : String.join(", ", keys));
                }
                
                // If no parts but finishReason is SAFETY, log it
                if ("SAFETY".equals(finishReason)) {
                    log.warn("Response was blocked due to safety filters");
                    throw new RuntimeException("Response blocked by safety filters");
                }
                
                // If MAX_TOKENS and no parts, it means reasoning consumed all tokens
                if ("MAX_TOKENS".equals(finishReason)) {
                    log.error("Response reached MAX_TOKENS limit before generating any output. " +
                            "This may be due to reasoning tokens consuming the budget. " +
                            "Try reducing prompt complexity or using a model without reasoning.");
                    throw new RuntimeException("Response truncated: reasoning tokens consumed all available tokens before generating output");
                }
            }
            
            log.error("No valid response from Gemini API. Response: {}", responseBody);
            throw new RuntimeException("No response from Gemini API");
            
        } catch (Exception e) {
            log.error("Error calling Gemini Vision API", e);
            throw new RuntimeException("Failed to call Gemini API: " + e.getMessage(), e);
        }
    }
    
    /**
     * Build comprehensive analysis prompt
     */
    private String buildComprehensiveAnalysisPrompt(java.util.Locale locale) {
        String languageInstruction = getLanguageInstruction(locale);
        
        return "Perform a comprehensive analysis of this image. " +
                languageInstruction + " " +
                "Return a JSON object with these fields: " +
                "description (detailed 2-3 sentence description), " +
                "tags (array of 10-15 relevant tags), " +
                "style (object with artStyle, mood, technique, influences array, genre, era), " +
                "colors (object with dominantColors array, colorScheme, temperature, saturation, brightness, accentColors array), " +
                "objects (array of detected objects with name, confidence, position, relativeSize, attributes), " +
                "suggestions (array of 3-5 edit suggestions with type, suggestion, reason, priority, impact, difficulty, action), " +
                "confidence (overall confidence score 0.0-1.0). " +
                "Return ONLY valid JSON, no markdown formatting.";
    }
    
    /**
     * Get language instruction based on user locale
     */
    private String getLanguageInstruction(java.util.Locale locale) {
        String language = locale.getLanguage();
        
        switch (language) {
            case "vi":
                return "IMPORTANT: Respond in Vietnamese (Tiếng Việt). All text fields (description, tags, style, suggestions, etc.) must be in Vietnamese.";
            case "zh":
                return "IMPORTANT: Respond in Chinese (中文). All text fields (description, tags, style, suggestions, etc.) must be in Chinese.";
            case "ar":
                return "IMPORTANT: Respond in Arabic (العربية). All text fields (description, tags, style, suggestions, etc.) must be in Arabic.";
            case "hi":
                return "IMPORTANT: Respond in Hindi (हिन्दी). All text fields (description, tags, style, suggestions, etc.) must be in Hindi.";
            case "de":
                return "IMPORTANT: Respond in German (Deutsch). All text fields (description, tags, style, suggestions, etc.) must be in German.";
            case "fr":
                return "IMPORTANT: Respond in French (Français). All text fields (description, tags, style, suggestions, etc.) must be in French.";
            case "it":
                return "IMPORTANT: Respond in Italian (Italiano). All text fields (description, tags, style, suggestions, etc.) must be in Italian.";
            case "pt":
                return "IMPORTANT: Respond in Portuguese (Português). All text fields (description, tags, style, suggestions, etc.) must be in Portuguese.";
            case "ru":
                return "IMPORTANT: Respond in Russian (Русский). All text fields (description, tags, style, suggestions, etc.) must be in Russian.";
            case "ja":
                return "IMPORTANT: Respond in Japanese (日本語). All text fields (description, tags, style, suggestions, etc.) must be in Japanese.";
            case "ko":
                return "IMPORTANT: Respond in Korean (한국어). All text fields (description, tags, style, suggestions, etc.) must be in Korean.";
            case "id":
                return "IMPORTANT: Respond in Indonesian (Bahasa Indonesia). All text fields (description, tags, style, suggestions, etc.) must be in Indonesian.";
            case "th":
                return "IMPORTANT: Respond in Thai (ไทย). All text fields (description, tags, style, suggestions, etc.) must be in Thai.";
            case "es":
                return "IMPORTANT: Respond in Spanish (Español). All text fields (description, tags, style, suggestions, etc.) must be in Spanish.";
            default:
                return "IMPORTANT: Respond in English. All text fields (description, tags, style, suggestions, etc.) must be in English.";
        }
    }
    
    /**
     * Parse comprehensive analysis response
     */
    private ImageAnalysisResult parseAnalysisResponse(String response) {
        try {
            // Clean response
            String cleanJson = response.replaceAll("```json\\s*", "").replaceAll("```\\s*", "").trim();
            
            return objectMapper.readValue(cleanJson, ImageAnalysisResult.class);
            
        } catch (Exception e) {
            log.error("Error parsing analysis response", e);
            
            // Return partial result
            return ImageAnalysisResult.builder()
                    .description("Analysis completed with partial results")
                    .tags(Collections.emptyList())
                    .confidence(0.5)
                    .build();
        }
    }
    
    /**
     * Check if service is available
     */
    public boolean isAvailable() {
        return apiKey != null && !apiKey.isEmpty();
    }
}
