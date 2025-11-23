package net.devlord.trendy.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.LocaleResolver;

import java.util.Locale;

public class LocaleDetectionInterceptor implements HandlerInterceptor {

    private final LocaleResolver localeResolver;

    public LocaleDetectionInterceptor(LocaleResolver localeResolver) {
        this.localeResolver = localeResolver;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // Check if lang parameter is present (set by LocaleChangeInterceptor)
        String langParam = request.getParameter("lang");
        
        // If lang parameter is present, let LocaleChangeInterceptor handle it
        if (langParam != null && !langParam.isEmpty()) {
            return true;
        }
        
        // Check if locale cookie exists
        jakarta.servlet.http.Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (jakarta.servlet.http.Cookie cookie : cookies) {
                if ("lang".equals(cookie.getName()) && cookie.getValue() != null && !cookie.getValue().isEmpty()) {
                    // Locale already set via cookie, don't override
                    return true;
                }
            }
        }
        
        // Only set locale if not already set by cookie or parameter (first visit)
        Locale currentLocale = localeResolver.resolveLocale(request);
        
        // If no locale is set (first visit), detect from browser
        if (currentLocale == null || currentLocale.equals(Locale.getDefault()) || currentLocale.equals(Locale.ENGLISH)) {
            String acceptLanguage = request.getHeader("Accept-Language");
            Locale detectedLocale = InternationalizationConfig.getBestMatchingLocale(acceptLanguage);
            
            // Only set if different from default
            if (!detectedLocale.equals(Locale.ENGLISH)) {
                localeResolver.setLocale(request, response, detectedLocale);
            }
        }

        return true;
    }
}
