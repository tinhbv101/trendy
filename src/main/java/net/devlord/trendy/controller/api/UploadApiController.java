package net.devlord.trendy.controller.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devlord.trendy.service.MinioService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * API Controller for file uploads
 */
@RestController
@RequestMapping("/api/upload")
@RequiredArgsConstructor
@Slf4j
public class UploadApiController {
    
    private final MinioService minioService;
    
    /**
     * Upload image file
     * 
     * @param file Image file to upload
     * @return Upload result with file path
     */
    @PostMapping("/image")
    public ResponseEntity<Map<String, Object>> uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            log.info("Uploading image: {}, size: {} bytes", file.getOriginalFilename(), file.getSize());
            
            // Validate file
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "File is empty"
                ));
            }
            
            // Check file type
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "File must be an image"
                ));
            }
            
            // Upload to MinIO using the correct method
            String filePath = minioService.uploadFile(file, "uploads");
            
            log.info("Image uploaded successfully: {}", filePath);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("filename", file.getOriginalFilename());
            response.put("path", filePath);
            response.put("url", "/images/" + filePath);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Error uploading image: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(Map.of(
                "success", false,
                "message", "Error uploading file: " + e.getMessage()
            ));
        }
    }
}
