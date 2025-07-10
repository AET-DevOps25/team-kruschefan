package com.devops.kruschefan.form;

import com.devops.kruschefan.form.entity.FormEntity;
import com.devops.kruschefan.form.entity.FormResponseEntity;
import com.devops.kruschefan.form.repository.FormRepository;
import com.devops.kruschefan.form.repository.FormResponseRepository;
import com.devops.kruschefan.form.service.FormService;
import com.devops.kruschefan.openapi.model.*;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FormServiceTest {

        @Mock
        private FormRepository formRepository;

        @Mock
        private FormResponseRepository formResponseRepository;

        @Mock
        private ModelMapper modelMapper;

        @Mock
        private MeterRegistry meterRegistry;

        @Mock
        private Counter counter;

        @InjectMocks
        private FormService formService;

        private UUID testFormId;
        private UUID testResponseId;
        private UUID testQuestionId;

        @BeforeEach
        void setUp() {
                testFormId = UUID.randomUUID();
                testResponseId = UUID.randomUUID();
                testQuestionId = UUID.randomUUID();

                when(meterRegistry.counter(anyString())).thenReturn(counter);
        }

        @Test
        void createForm_ShouldReturnCreatedResponse() {
                FormCreateRequest request = new FormCreateRequest()
                                .formName("Test Form")
                                .questions(List.of(new QuestionResponse()
                                                .id(testQuestionId)
                                                .label("Test question")
                                                .required(true)
                                                .type(QuestionType.TEXT)));

                FormEntity savedEntity = FormEntity.builder()
                                .id(testFormId)
                                .formName(request.getFormName())
                                .creatorId(UUID.randomUUID())
                                .createdOn(new Date())
                                .questions(request.getQuestions())
                                .build();

                FormCreateResponse responseDto = new FormCreateResponse()
                                .id(testFormId)
                                .formName(request.getFormName());

                when(modelMapper.map(request, FormEntity.class)).thenReturn(savedEntity);
                when(formRepository.save(any(FormEntity.class))).thenReturn(savedEntity);
                when(modelMapper.map(savedEntity, FormCreateResponse.class)).thenReturn(responseDto);

                ResponseEntity<FormCreateResponse> response = formService.createForm(request);

                assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
                assertThat(response.getBody()).isNotNull();
                assertThat(response.getBody().getId()).isEqualTo(testFormId);
                verify(formRepository).save(any(FormEntity.class));
                verify(counter, times(2)).increment();
        }

        @Test
        void getAllForms_ShouldReturnListOfForms() {
                FormEntity form1 = FormEntity.builder()
                                .id(testFormId)
                                .formName("Form 1")
                                .build();

                FormEntity form2 = FormEntity.builder()
                                .id(UUID.randomUUID())
                                .formName("Form 2")
                                .build();

                List<FormEntity> forms = List.of(form1, form2);

                when(formRepository.findAll()).thenReturn(forms);
                when(modelMapper.map(any(FormEntity.class), eq(FormCreateResponse.class)))
                                .thenAnswer(invocation -> {
                                        FormEntity entity = invocation.getArgument(0);
                                        return new FormCreateResponse()
                                                        .id(entity.getId())
                                                        .formName(entity.getFormName());
                                });

                ResponseEntity<List<FormCreateResponse>> response = formService.getAllForms();

                assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
                assertThat(response.getBody()).hasSize(2);
                verify(counter, times(2)).increment();
        }

        @Test
        void getFormById_WhenFormExists_ShouldReturnForm() {
                FormEntity form = FormEntity.builder()
                                .id(testFormId)
                                .formName("Test Form")
                                .build();

                FormCreateResponse responseDto = new FormCreateResponse()
                                .id(testFormId)
                                .formName("Test Form");

                when(formRepository.findById(testFormId)).thenReturn(Optional.of(form));
                when(modelMapper.map(form, FormCreateResponse.class)).thenReturn(responseDto);

                ResponseEntity<FormCreateResponse> response = formService.getFormById(testFormId);

                assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
                assertThat(response.getBody()).isNotNull();
                assertThat(response.getBody().getId()).isEqualTo(testFormId);
                verify(counter, times(2)).increment();
        }

        @Test
        void getFormById_WhenFormNotExists_ShouldReturnNotFound() {
                when(formRepository.findById(testFormId)).thenReturn(Optional.empty());

                ResponseEntity<FormCreateResponse> response = formService.getFormById(testFormId);

                assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
                verify(counter, times(2)).increment();
        }

        @Test
        void submitForm_WhenFormExists_ShouldCreateResponse() {
                FormSubmitRequest request = new FormSubmitRequest()
                                .formId(testFormId)
                                .answers(List.of(new FormSubmitRequestAnswersInner()
                                                .questionId(testQuestionId)
                                                .answer("Test answer")));

                FormEntity form = FormEntity.builder()
                                .id(testFormId)
                                .questions(List.of(new QuestionResponse()
                                                .id(testQuestionId)
                                                .label("Test question")))
                                .build();

                FormResponseEntity savedResponse = FormResponseEntity.builder()
                                .id(testResponseId)
                                .formId(testFormId)
                                .submittedAt(new Date())
                                .questions(form.getQuestions())
                                .answers(List.of(new FormResponseEntity.FormAnswer(testQuestionId, "Test answer")))
                                .build();

                FormSubmitResponse responseDto = new FormSubmitResponse()
                                .id(testResponseId)
                                .formId(testFormId);

                when(formRepository.existsById(testFormId)).thenReturn(true);
                when(formRepository.findById(testFormId)).thenReturn(Optional.of(form));
                when(formResponseRepository.save(any(FormResponseEntity.class))).thenReturn(savedResponse);
                when(modelMapper.map(savedResponse, FormSubmitResponse.class)).thenReturn(responseDto);

                ResponseEntity<FormSubmitResponse> response = formService.submitForm(request);

                assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
                assertThat(response.getBody()).isNotNull();
                assertThat(response.getBody().getId()).isEqualTo(testResponseId);
                verify(formResponseRepository).save(any(FormResponseEntity.class));
                verify(counter, times(2)).increment();
        }

        @Test
        void submitForm_WhenFormNotExists_ShouldReturnNotFound() {
                FormSubmitRequest request = new FormSubmitRequest()
                                .formId(testFormId)
                                .answers(List.of());

                when(formRepository.existsById(testFormId)).thenReturn(false);

                ResponseEntity<FormSubmitResponse> response = formService.submitForm(request);

                assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
                verify(counter, times(2)).increment();
        }

        @Test
        void getAllFormsResponses_ShouldReturnAllResponses() {
                FormResponseEntity response1 = FormResponseEntity.builder()
                                .id(testResponseId)
                                .formId(testFormId)
                                .build();

                FormResponseEntity response2 = FormResponseEntity.builder()
                                .id(UUID.randomUUID())
                                .formId(UUID.randomUUID())
                                .build();

                List<FormResponseEntity> responses = List.of(response1, response2);

                when(formResponseRepository.findAll()).thenReturn(responses);
                when(modelMapper.map(any(FormResponseEntity.class), eq(FormSubmitResponse.class)))
                                .thenAnswer(invocation -> {
                                        FormResponseEntity entity = invocation.getArgument(0);
                                        return new FormSubmitResponse()
                                                        .id(entity.getId())
                                                        .formId(entity.getFormId());
                                });

                ResponseEntity<List<FormSubmitResponse>> response = formService.getAllFormsResponses();

                assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
                assertThat(response.getBody()).hasSize(2);
                verify(counter, times(2)).increment();
        }

        @Test
        void getFormResponseById_WhenExists_ShouldReturnResponse() {
                FormResponseEntity responseEntity = FormResponseEntity.builder()
                                .id(testResponseId)
                                .formId(testFormId)
                                .build();

                FormSubmitResponse responseDto = new FormSubmitResponse()
                                .id(testResponseId)
                                .formId(testFormId);

                when(formResponseRepository.findById(testResponseId)).thenReturn(Optional.of(responseEntity));
                when(modelMapper.map(responseEntity, FormSubmitResponse.class)).thenReturn(responseDto);

                ResponseEntity<FormSubmitResponse> response = formService.getFormResponseById(testResponseId);

                assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
                assertThat(response.getBody()).isNotNull();
                assertThat(response.getBody().getId()).isEqualTo(testResponseId);
                verify(counter, times(2)).increment();
        }

        @Test
        void getFormResponseById_WhenNotExists_ShouldReturnNotFound() {
                when(formResponseRepository.findById(testResponseId)).thenReturn(Optional.empty());

                ResponseEntity<FormSubmitResponse> response = formService.getFormResponseById(testResponseId);

                assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        }

        @Test
        void submitForm_ShouldHandleDifferentAnswerTypes() {
                FormSubmitRequest request = new FormSubmitRequest()
                                .formId(testFormId)
                                .answers(List.of(
                                                new FormSubmitRequestAnswersInner()
                                                                .questionId(testQuestionId)
                                                                .answer("text answer"),
                                                new FormSubmitRequestAnswersInner()
                                                                .questionId(testQuestionId)
                                                                .answer(42),
                                                new FormSubmitRequestAnswersInner()
                                                                .questionId(testQuestionId)
                                                                .answer(List.of("option1", "option2"))));

                FormEntity form = FormEntity.builder()
                                .id(testFormId)
                                .questions(List.of(new QuestionResponse().id(testQuestionId)))
                                .build();

                when(formRepository.existsById(testFormId)).thenReturn(true);
                when(formRepository.findById(testFormId)).thenReturn(Optional.of(form));
                when(formResponseRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

                ResponseEntity<FormSubmitResponse> response = formService.submitForm(request);

                assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
                verify(formResponseRepository).save(any(FormResponseEntity.class));
        }
}