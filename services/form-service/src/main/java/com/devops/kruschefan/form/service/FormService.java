package com.devops.kruschefan.form.service;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import io.micrometer.core.instrument.MeterRegistry;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.devops.kruschefan.openapi.api.FormApiDelegate;
import com.devops.kruschefan.openapi.model.FormCreateRequest;
import com.devops.kruschefan.openapi.model.FormCreateResponse;
import com.devops.kruschefan.openapi.model.FormSubmitRequest;
import com.devops.kruschefan.openapi.model.FormSubmitResponse;
import com.devops.kruschefan.form.entity.FormEntity;
import com.devops.kruschefan.form.entity.FormResponseEntity;
import com.devops.kruschefan.form.repository.FormRepository;
import com.devops.kruschefan.form.repository.FormResponseRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class FormService implements FormApiDelegate {

    private final FormRepository formRepository;
    private final FormResponseRepository formResponseRepository;
    private final ModelMapper modelMapper;
    private final MeterRegistry meterRegistry;

    @Override
    public ResponseEntity<FormCreateResponse> createForm(FormCreateRequest formCreateRequest) {
        log.info("Creating new form with name: {}", formCreateRequest.getFormName());
        meterRegistry.counter("form.create.requests.total").increment();

        FormEntity formEntity = modelMapper.map(formCreateRequest, FormEntity.class);
        formEntity.setId(UUID.randomUUID());
        formEntity.setCreatedOn(new Date());
        formEntity.setCreatorId(UUID.randomUUID());

        FormEntity savedForm = formRepository.save(formEntity);
        log.info("Form created with ID: {}", savedForm.getId());
        meterRegistry.counter("form.create.requests.success").increment();

        FormCreateResponse response = modelMapper.map(savedForm, FormCreateResponse.class);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<List<FormCreateResponse>> getAllForms() {
        log.info("Fetching all forms");
        meterRegistry.counter("form.get_all.requests.total").increment();

        List<FormEntity> forms = formRepository.findAll();
        log.info("Found {} forms", forms.size());
        meterRegistry.counter("form.get_all.requests.success").increment();

        List<FormCreateResponse> response = forms.stream()
                .map(form -> modelMapper.map(form, FormCreateResponse.class))
                .toList();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<FormCreateResponse> getFormById(UUID formId) {
        log.info("Fetching form with ID: {}", formId);
        meterRegistry.counter("form.get_by_id.requests.total").increment();

        FormEntity form = formRepository.findById(formId).orElse(null);
        if (form == null) {
            log.warn("Form with ID {} not found", formId);
            meterRegistry.counter("form.get_by_id.requests.not_found").increment();
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        FormCreateResponse response = modelMapper.map(form, FormCreateResponse.class);
        meterRegistry.counter("form.get_by_id.requests.success").increment();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<List<FormSubmitResponse>> getAllFormsResponses() {
        log.info("Fetching all form responses");
        meterRegistry.counter("form.get_all_responses.requests.total").increment();

        List<FormResponseEntity> responses = formResponseRepository.findAll();
        log.info("Found {} form responses", responses.size());
        meterRegistry.counter("form.get_all_responses.requests.success").increment();

        List<FormSubmitResponse> response = responses.stream()
                .map(resp -> modelMapper.map(resp, FormSubmitResponse.class))
                .toList();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<FormSubmitResponse> submitForm(FormSubmitRequest formSubmitRequest) {
        log.info("Submitting response for form ID: {}", formSubmitRequest.getFormId());
        meterRegistry.counter("form.submit.requests.total").increment();

        if (!formRepository.existsById(formSubmitRequest.getFormId())) {
            log.warn("Form with ID {} not found", formSubmitRequest.getFormId());
            meterRegistry.counter("form.submit.requests.not_found").increment();
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        FormResponseEntity responseEntity = new FormResponseEntity();
        responseEntity.setId(UUID.randomUUID());
        responseEntity.setFormId(formSubmitRequest.getFormId());
        responseEntity.setSubmittedAt(new Date());

        // Convert answers to our internal format
        List<FormResponseEntity.FormAnswer> answers = formSubmitRequest.getAnswers().stream()
                .map(answer -> FormResponseEntity.FormAnswer.builder()
                        .questionId(answer.getQuestionId())
                        .answer(answer.getAnswer())
                        .build())
                .toList();

        responseEntity.setAnswers(answers);

        // Get questions from the form
        FormEntity form = formRepository.findById(formSubmitRequest.getFormId()).orElseThrow();
        responseEntity.setQuestions(form.getQuestions());

        FormResponseEntity savedResponse = formResponseRepository.save(responseEntity);
        log.info("Form response submitted with ID: {}", savedResponse.getId());
        meterRegistry.counter("form.submit.requests.success").increment();

        FormSubmitResponse response = modelMapper.map(savedResponse, FormSubmitResponse.class);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<FormSubmitResponse> getFormResponseById(UUID responseId) {
        log.info("Fetching form response with ID: {}", responseId);
        meterRegistry.counter("form.get_response_by_id.requests.total").increment();

        FormResponseEntity response = formResponseRepository.findById(responseId).orElse(null);
        if (response == null) {
            log.warn("Form response with ID {} not found", responseId);
            meterRegistry.counter("form.get_response_by_id.requests.not_found").increment();
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        log.info("Form response found with ID: {}", responseId);
        meterRegistry.counter("form.get_response_by_id.requests.success").increment();

        FormSubmitResponse apiResponse = modelMapper.map(response, FormSubmitResponse.class);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }
}