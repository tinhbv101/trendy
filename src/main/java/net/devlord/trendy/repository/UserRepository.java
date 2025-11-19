package net.devlord.trendy.repository;

import net.devlord.trendy.model.entity.User;
import net.devlord.trendy.model.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    Optional<User> findByUsername(String username);
    
    Optional<User> findByEmail(String email);
    
    Boolean existsByUsername(String username);
    
    Boolean existsByEmail(String email);
    
    List<User> findByRole(Role role);
    
    List<User> findByEnabledTrue();
}

