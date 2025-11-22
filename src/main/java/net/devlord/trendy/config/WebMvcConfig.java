package net.devlord.trendy.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.CacheControl;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.concurrent.TimeUnit;

/**
 * Web MVC Configuration
 * Note: Local file serving is no longer needed as all files are stored in MinIO
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    
    // All files are now served from MinIO via ImageController
    // No need to expose local upload directory
    
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Configure cache headers for static resources
        // Cache for 1 year (365 days) with immutable flag for better performance
        CacheControl cacheControl = CacheControl.maxAge(365, TimeUnit.DAYS)
                .cachePublic()
                .immutable();
        
        // CSS files
        registry.addResourceHandler("/css/**")
                .addResourceLocations("classpath:/static/css/")
                .setCacheControl(cacheControl)
                .resourceChain(true); // Enable resource chain for better caching
        
        // JavaScript files
        registry.addResourceHandler("/js/**")
                .addResourceLocations("classpath:/static/js/")
                .setCacheControl(cacheControl)
                .resourceChain(true);
        
        // Root-level static files (logo.png, favicon.ico)
        registry.addResourceHandler("/logo.png", "/favicon.ico", "/*.png", "/*.ico")
                .addResourceLocations("classpath:/static/")
                .setCacheControl(cacheControl)
                .resourceChain(true);
        
        // robots.txt and sitemap.xml - serve with text/plain content type
        registry.addResourceHandler("/robots.txt", "/sitemap.xml")
                .addResourceLocations("classpath:/static/")
                .setCacheControl(CacheControl.maxAge(1, TimeUnit.DAYS).cachePublic());
    }
}

