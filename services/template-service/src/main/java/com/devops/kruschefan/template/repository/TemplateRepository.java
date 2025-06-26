package com.devops.kruschefan.template.repository;

import com.devops.kruschefan.template.entity.TemplateEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.UUID;

public interface TemplateRepository extends MongoRepository<TemplateEntity, UUID> {
}
