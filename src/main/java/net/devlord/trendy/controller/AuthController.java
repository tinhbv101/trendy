package net.devlord.trendy.controller;

import net.devlord.trendy.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class AuthController {
    
    private final UserService userService;
    
    @GetMapping("/login")
    public String loginPage() {
        return "auth/login";
    }
    
    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("registerForm", new RegisterForm());
        return "auth/register";
    }
    
    @PostMapping("/register")
    public String register(
            @Valid @ModelAttribute RegisterForm registerForm,
            BindingResult result,
            RedirectAttributes redirectAttributes) {
        
        if (result.hasErrors()) {
            return "auth/register";
        }
        
        if (!registerForm.getPassword().equals(registerForm.getConfirmPassword())) {
            result.rejectValue("confirmPassword", "error.registerForm", "Passwords do not match");
            return "auth/register";
        }
        
        try {
            userService.registerUser(
                registerForm.getUsername(),
                registerForm.getEmail(),
                registerForm.getPassword(),
                registerForm.getFullName()
            );
            
            redirectAttributes.addFlashAttribute("success", "Registration successful! Please login.");
            return "redirect:/login";
            
        } catch (IllegalArgumentException e) {
            result.rejectValue("username", "error.registerForm", e.getMessage());
            return "auth/register";
        }
    }
    
    @Data
    public static class RegisterForm {
        @NotBlank(message = "Username is required")
        @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
        private String username;
        
        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email format")
        private String email;
        
        @NotBlank(message = "Password is required")
        @Size(min = 6, message = "Password must be at least 6 characters")
        private String password;
        
        @NotBlank(message = "Please confirm password")
        private String confirmPassword;
        
        @NotBlank(message = "Full name is required")
        private String fullName;
    }
}

