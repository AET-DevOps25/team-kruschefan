package com.devops.kruschefan.template;

import com.devops.kruschefan.openapi.model.*;
import com.devops.kruschefan.template.entity.TemplateEntity;
import com.devops.kruschefan.template.repository.TemplateRepository;
import com.devops.kruschefan.template.service.TemplateService;

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

import java.time.OffsetDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TemplateServiceTest {

    @Mock
    private TemplateRepository templateRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private MeterRegistry meterRegistry;

    @Mock
    private Counter counter;

    @InjectMocks
    private TemplateService templateService;

    private UUID testTemplateId;
    private UUID testCreatorId;
    private UUID testQuestionId;

    @BeforeEach
    void setUp() {
        testTemplateId = UUID.randomUUID();
        testCreatorId = UUID.randomUUID();
        testQuestionId = UUID.randomUUID();

        when(meterRegistry.counter(anyString())).thenReturn(counter);
    }

    @Test
    void createTemplate_ShouldReturnCreatedResponse() {
        QuestionResponse question = new QuestionResponse()
                .id(testQuestionId)
                .question("Test question")
                .type(QuestionType.TEXT);

        TemplateCreateRequest request = new TemplateCreateRequest()
                .templateName("Test Template")
                .questions(List.of(question));

        TemplateEntity savedEntity = TemplateEntity.builder()
                .id(testTemplateId)
                .templateName(request.getTemplateName())
                .creatorId(testCreatorId)
                .createdOn(Date.from(OffsetDateTime.now().toInstant()))
                .questions(request.getQuestions())
                .build();

        TemplateResponse responseDto = new TemplateResponse()
                .id(testTemplateId)
                .templateName(request.getTemplateName());

        when(modelMapper.map(request, TemplateEntity.class)).thenReturn(savedEntity);
        when(templateRepository.save(any(TemplateEntity.class))).thenReturn(savedEntity);
        when(modelMapper.map(savedEntity, TemplateResponse.class)).thenReturn(responseDto);

        ResponseEntity<TemplateResponse> response = templateService.createTemplate(request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isEqualTo(testTemplateId);
        verify(templateRepository).save(any(TemplateEntity.class));
        verify(counter, times(2)).increment();
    }

    @Test
    void getAllTemplates_ShouldReturnListOfTemplates() {
        TemplateEntity template1 = TemplateEntity.builder()
                .id(testTemplateId)
                .templateName("Template 1")
                .build();

        TemplateEntity template2 = TemplateEntity.builder()
                .id(UUID.randomUUID())
                .templateName("Template 2")
                .build();

        List<TemplateEntity> templates = List.of(template1, template2);

        when(templateRepository.findAll()).thenReturn(templates);
        when(modelMapper.map(any(TemplateEntity.class), eq(TemplateResponse.class)))
                .thenAnswer(invocation -> {
                    TemplateEntity entity = invocation.getArgument(0);
                    return new TemplateResponse()
                            .id(entity.getId())
                            .templateName(entity.getTemplateName());
                });

        ResponseEntity<List<TemplateResponse>> response = templateService.getAllTemplates();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(2);
        verify(counter, times(2)).increment();
    }

    @Test
    void getTemplateById_WhenTemplateExists_ShouldReturnTemplate() {
        TemplateEntity template = TemplateEntity.builder()
                .id(testTemplateId)
                .templateName("Test Template")
                .build();

        TemplateResponse responseDto = new TemplateResponse()
                .id(testTemplateId)
                .templateName("Test Template");

        when(templateRepository.findById(testTemplateId)).thenReturn(Optional.of(template));
        when(modelMapper.map(template, TemplateResponse.class)).thenReturn(responseDto);

        ResponseEntity<TemplateResponse> response = templateService.getTemplateById(testTemplateId);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isEqualTo(testTemplateId);
        verify(counter, times(2)).increment();
    }

    @Test
    void getTemplateById_WhenTemplateNotExists_ShouldReturnNotFound() {
        when(templateRepository.findById(testTemplateId)).thenReturn(Optional.empty());

        ResponseEntity<TemplateResponse> response = templateService.getTemplateById(testTemplateId);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        verify(counter, times(2)).increment();
    }

    @Test
    void updateTemplate_WhenTemplateExists_ShouldUpdateAndReturnTemplate() {
        TemplateUpdateRequest updateRequest = new TemplateUpdateRequest()
                .templateName("Updated Name")
                .questions(List.of(new QuestionResponse()
                        .id(testQuestionId)
                        .question("Updated question")));

        TemplateEntity existingTemplate = TemplateEntity.builder()
                .id(testTemplateId)
                .templateName("Original Name")
                .questions(List.of(new QuestionResponse()
                        .id(testQuestionId)
                        .question("Original question")))
                .build();

        TemplateEntity updatedTemplate = TemplateEntity.builder()
                .id(testTemplateId)
                .templateName(updateRequest.getTemplateName())
                .questions(updateRequest.getQuestions())
                .build();

        TemplateResponse responseDto = new TemplateResponse()
                .id(testTemplateId)
                .templateName(updateRequest.getTemplateName());

        when(templateRepository.findById(testTemplateId)).thenReturn(Optional.of(existingTemplate));
        when(templateRepository.save(any(TemplateEntity.class))).thenReturn(updatedTemplate);

        doAnswer(invocation -> {
            TemplateUpdateRequest source = invocation.getArgument(0);
            TemplateEntity destination = invocation.getArgument(1);
            destination.setTemplateName(source.getTemplateName());
            destination.setQuestions(source.getQuestions());
            return null;
        }).when(modelMapper).map(any(TemplateUpdateRequest.class), any(TemplateEntity.class));

        when(modelMapper.map(updatedTemplate, TemplateResponse.class)).thenReturn(responseDto);

        ResponseEntity<TemplateResponse> response = templateService.updateTemplate(testTemplateId, updateRequest);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getTemplateName()).isEqualTo("Updated Name");
        verify(templateRepository).save(any(TemplateEntity.class));
        verify(counter, times(2)).increment();
    }

    @Test
    void updateTemplate_WhenTemplateNotExists_ShouldReturnNotFound() {
        TemplateUpdateRequest updateRequest = new TemplateUpdateRequest()
                .templateName("Updated Name");

        when(templateRepository.findById(testTemplateId)).thenReturn(Optional.empty());

        ResponseEntity<TemplateResponse> response = templateService.updateTemplate(testTemplateId, updateRequest);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        verify(counter, times(2)).increment();
    }

    @Test
    void deleteTemplate_WhenTemplateExists_ShouldDeleteAndReturnNoContent() {
        when(templateRepository.existsById(testTemplateId)).thenReturn(true);

        ResponseEntity<Void> response = templateService.deleteTemplate(testTemplateId);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(templateRepository).deleteById(testTemplateId);
        verify(counter, times(2)).increment();
    }

    @Test
    void deleteTemplate_WhenTemplateNotExists_ShouldReturnNotFound() {
        when(templateRepository.existsById(testTemplateId)).thenReturn(false);

        ResponseEntity<Void> response = templateService.deleteTemplate(testTemplateId);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        verify(templateRepository, never()).deleteById(any());
        verify(counter, times(2)).increment();
    }

    @Test
    void createTemplate_WithQuestions_ShouldSaveQuestions() {
        QuestionResponse question = new QuestionResponse()
                .id(testQuestionId)
                .question("Test question")
                .type(QuestionType.TEXT);

        TemplateCreateRequest request = new TemplateCreateRequest()
                .templateName("Test Template")
                .questions(List.of(question));

        TemplateEntity savedEntity = TemplateEntity.builder()
                .id(testTemplateId)
                .templateName(request.getTemplateName())
                .questions(request.getQuestions())
                .build();

        when(modelMapper.map(request, TemplateEntity.class)).thenReturn(savedEntity);
        when(templateRepository.save(any(TemplateEntity.class))).thenReturn(savedEntity);
        when(modelMapper.map(any(TemplateEntity.class), eq(TemplateResponse.class)))
                .thenReturn(new TemplateResponse().id(testTemplateId));

        ResponseEntity<TemplateResponse> response = templateService.createTemplate(request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        verify(templateRepository).save(argThat(template -> template.getQuestions() != null &&
                template.getQuestions().size() == 1 &&
                template.getQuestions().getFirst().getId().equals(testQuestionId)));
    }
}