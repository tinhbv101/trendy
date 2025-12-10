package net.devlord.trendy.controller.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import net.devlord.trendy.model.dto.AchievementDTO;
import net.devlord.trendy.model.entity.User;
import net.devlord.trendy.model.entity.UserAchievement;
import net.devlord.trendy.model.enums.AchievementType;
import net.devlord.trendy.service.AchievementService;
import net.devlord.trendy.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/achievements")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Achievements", description = "Achievement and badge management APIs")
public class AchievementApiController {
    
    private final AchievementService achievementService;
    private final UserService userService;
    private final MessageSource messageSource;
    
    @Operation(summary = "Get all available achievements")
    @GetMapping("/all")
    public ResponseEntity<List<AchievementDTO>> getAllAchievements() {
        log.info("Getting all achievements (no auth required)");
        List<AchievementDTO> achievements = Arrays.stream(AchievementType.values())
            .map(this::convertTypeToDTO)
            .collect(Collectors.toList());
        log.info("Returning {} achievement types", achievements.size());
        return ResponseEntity.ok(achievements);
    }
    
    @Operation(summary = "Test endpoint")
    @GetMapping("/test")
    public ResponseEntity<Map<String, Object>> testEndpoint() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Map<String, Object> result = new java.util.HashMap<>();
        result.put("authenticated", authentication != null && authentication.isAuthenticated());
        result.put("username", authentication != null ? authentication.getName() : null);
        result.put("principal", authentication != null ? authentication.getPrincipal().getClass().getSimpleName() : null);
        result.put("isAnonymous", authentication != null && "anonymousUser".equals(authentication.getName()));
        return ResponseEntity.ok(result);
    }
    
    @Operation(summary = "Get current user achievements")
    @GetMapping
    public ResponseEntity<List<AchievementDTO>> getCurrentUserAchievements() {
        // Get authentication from SecurityContext
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        log.info("Getting achievements - Authentication: {}", authentication != null ? authentication.getName() : "null");
        log.info("Is authenticated: {}", authentication != null ? authentication.isAuthenticated() : false);
        log.info("Principal type: {}", authentication != null ? authentication.getPrincipal().getClass().getName() : "null");
        
        if (authentication == null || !authentication.isAuthenticated() || 
            "anonymousUser".equals(authentication.getName()) ||
            authentication.getPrincipal().equals("anonymousUser")) {
            log.warn("Unauthorized access attempt to achievements API");
            return ResponseEntity.status(401).build();
        }
        
        String username = authentication.getName();
        log.info("Attempting to find user: {}", username);
        
        User user = userService.findByUsername(username)
            .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));
        
        log.info("Loading achievements for user: {} (id: {})", username, user.getId());
        
        List<UserAchievement> userAchievements = achievementService.getUserAchievements(user.getId());
        log.info("Found {} user achievements in database", userAchievements.size());
        
        Map<AchievementType, UserAchievement> achievementMap = userAchievements.stream()
            .collect(Collectors.toMap(UserAchievement::getAchievementType, ua -> ua));
        
        List<AchievementDTO> achievements = Arrays.stream(AchievementType.values())
            .map(type -> {
                UserAchievement userAchievement = achievementMap.get(type);
                return convertToDTO(type, userAchievement);
            })
            .collect(Collectors.toList());
        
        long completedCount = achievements.stream().filter(AchievementDTO::getCompleted).count();
        log.info("Returning {} total achievements, {} completed", achievements.size(), completedCount);
        
        return ResponseEntity.ok(achievements);
    }
    
    @Operation(summary = "Get user achievements by ID")
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<AchievementDTO>> getUserAchievements(@PathVariable Long userId) {
        List<UserAchievement> userAchievements = achievementService.getUserAchievements(userId);
        Map<AchievementType, UserAchievement> achievementMap = userAchievements.stream()
            .collect(Collectors.toMap(UserAchievement::getAchievementType, ua -> ua));
        
        List<AchievementDTO> achievements = Arrays.stream(AchievementType.values())
            .map(type -> {
                UserAchievement userAchievement = achievementMap.get(type);
                return convertToDTO(type, userAchievement);
            })
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(achievements);
    }
    
    @Operation(summary = "Get completed achievements")
    @GetMapping("/completed")
    public ResponseEntity<List<AchievementDTO>> getCompletedAchievements() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated() || 
            "anonymousUser".equals(authentication.getName()) ||
            authentication.getPrincipal().equals("anonymousUser")) {
            return ResponseEntity.status(401).build();
        }
        
        String username = authentication.getName();
        User user = userService.findByUsername(username)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));
        
        List<UserAchievement> completed = achievementService.getCompletedAchievements(user.getId());
        List<AchievementDTO> achievements = completed.stream()
            .map(ua -> convertToDTO(ua.getAchievementType(), ua))
            .collect(Collectors.toList());
        return ResponseEntity.ok(achievements);
    }
    
    @Operation(summary = "Get achievement statistics")
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getAchievementStats() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated() || 
            "anonymousUser".equals(authentication.getName()) ||
            authentication.getPrincipal().equals("anonymousUser")) {
            return ResponseEntity.status(401).build();
        }
        
        String username = authentication.getName();
        User user = userService.findByUsername(username)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));
        
        long completed = achievementService.getCompletedAchievementCount(user.getId());
        long total = AchievementType.values().length;
        double percentage = (double) completed / total * 100;
        
        return ResponseEntity.ok(Map.of(
            "completed", completed,
            "total", total,
            "percentage", percentage
        ));
    }
    
    private AchievementDTO convertTypeToDTO(AchievementType type) {
        Locale locale = LocaleContextHolder.getLocale();
        String nameKey = "achievement." + type.name() + ".name";
        String descKey = "achievement." + type.name() + ".desc";
        
        AchievementDTO dto = new AchievementDTO();
        dto.setType(type);
        dto.setName(messageSource.getMessage(nameKey, null, type.getName(), locale));
        dto.setDescription(messageSource.getMessage(descKey, null, type.getDescription(), locale));
        dto.setIcon(type.getIcon());
        dto.setXpReward(type.getXpReward());
        dto.setProgress(0);
        dto.setCompleted(false);
        dto.setNotified(false);
        return dto;
    }
    
    private AchievementDTO convertToDTO(AchievementType type, UserAchievement userAchievement) {
        Locale locale = LocaleContextHolder.getLocale();
        String nameKey = "achievement." + type.name() + ".name";
        String descKey = "achievement." + type.name() + ".desc";
        
        AchievementDTO dto = new AchievementDTO();
        dto.setType(type);
        dto.setName(messageSource.getMessage(nameKey, null, type.getName(), locale));
        dto.setDescription(messageSource.getMessage(descKey, null, type.getDescription(), locale));
        dto.setIcon(type.getIcon());
        dto.setXpReward(type.getXpReward());
        
        if (userAchievement != null) {
            dto.setId(userAchievement.getId());
            dto.setProgress(userAchievement.getProgress());
            dto.setCompleted(userAchievement.getCompleted());
            dto.setCompletedAt(userAchievement.getCompletedAt());
            dto.setNotified(userAchievement.getNotified());
        } else {
            dto.setProgress(0);
            dto.setCompleted(false);
            dto.setNotified(false);
        }
        
        return dto;
    }
}
