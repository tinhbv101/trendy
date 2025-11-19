package net.devlord.trendy.service;

import net.devlord.trendy.exception.FileStorageException;
import net.devlord.trendy.model.entity.Trend;
import net.devlord.trendy.model.entity.TrendExample;
import net.devlord.trendy.repository.TrendExampleRepository;
import net.devlord.trendy.repository.TrendRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class FileStorageService {
    
    private final MinioService minioService;
    private final TrendExampleRepository trendExampleRepository;
    private final TrendRepository trendRepository;
    
    @Value("${file.allowed-types}")
    private String allowedTypesConfig;
    
    @Value("${file.max-size}")
    private long maxFileSize;
    
    public String storeFile(MultipartFile file) {
        return storeFile(file, "uploads");
    }
    
    public String storeFile(MultipartFile file, String folder) {
        // Validate file
        validateFile(file);
        
        try {
            // Upload to MinIO
            String objectName = minioService.uploadFile(file, folder);
            
            log.info("File stored successfully to MinIO: {}", objectName);
            return objectName;
            
        } catch (Exception ex) {
            throw new FileStorageException("Could not store file to MinIO", ex);
        }
    }
    
    public List<String> storeFiles(MultipartFile[] files) {
        return storeFiles(files, "uploads");
    }
    
    public List<String> storeFiles(MultipartFile[] files, String folder) {
        List<String> filenames = new ArrayList<>();
        for (MultipartFile file : files) {
            if (!file.isEmpty()) {
                String filename = storeFile(file, folder);
                filenames.add(filename);
            }
        }
        return filenames;
    }
    
    @Transactional
    public void saveExampleImages(Long trendId, MultipartFile[] files) {
        // Get trend entity
        Trend trend = trendRepository.findById(trendId)
            .orElseThrow(() -> new FileStorageException("Trend not found: " + trendId));
        
        // Store files to MinIO (in "trends" folder) and save to database
        List<String> filenames = storeFiles(files, "trends");
        
        int displayOrder = 0;
        for (String filename : filenames) {
            TrendExample example = new TrendExample();
            example.setTrend(trend);
            example.setImagePath(filename);
            example.setDisplayOrder(displayOrder++);
            trendExampleRepository.save(example);
        }
        
        log.info("Saved {} example images for trend {} to MinIO", filenames.size(), trendId);
    }
    
    public void deleteFile(String objectName) {
        try {
            minioService.deleteFile(objectName);
            log.info("File deleted from MinIO: {}", objectName);
        } catch (Exception ex) {
            log.error("Could not delete file from MinIO: {}", objectName, ex);
        }
    }
    
    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new FileStorageException("Cannot upload empty file");
        }
        
        if (file.getSize() > maxFileSize) {
            throw new FileStorageException("File size exceeds maximum allowed size");
        }
        
        String contentType = file.getContentType();
        List<String> allowedTypes = List.of(allowedTypesConfig.split(","));
        if (contentType == null || !allowedTypes.contains(contentType)) {
            throw new FileStorageException("File type not allowed: " + contentType);
        }
    }
}

