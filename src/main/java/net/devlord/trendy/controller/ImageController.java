package net.devlord.trendy.controller;

import net.devlord.trendy.service.MinioService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.InputStream;

@Controller
@RequestMapping("/images")
@RequiredArgsConstructor
@Slf4j
public class ImageController {

    private final MinioService minioService;

    /**
     * Serve image from MinIO
     * URL format: /images/{folder}/{filename}
     * Example: /images/generated/abc-123.jpg
     */
    @GetMapping("/{folder}/{filename:.+}")
    public ResponseEntity<InputStreamResource> getImage(
            @PathVariable String folder,
            @PathVariable String filename) {
        try {
            String objectName = folder + "/" + filename;
            
            // Check if file exists
            if (!minioService.fileExists(objectName)) {
                log.warn("Image not found in MinIO: {}", objectName);
                return ResponseEntity.notFound().build();
            }

            // Get file from MinIO
            InputStream inputStream = minioService.getFile(objectName);
            InputStreamResource resource = new InputStreamResource(inputStream);

            // Determine content type
            String contentType = determineContentType(filename);

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CACHE_CONTROL, "max-age=3600")
                    .body(resource);

        } catch (Exception e) {
            log.error("Failed to serve image from MinIO: {}/{}", folder, filename, e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Get presigned URL for image (for external access)
     */
    @GetMapping("/url/{folder}/{filename:.+}")
    public ResponseEntity<String> getImageUrl(
            @PathVariable String folder,
            @PathVariable String filename) {
        try {
            String objectName = folder + "/" + filename;
            String url = minioService.getPresignedUrl(objectName);
            return ResponseEntity.ok(url);
        } catch (Exception e) {
            log.error("Failed to get presigned URL: {}/{}", folder, filename, e);
            return ResponseEntity.internalServerError().build();
        }
    }

    private String determineContentType(String filename) {
        String lowercase = filename.toLowerCase();
        if (lowercase.endsWith(".jpg") || lowercase.endsWith(".jpeg")) {
            return "image/jpeg";
        } else if (lowercase.endsWith(".png")) {
            return "image/png";
        } else if (lowercase.endsWith(".gif")) {
            return "image/gif";
        } else if (lowercase.endsWith(".webp")) {
            return "image/webp";
        }
        return "application/octet-stream";
    }
}

