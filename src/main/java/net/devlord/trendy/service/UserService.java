package net.devlord.trendy.service;

import net.devlord.trendy.model.entity.User;
import net.devlord.trendy.model.enums.Role;
import net.devlord.trendy.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    @Transactional(readOnly = true)
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
    
    @Transactional(readOnly = true)
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    
    @Transactional(readOnly = true)
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }
    
    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
    
    @Transactional
    public User createUser(String username, String email, String password, String fullName, Role role) {
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setFullName(fullName);
        user.setRole(role);
        user.setEnabled(true);
        
        User savedUser = userRepository.save(user);
        log.info("Created new user: {}", username);
        return savedUser;
    }
    
    @Transactional
    public User registerUser(String username, String email, String password, String fullName) {
        if (existsByUsername(username)) {
            throw new IllegalArgumentException("Username already exists");
        }
        if (existsByEmail(email)) {
            throw new IllegalArgumentException("Email already exists");
        }
        
        return createUser(username, email, password, fullName, Role.USER);
    }
    
    @Transactional
    public void changePassword(String username, String currentPassword, String newPassword) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        
        // Verify current password
        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new IllegalArgumentException("Current password is incorrect");
        }
        
        // Update to new password
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        log.info("Password changed successfully for user: {}", username);
    }
    
    @Transactional
    public User updateProfile(String username, String fullName, String email) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        
        // Check if email is being changed and if it's already taken by another user
        if (!user.getEmail().equals(email)) {
            if (existsByEmail(email)) {
                throw new IllegalArgumentException("Email is already in use by another account");
            }
            user.setEmail(email);
            log.info("Email updated for user: {} to {}", username, email);
        }
        
        // Update full name
        if (fullName != null && !fullName.trim().isEmpty()) {
            user.setFullName(fullName.trim());
            log.info("Full name updated for user: {}", username);
        }
        
        User savedUser = userRepository.save(user);
        log.info("Profile updated successfully for user: {}", username);
        return savedUser;
    }
    
    @Transactional(readOnly = true)
    public long countGeneratedImagesByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        return user.getGeneratedImages() != null ? user.getGeneratedImages().size() : 0;
    }
}

