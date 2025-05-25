package com.devops.kruschefan.user.repository;

import com.devops.kruschefan.user.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {
    
    // Custom finder methods based on fields in your User class
    Optional<User> findByUsername(String username);
    
    Optional<User> findByEmail(String email);
}
