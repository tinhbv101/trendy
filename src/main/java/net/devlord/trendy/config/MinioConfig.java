package net.devlord.trendy.config;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class MinioConfig {

    @Value("${minio.endpoint}")
    private String endpoint;

    @Value("${minio.access-key}")
    private String accessKey;

    @Value("${minio.secret-key}")
    private String secretKey;

    @Value("${minio.bucket-name}")
    private String bucketName;

    @Value("${minio.auto-create-bucket:true}")
    private boolean autoCreateBucket;

    @Bean
    public MinioClient minioClient() {
        try {
            MinioClient minioClient = MinioClient.builder()
                    .endpoint(endpoint)
                    .credentials(accessKey, secretKey)
                    .build();

            log.info("MinIO client initialized successfully: {}", endpoint);

            // Auto-create bucket if enabled
            if (autoCreateBucket) {
                try {
                    boolean bucketExists = minioClient.bucketExists(
                            BucketExistsArgs.builder().bucket(bucketName).build()
                    );

                    if (!bucketExists) {
                        minioClient.makeBucket(
                                MakeBucketArgs.builder().bucket(bucketName).build()
                        );
                        log.info("MinIO bucket created: {}", bucketName);
                    } else {
                        log.info("MinIO bucket already exists: {}", bucketName);
                    }
                } catch (Exception e) {
                    log.warn("Could not check/create bucket (MinIO may not be running): {}", e.getMessage());
                }
            }

            return minioClient;
        } catch (Exception e) {
            log.error("Failed to initialize MinIO client: {}", e.getMessage());
            log.warn("Application will continue without MinIO - some features may not work");
            // Return a dummy client instead of throwing exception
            return MinioClient.builder()
                    .endpoint(endpoint)
                    .credentials(accessKey, secretKey)
                    .build();
        }
    }
}

