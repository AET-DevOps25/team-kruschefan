package com.devops.kruschefan.template.service;

import com.devops.kruschefan.openapi.api.TemplateApiDelegate;
import com.devops.kruschefan.openapi.model.TemplateCreateRequest;
import com.devops.kruschefan.openapi.model.TemplateResponse;
import com.devops.kruschefan.openapi.model.TemplateUpdateRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TemplateService implements TemplateApiDelegate {

    @Override
    public ResponseEntity<List<TemplateResponse>> createTemplate(TemplateCreateRequest templateCreateRequest) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

    @Override
    public ResponseEntity<Void> deleteTemplate(Integer templateId) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

    @Override
    public ResponseEntity<List<TemplateResponse>> getAllTemplates() {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

    @Override
    public ResponseEntity<TemplateResponse> getTemplateById(Integer templateId) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

    @Override
    public ResponseEntity<TemplateResponse> updateTemplate(Integer templateId, TemplateUpdateRequest templateUpdateRequest) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }
}
