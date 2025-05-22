package com.devops.kruschefan.user.repository;

import com.devops.kruschefan.user.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<User, String> {
}
