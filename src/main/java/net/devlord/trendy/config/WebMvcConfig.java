package net.devlord.trendy.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC Configuration
 * Note: Local file serving is no longer needed as all files are stored in MinIO
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    
    // All files are now served from MinIO via ImageController
    // No need to expose local upload directory
}

