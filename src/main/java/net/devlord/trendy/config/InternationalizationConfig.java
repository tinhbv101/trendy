package net.devlord.trendy.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

@Configuration
public class InternationalizationConfig implements WebMvcConfigurer {

    private static final List<Locale> SUPPORTED_LOCALES = Arrays.asList(
        Locale.ENGLISH,                    // en - English
        Locale.forLanguageTag("es"),       // es - Spanish (Español)
        Locale.CHINESE,                    // zh - Chinese (中文)
        Locale.forLanguageTag("ar"),       // ar - Arabic (العربية)
        Locale.forLanguageTag("hi"),       // hi - Hindi (हिन्दी)
        Locale.GERMAN,                     // de - German (Deutsch)
        Locale.FRENCH,                     // fr - French (Français)
        Locale.ITALIAN,                    // it - Italian (Italiano)
        Locale.forLanguageTag("pt"),       // pt - Portuguese (Português)
        Locale.forLanguageTag("ru"),       // ru - Russian (Русский)
        Locale.JAPANESE,                   // ja - Japanese (日本語)
        Locale.KOREAN,                     // ko - Korean (한국어)
        Locale.forLanguageTag("id"),       // id - Indonesian (Bahasa Indonesia)
        Locale.forLanguageTag("th"),       // th - Thai (ไทย)
        Locale.forLanguageTag("vi")        // vi - Vietnamese (Tiếng Việt)
    );

    @Bean
    public ReloadableResourceBundleMessageSource messageSource() {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasename("classpath:i18n/messages");
        messageSource.setDefaultEncoding("UTF-8");
        messageSource.setCacheSeconds(0); // Disable cache in development to see changes immediately
        messageSource.setFallbackToSystemLocale(false);
        messageSource.setUseCodeAsDefaultMessage(false);
        // Ensure all supported locales are available
        messageSource.setDefaultLocale(Locale.ENGLISH);
        return messageSource;
    }

    @Bean
    public LocaleResolver localeResolver() {
        CookieLocaleResolver resolver = new CookieLocaleResolver();
        resolver.setDefaultLocale(Locale.ENGLISH); // Default to English
        resolver.setCookieName("lang");
        resolver.setCookieMaxAge(86400 * 365); // 1 year
        resolver.setCookiePath("/"); // Available for entire application
        // Accept all supported locales
        return resolver;
    }

    @Bean
    public LocaleChangeInterceptor localeChangeInterceptor() {
        LocaleChangeInterceptor interceptor = new LocaleChangeInterceptor();
        interceptor.setParamName("lang");
        // Don't throw exception if locale is invalid, just use default
        interceptor.setIgnoreInvalidLocale(true);
        return interceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // LocaleChangeInterceptor must run first to handle lang parameter
        registry.addInterceptor(localeChangeInterceptor()).order(0);
        // LocaleDetectionInterceptor runs after to detect browser locale only if no cookie/param exists
        registry.addInterceptor(localeDetectionInterceptor()).order(1);
    }

    @Bean
    public LocaleDetectionInterceptor localeDetectionInterceptor() {
        return new LocaleDetectionInterceptor(localeResolver());
    }

    /**
     * Utility method to get the best matching locale from browser preferences
     */
    public static Locale getBestMatchingLocale(String acceptLanguageHeader) {
        if (acceptLanguageHeader == null || acceptLanguageHeader.trim().isEmpty()) {
            return Locale.ENGLISH;
        }

        // Parse Accept-Language header
        String[] languages = acceptLanguageHeader.split(",");

        for (String language : languages) {
            String lang = language.split(";")[0].trim().toLowerCase();

            // Check for exact matches first
            for (Locale supported : SUPPORTED_LOCALES) {
                String supportedLang = supported.getLanguage().toLowerCase();
                if (supportedLang.equals(lang) || 
                    lang.equals(supported.toString().toLowerCase())) {
                    return supported;
                }
            }

            // Check for language prefix (e.g., "zh-CN" -> "zh")
            for (Locale supported : SUPPORTED_LOCALES) {
                String supportedLang = supported.getLanguage().toLowerCase();
                if (lang.startsWith(supportedLang + "-")) {
                    return supported;
                }
            }
        }

        // Default to English if no match found
        return Locale.ENGLISH;
    }
    
    /**
     * Get locale from language code string (used by LocaleChangeInterceptor)
     */
    public static Locale getLocaleFromCode(String langCode) {
        if (langCode == null || langCode.trim().isEmpty()) {
            return Locale.ENGLISH;
        }
        
        String code = langCode.trim().toLowerCase();
        
        // Check against supported locales
        for (Locale supported : SUPPORTED_LOCALES) {
            if (supported.getLanguage().toLowerCase().equals(code)) {
                return supported;
            }
        }
        
        // Try to create locale from code
        try {
            Locale locale = Locale.forLanguageTag(code);
            if (locale != null && !locale.getLanguage().isEmpty()) {
                return locale;
            }
        } catch (Exception e) {
            // Ignore
        }
        
        // Default to English
        return Locale.ENGLISH;
    }
}
