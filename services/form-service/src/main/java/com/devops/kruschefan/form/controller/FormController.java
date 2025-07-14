package com.devops.kruschefan.form.controller;

import com.devops.kruschefan.form.service.FormService;
import com.devops.kruschefan.openapi.api.FormApi;
import com.devops.kruschefan.openapi.api.FormApiDelegate;
import com.devops.kruschefan.openapi.model.FormCreateRequest;
import com.devops.kruschefan.openapi.model.FormCreateResponse;
import com.devops.kruschefan.openapi.model.FormSubmitRequest;
import com.devops.kruschefan.openapi.model.FormSubmitResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@PreAuthorize("hasRole('client_user')")
public class FormController implements FormApi {
    private final FormService formService;

    @Override
    public ResponseEntity<FormCreateResponse> createForm(FormCreateRequest formCreateRequest) {
        return formService.createForm(formCreateRequest);
    }

    @Override
    public ResponseEntity<List<FormCreateResponse>> getAllForms() {
        return formService.getAllForms();
    }

    @Override
    public ResponseEntity<List<FormSubmitResponse>> getAllFormsResponses() {
        return formService.getAllFormsResponses();
    }

    @Override
    public ResponseEntity<FormCreateResponse> getFormById(UUID formId) {
        return formService.getFormById(formId);
    }

    @Override
    public ResponseEntity<FormSubmitResponse> getFormResponseById(UUID responseId) {
        return formService.getFormResponseById(responseId);
    }

    @Override
    public ResponseEntity<FormSubmitResponse> submitForm(FormSubmitRequest formSubmitRequest) {
        return formService.submitForm(formSubmitRequest);
    }
}
