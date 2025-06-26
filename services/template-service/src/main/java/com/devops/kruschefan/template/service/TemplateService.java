package com.devops.kruschefan.template.service;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.devops.kruschefan.openapi.api.TemplateApiDelegate;
import com.devops.kruschefan.openapi.model.TemplateCreateRequest;
import com.devops.kruschefan.openapi.model.TemplateResponse;
import com.devops.kruschefan.openapi.model.TemplateUpdateRequest;
import com.devops.kruschefan.template.entity.TemplateEntity;
import com.devops.kruschefan.template.repository.TemplateRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class TemplateService implements TemplateApiDelegate {

    private final TemplateRepository templateRepository;
    private final ModelMapper modelMapper;

    @Override
    public ResponseEntity<TemplateResponse> createTemplate(TemplateCreateRequest templateCreateRequest) {
        log.info("Creating new template with name: {}", templateCreateRequest.getTemplateName());

        TemplateEntity templateEntity = modelMapper.map(templateCreateRequest, TemplateEntity.class);
        templateEntity.setId(UUID.randomUUID());
        templateEntity.setCreatedOn(new Date());
        templateEntity.setCreatorId(UUID.randomUUID()); // TODO: Get the actual creator ID from the token

        TemplateEntity savedTemplate = templateRepository.save(templateEntity);
        log.info("Template created with ID: {}", savedTemplate.getId());

        TemplateResponse response = modelMapper.map(savedTemplate, TemplateResponse.class);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<Void> deleteTemplate(UUID templateId) {
        log.info("Deleting template with ID: {}", templateId);

        if (!templateRepository.existsById(templateId)) {
            log.warn("Template with ID {} not found for deletion", templateId);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        templateRepository.deleteById(templateId);
        log.info("Template with ID {} successfully deleted", templateId);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Override
    public ResponseEntity<List<TemplateResponse>> getAllTemplates() {
        log.info("Fetching all templates for creator ID: {}", UUID.randomUUID()); // TODO: Get the actual creator ID
                                                                                  // from the token

        List<TemplateEntity> templates = templateRepository.findAll();
        log.info("Found {} templates", templates.size());

        List<TemplateResponse> response = templates.stream()
                .map(template -> modelMapper.map(template, TemplateResponse.class))
                .toList();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<TemplateResponse> getTemplateById(UUID templateId) {
        log.info("Fetching template with ID: {}", templateId);

        TemplateEntity template = templateRepository.findById(templateId).orElse(null);
        if (template == null) {
            log.warn("Template with ID {} not found", templateId);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        log.info("Template found with ID: {}", templateId);

        TemplateResponse response = modelMapper.map(template, TemplateResponse.class);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<TemplateResponse> updateTemplate(UUID templateId,
            TemplateUpdateRequest templateUpdateRequest) {
        log.info("Updating template with ID: {}", templateId);

        TemplateEntity template = templateRepository.findById(templateId).orElse(null);
        if (template == null) {
            log.warn("Template with ID {} not found for update", templateId);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        modelMapper.map(templateUpdateRequest, template);
        template.setId(templateId); // Ensure the ID remains unchanged
        TemplateEntity updatedTemplate = templateRepository.save(template);
        log.info("Template with ID {} successfully updated", templateId);

        TemplateResponse response = modelMapper.map(updatedTemplate, TemplateResponse.class);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}