package com.devops.kruschefan.template.service;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import io.micrometer.core.instrument.MeterRegistry;
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
    private final MeterRegistry meterRegistry;

    @Override
    public ResponseEntity<TemplateResponse> createTemplate(TemplateCreateRequest templateCreateRequest) {
        log.info("Creating new template with name: {}", templateCreateRequest.getTemplateName());
        meterRegistry.counter("template.create.requests.total").increment();

        TemplateEntity templateEntity = modelMapper.map(templateCreateRequest, TemplateEntity.class);
        templateEntity.setId(UUID.randomUUID());
        templateEntity.setCreatedOn(new Date());
        templateEntity.setCreatorId(UUID.randomUUID()); // TODO: Get the actual creator ID from the token

        TemplateEntity savedTemplate = templateRepository.save(templateEntity);
        log.info("Template created with ID: {}", savedTemplate.getId());
        meterRegistry.counter("template.create.requests.success").increment();

        TemplateResponse response = modelMapper.map(savedTemplate, TemplateResponse.class);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<Void> deleteTemplate(UUID templateId) {
        log.info("Deleting template with ID: {}", templateId);
        meterRegistry.counter("template.delete.requests.total").increment();

        if (!templateRepository.existsById(templateId)) {
            log.warn("Template with ID {} not found for deletion", templateId);
            meterRegistry.counter("template.delete.requests.not_found").increment();
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        templateRepository.deleteById(templateId);
        log.info("Template with ID {} successfully deleted", templateId);
        meterRegistry.counter("template.delete.requests.success").increment();

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Override
    public ResponseEntity<List<TemplateResponse>> getAllTemplates() {
        log.info("Fetching all templates for creator ID: {}", UUID.randomUUID()); // TODO: Get the actual creator ID
                                                                                  // from the token
        meterRegistry.counter("template.get_all.requests.total").increment();

        List<TemplateEntity> templates = templateRepository.findAll();
        log.info("Found {} templates", templates.size());
        meterRegistry.counter("template.get_all.requests.success").increment();

        List<TemplateResponse> response = templates.stream()
                .map(template -> modelMapper.map(template, TemplateResponse.class))
                .toList();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<TemplateResponse> getTemplateById(UUID templateId) {
        log.info("Fetching template with ID: {}", templateId);
        meterRegistry.counter("template.get_by_id.requests.total").increment();

        TemplateEntity template = templateRepository.findById(templateId).orElse(null);
        if (template == null) {
            log.warn("Template with ID {} not found", templateId);
            meterRegistry.counter("template.get_by_id.requests.not_found").increment();
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        log.info("Template found with ID: {}", templateId);
        meterRegistry.counter("template.get_by_id.requests.success").increment();

        TemplateResponse response = modelMapper.map(template, TemplateResponse.class);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<TemplateResponse> updateTemplate(UUID templateId,
            TemplateUpdateRequest templateUpdateRequest) {
        log.info("Updating template with ID: {}", templateId);
        meterRegistry.counter("template.update.requests.total").increment();

        TemplateEntity template = templateRepository.findById(templateId).orElse(null);
        if (template == null) {
            log.warn("Template with ID {} not found for update", templateId);
            meterRegistry.counter("template.update.requests.not_found").increment();
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        modelMapper.map(templateUpdateRequest, template);
        template.setId(templateId); // Ensure the ID remains unchanged
        TemplateEntity updatedTemplate = templateRepository.save(template);
        log.info("Template with ID {} successfully updated", templateId);
        meterRegistry.counter("template.update.requests.success").increment();

        TemplateResponse response = modelMapper.map(updatedTemplate, TemplateResponse.class);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}