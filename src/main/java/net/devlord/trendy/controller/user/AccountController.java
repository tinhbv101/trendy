package net.devlord.trendy.controller.user;

import jakarta.validation.Valid;
import net.devlord.trendy.model.dto.ChangePasswordRequest;
import net.devlord.trendy.model.dto.UpdateProfileRequest;
import net.devlord.trendy.model.entity.User;
import net.devlord.trendy.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/account")
@RequiredArgsConstructor
@Slf4j
public class AccountController {
    
    private final UserService userService;
    
    @GetMapping
    public String accountSettings(Model model, Authentication authentication) {
        String username = authentication.getName();
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        
        // Add user info to model
        model.addAttribute("user", user);
        
        // Add statistics
        long totalImages = userService.countGeneratedImagesByUsername(username);
        model.addAttribute("totalImages", totalImages);
        
        // Add form objects if not present (for validation errors)
        if (!model.containsAttribute("updateProfileRequest")) {
            UpdateProfileRequest profileRequest = new UpdateProfileRequest();
            profileRequest.setFullName(user.getFullName());
            profileRequest.setEmail(user.getEmail());
            model.addAttribute("updateProfileRequest", profileRequest);
        }
        
        if (!model.containsAttribute("changePasswordRequest")) {
            model.addAttribute("changePasswordRequest", new ChangePasswordRequest());
        }
        
        model.addAttribute("pageTitle", "Account Settings");
        return "user/account";
    }
    
    @PostMapping("/change-password")
    public String changePassword(
            @Valid @ModelAttribute ChangePasswordRequest request,
            BindingResult bindingResult,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {
        
        // Check for validation errors
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.changePasswordRequest", bindingResult);
            redirectAttributes.addFlashAttribute("changePasswordRequest", request);
            redirectAttributes.addFlashAttribute("error", "Please fix the validation errors");
            return "redirect:/account";
        }
        
        // Check if new password and confirm password match
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            redirectAttributes.addFlashAttribute("changePasswordRequest", request);
            redirectAttributes.addFlashAttribute("error", "New password and confirmation do not match");
            return "redirect:/account";
        }
        
        try {
            String username = authentication.getName();
            userService.changePassword(username, request.getCurrentPassword(), request.getNewPassword());
            
            redirectAttributes.addFlashAttribute("success", "Password changed successfully!");
            log.info("Password changed for user: {}", username);
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("changePasswordRequest", request);
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/account";
        } catch (Exception e) {
            log.error("Error changing password", e);
            redirectAttributes.addFlashAttribute("changePasswordRequest", request);
            redirectAttributes.addFlashAttribute("error", "An error occurred while changing password");
            return "redirect:/account";
        }
        
        return "redirect:/account";
    }
    
    @PostMapping("/update-profile")
    public String updateProfile(
            @Valid @ModelAttribute UpdateProfileRequest request,
            BindingResult bindingResult,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {
        
        // Check for validation errors
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.updateProfileRequest", bindingResult);
            redirectAttributes.addFlashAttribute("updateProfileRequest", request);
            redirectAttributes.addFlashAttribute("profileError", "Please fix the validation errors");
            return "redirect:/account";
        }
        
        try {
            String username = authentication.getName();
            userService.updateProfile(username, request.getFullName(), request.getEmail());
            
            redirectAttributes.addFlashAttribute("success", "Profile updated successfully!");
            log.info("Profile updated for user: {}", username);
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("updateProfileRequest", request);
            redirectAttributes.addFlashAttribute("profileError", e.getMessage());
            return "redirect:/account";
        } catch (Exception e) {
            log.error("Error updating profile", e);
            redirectAttributes.addFlashAttribute("updateProfileRequest", request);
            redirectAttributes.addFlashAttribute("profileError", "An error occurred while updating profile");
            return "redirect:/account";
        }
        
        return "redirect:/account";
    }
}

