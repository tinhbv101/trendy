package net.devlord.trendy.controller;

import net.devlord.trendy.model.entity.GeneratedImage;
import net.devlord.trendy.repository.GeneratedImageRepository;
import net.devlord.trendy.service.MinioService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Controller
@RequestMapping("/images")
@RequiredArgsConstructor
@Slf4j
public class ImageController {

    private final MinioService minioService;
    private final GeneratedImageRepository generatedImageRepository;
    private final ObjectMapper objectMapper;

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
                    .header(HttpHeaders.CACHE_CONTROL, "public, max-age=31536000, immutable") // 1 year cache
                    .header(HttpHeaders.EXPIRES, java.time.Instant.now().plusSeconds(31536000).toString())
                    .header("X-Content-Type-Options", "nosniff")
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

    /**
     * Download image with custom filename
     * URL: /images/download/{folder}/{filename}?name={customName}
     */
    @GetMapping("/download/{folder}/{filename:.+}")
    public ResponseEntity<InputStreamResource> downloadImage(
            @PathVariable String folder,
            @PathVariable String filename,
            @RequestParam(required = false) String name) {
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

            // Determine download filename
            String downloadFilename = (name != null && !name.trim().isEmpty()) 
                ? name 
                : filename;
            
            // Ensure proper file extension
            if (!downloadFilename.contains(".")) {
                String extension = getFileExtension(filename);
                downloadFilename += extension;
            }

            // Determine content type
            String contentType = determineContentType(filename);

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, 
                        "attachment; filename=\"" + downloadFilename + "\"")
                    .body(resource);

        } catch (Exception e) {
            log.error("Failed to download image from MinIO: {}/{}", folder, filename, e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Download all input images as ZIP for a generated image
     * URL: /images/download-inputs/{imageId}
     */
    @GetMapping("/download-inputs/{imageId}")
    public ResponseEntity<InputStreamResource> downloadInputImages(
            @PathVariable Long imageId,
            Authentication authentication) {
        try {
            // Get the generated image with user and trend eagerly loaded
            GeneratedImage image = generatedImageRepository.findByIdWithUserAndTrend(imageId)
                    .orElseThrow(() -> new IllegalArgumentException("Image not found"));
            
            // Check ownership
            if (!image.getUser().getUsername().equals(authentication.getName())) {
                log.warn("Unauthorized access attempt to download inputs for image {}", imageId);
                return ResponseEntity.status(403).build();
            }

            // Parse input images JSON
            if (image.getInputImages() == null || image.getInputImages().isEmpty()) {
                log.warn("No input images found for image {}", imageId);
                return ResponseEntity.notFound().build();
            }

            List<String> inputPaths = objectMapper.readValue(
                image.getInputImages(), 
                objectMapper.getTypeFactory().constructCollectionType(List.class, String.class)
            );

            if (inputPaths.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            // Create ZIP file in memory
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try (ZipOutputStream zos = new ZipOutputStream(baos)) {
                int index = 1;
                for (String inputPath : inputPaths) {
                    try {
                        if (minioService.fileExists(inputPath)) {
                            InputStream fileStream = minioService.getFile(inputPath);
                            
                            // Extract filename or create numbered filename
                            String fileName = inputPath.contains("/") 
                                ? inputPath.substring(inputPath.lastIndexOf("/") + 1)
                                : "input-" + index + getFileExtension(inputPath);
                            
                            ZipEntry zipEntry = new ZipEntry(fileName);
                            zos.putNextEntry(zipEntry);
                            
                            byte[] buffer = new byte[1024];
                            int len;
                            while ((len = fileStream.read(buffer)) > 0) {
                                zos.write(buffer, 0, len);
                            }
                            
                            fileStream.close();
                            zos.closeEntry();
                            index++;
                        }
                    } catch (Exception e) {
                        log.error("Failed to add file to ZIP: {}", inputPath, e);
                        // Continue with next file
                    }
                }
            }

            byte[] zipBytes = baos.toByteArray();
            ByteArrayInputStream bais = new ByteArrayInputStream(zipBytes);
            InputStreamResource resource = new InputStreamResource(bais);

            // Create filename with trend name and date
            String trendName = image.getTrend().getTrendName()
                .replaceAll("[^a-zA-Z0-9-_]", "-")
                .toLowerCase();
            String date = image.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            String zipFilename = trendName + "-inputs-" + date + ".zip";

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .header(HttpHeaders.CONTENT_DISPOSITION, 
                        "attachment; filename=\"" + zipFilename + "\"")
                    .contentLength(zipBytes.length)
                    .body(resource);

        } catch (Exception e) {
            log.error("Failed to create ZIP of input images for image {}", imageId, e);
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

    private String getFileExtension(String filename) {
        int lastDot = filename.lastIndexOf('.');
        if (lastDot > 0 && lastDot < filename.length() - 1) {
            return filename.substring(lastDot);
        }
        return ".png"; // default extension
    }
}

