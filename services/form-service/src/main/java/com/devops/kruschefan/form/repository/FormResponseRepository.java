package com.devops.kruschefan.form.repository;

import com.devops.kruschefan.form.entity.FormResponseEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.UUID;

public interface FormResponseRepository extends MongoRepository<FormResponseEntity, UUID> {
}