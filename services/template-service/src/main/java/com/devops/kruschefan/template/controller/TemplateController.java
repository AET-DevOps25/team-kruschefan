package com.devops.kruschefan.template.controller;

import com.devops.kruschefan.openapi.api.TemplateApi;
import com.devops.kruschefan.openapi.model.TemplateCreateRequest;
import com.devops.kruschefan.openapi.model.TemplateResponse;
import com.devops.kruschefan.openapi.model.TemplateUpdateRequest;
import com.devops.kruschefan.template.service.TemplateService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@PreAuthorize("hasRole('client_user')")
public class TemplateController implements TemplateApi {

    private final TemplateService templateService;

    @Override
    public ResponseEntity<TemplateResponse> createTemplate(TemplateCreateRequest templateCreateRequest) {
        return templateService.createTemplate(templateCreateRequest);
    }

    @Override
    public ResponseEntity<Void> deleteTemplate(UUID templateId) {
        return templateService.deleteTemplate(templateId);
    }

    @Override
    public ResponseEntity<List<TemplateResponse>> getAllTemplates() {
        return templateService.getAllTemplates();
    }

    @Override
    public ResponseEntity<TemplateResponse> getTemplateById(UUID templateId) {
        return templateService.getTemplateById(templateId);
    }

    @Override
    public ResponseEntity<TemplateResponse> updateTemplate(UUID templateId, TemplateUpdateRequest templateUpdateRequest) {
        return templateService.updateTemplate(templateId, templateUpdateRequest);
    }
}
