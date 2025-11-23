package net.devlord.trendy.service;

import net.devlord.trendy.exception.ImageGenerationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.UUID;

/**
 * AI Service for image generation
 * Now integrated with Google Gemini AI (Gemini 3 Pro Image)!
 * Falls back to mock implementation if Gemini is not configured or fails
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class AIService {

    private final GeminiService geminiService;
    private final MinioService minioService;
    
    /**
     * Generate image using AI
     * Uses Gemini, falls back to Mock if not available
     */
    public String generateImage(String prompt, String inputImages) {
        log.info("Generating image with prompt: {}", prompt);
        log.info("Input images: {}", inputImages);

        // Try Gemini (Google AI - best value!)
        if (geminiService.isAvailable()) {
            try {
                log.info("Using Google Gemini AI for generation");
                log.info("Model info: {}", geminiService.getModelInfo());

                String generatedFilename = geminiService.generateImageWithInput(prompt, inputImages);

                log.info("Gemini generation successful: {}", generatedFilename);
                return generatedFilename;

            } catch (Exception e) {
                log.error("Gemini generation failed, falling back to mock", e);
                // Fall through to mock implementation
            }
        }
        
        // Mock implementation fallback
        log.warn("Using MOCK AI generation (Gemini not configured or failed)");
        log.info("To use real AI generation:");
        log.info("  - Set GEMINI_API_KEY environment variable");
        log.info("  - Get free API key at: https://makersuite.google.com/app/apikey");
        
        try {
            // Simulate AI processing time
            Thread.sleep(2000);
            
            // Generate a mock placeholder image
            String generatedFilename = createMockImage(prompt);
            
            log.info("Mock image generated: {}", generatedFilename);
            return generatedFilename;
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ImageGenerationException("Image generation interrupted", e);
        } catch (Exception e) {
            log.error("Failed to generate image", e);
            throw new ImageGenerationException("Failed to generate image: " + e.getMessage(), e);
        }
    }
    
    /**
     * Create a mock placeholder image for testing and upload to MinIO
     */
    private String createMockImage(String prompt) {
        try {
            // Create a simple placeholder image
            int width = 1024;
            int height = 1024;
            BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2d = image.createGraphics();
            
            // Set background gradient
            GradientPaint gradient = new GradientPaint(
                0, 0, new Color(100, 150, 200),
                width, height, new Color(200, 100, 150)
            );
            g2d.setPaint(gradient);
            g2d.fillRect(0, 0, width, height);
            
            // Add text
            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("Arial", Font.BOLD, 40));
            
            // Draw "Mock AI Generated"
            String mockText = "Mock AI Generated";
            FontMetrics fm = g2d.getFontMetrics();
            int textWidth = fm.stringWidth(mockText);
            g2d.drawString(mockText, (width - textWidth) / 2, height / 2 - 50);
            
            // Draw prompt (truncated if too long)
            g2d.setFont(new Font("Arial", Font.PLAIN, 24));
            String truncatedPrompt = prompt.length() > 50 ? prompt.substring(0, 47) + "..." : prompt;
            fm = g2d.getFontMetrics();
            textWidth = fm.stringWidth(truncatedPrompt);
            g2d.drawString(truncatedPrompt, (width - textWidth) / 2, height / 2 + 20);
            
            g2d.dispose();
            
            // Convert to byte array
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, "png", baos);
            byte[] imageBytes = baos.toByteArray();
            
            // Upload to MinIO
            String filename = "generated_mock_" + UUID.randomUUID().toString() + ".png";
            ByteArrayInputStream bais = new ByteArrayInputStream(imageBytes);
            String objectName = minioService.uploadFile(bais, "generated", filename, "image/png", imageBytes.length);
            
            log.info("Created mock image and uploaded to MinIO: {}", objectName);
            return objectName;
            
        } catch (IOException e) {
            log.error("Failed to create mock image", e);
            throw new ImageGenerationException("Failed to create mock image", e);
        }
    }
}
