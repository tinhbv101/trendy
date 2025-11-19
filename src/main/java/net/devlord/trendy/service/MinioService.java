package net.devlord.trendy.service;

import io.minio.*;
import io.minio.http.Method;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class MinioService {

    private final MinioClient minioClient;

    @Value("${minio.bucket-name}")
    private String bucketName;

    @Value("${minio.endpoint}")
    private String minioEndpoint;

    /**
     * Upload file to MinIO
     *
     * @param file MultipartFile to upload
     * @param folder Folder path in bucket (e.g., "trends", "generated", "uploads")
     * @return Object name (path) of uploaded file
     */
    public String uploadFile(MultipartFile file, String folder) {
        try {
            // Generate unique filename
            String originalFilename = file.getOriginalFilename();
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            String objectName = folder + "/" + UUID.randomUUID() + extension;

            // Upload file
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .stream(file.getInputStream(), file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build()
            );

            log.info("File uploaded successfully: {}", objectName);
            return objectName;

        } catch (Exception e) {
            log.error("Failed to upload file to MinIO: {}", e.getMessage(), e);
            throw new RuntimeException("Could not upload file to MinIO", e);
        }
    }

    /**
     * Upload file from InputStream
     *
     * @param inputStream InputStream of file
     * @param folder Folder path
     * @param filename Original filename
     * @param contentType Content type
     * @param size File size
     * @return Object name
     */
    public String uploadFile(InputStream inputStream, String folder, String filename, 
                            String contentType, long size) {
        try {
            String extension = "";
            if (filename != null && filename.contains(".")) {
                extension = filename.substring(filename.lastIndexOf("."));
            }
            String objectName = folder + "/" + UUID.randomUUID() + extension;

            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .stream(inputStream, size, -1)
                            .contentType(contentType)
                            .build()
            );

            log.info("File uploaded from stream: {}", objectName);
            return objectName;

        } catch (Exception e) {
            log.error("Failed to upload file from stream: {}", e.getMessage(), e);
            throw new RuntimeException("Could not upload file to MinIO", e);
        }
    }

    /**
     * Get file from MinIO
     *
     * @param objectName Object name (path)
     * @return InputStream of file
     */
    public InputStream getFile(String objectName) {
        try {
            return minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .build()
            );
        } catch (Exception e) {
            log.error("Failed to get file from MinIO: {}", e.getMessage(), e);
            throw new RuntimeException("Could not get file from MinIO", e);
        }
    }

    /**
     * Delete file from MinIO
     *
     * @param objectName Object name (path)
     */
    public void deleteFile(String objectName) {
        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .build()
            );
            log.info("File deleted: {}", objectName);
        } catch (Exception e) {
            log.error("Failed to delete file from MinIO: {}", e.getMessage(), e);
            throw new RuntimeException("Could not delete file from MinIO", e);
        }
    }

    /**
     * Get presigned URL for file access (expires in 7 days)
     *
     * @param objectName Object name (path)
     * @return Presigned URL
     */
    public String getPresignedUrl(String objectName) {
        return getPresignedUrl(objectName, 7, TimeUnit.DAYS);
    }

    /**
     * Get presigned URL with custom expiration
     *
     * @param objectName Object name
     * @param duration Duration
     * @param unit Time unit
     * @return Presigned URL
     */
    public String getPresignedUrl(String objectName, int duration, TimeUnit unit) {
        try {
            return minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Method.GET)
                            .bucket(bucketName)
                            .object(objectName)
                            .expiry((int) unit.toSeconds(duration), TimeUnit.SECONDS)
                            .build()
            );
        } catch (Exception e) {
            log.error("Failed to get presigned URL: {}", e.getMessage(), e);
            throw new RuntimeException("Could not generate presigned URL", e);
        }
    }

    /**
     * Check if file exists
     *
     * @param objectName Object name
     * @return true if exists
     */
    public boolean fileExists(String objectName) {
        try {
            minioClient.statObject(
                    StatObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .build()
            );
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Get file URL (public if bucket is public)
     *
     * @param objectName Object name
     * @return File URL
     */
    public String getFileUrl(String objectName) {
        return String.format("%s/%s/%s", 
                minioEndpoint, bucketName, objectName);
    }
}

