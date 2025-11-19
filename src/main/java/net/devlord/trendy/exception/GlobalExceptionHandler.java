package net.devlord.trendy.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    
    @ExceptionHandler(TrendNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleTrendNotFound(TrendNotFoundException ex, Model model) {
        log.error("Trend not found: {}", ex.getMessage());
        model.addAttribute("error", ex.getMessage());
        return "error/404";
    }
    
    @ExceptionHandler(ImageGenerationException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleImageGenerationException(ImageGenerationException ex, Model model) {
        log.error("Image generation failed: {}", ex.getMessage(), ex);
        model.addAttribute("error", "Failed to generate image: " + ex.getMessage());
        return "error/500";
    }
    
    @ExceptionHandler(FileStorageException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleFileStorageException(FileStorageException ex, Model model) {
        log.error("File storage error: {}", ex.getMessage(), ex);
        model.addAttribute("error", "File storage error: " + ex.getMessage());
        return "error/500";
    }
    
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    @ResponseStatus(HttpStatus.PAYLOAD_TOO_LARGE)
    public String handleMaxUploadSizeExceeded(MaxUploadSizeExceededException ex, Model model) {
        log.error("File size exceeded: {}", ex.getMessage());
        model.addAttribute("error", "File size too large. Maximum allowed size is 10MB.");
        return "error/413";
    }
    
    @ExceptionHandler(NoResourceFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleNoResourceFound(NoResourceFoundException ex) {
        // Silently ignore missing static resources like favicon, etc.
        log.debug("Resource not found: {}", ex.getMessage());
        return null; // Return null to prevent 500 error
    }
    
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleGeneralException(Exception ex, Model model) {
        log.error("Unexpected error: {}", ex.getMessage(), ex);
        model.addAttribute("error", "An unexpected error occurred. Please try again later.");
        return "error/500";
    }
}

