package com.devops.kruschefan.form.repository;

import com.devops.kruschefan.form.entity.FormEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.UUID;

public interface FormRepository extends MongoRepository<FormEntity, UUID> {
}