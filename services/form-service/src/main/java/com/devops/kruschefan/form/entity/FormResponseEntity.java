package com.devops.kruschefan.form.entity;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.devops.kruschefan.openapi.model.QuestionResponse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@EqualsAndHashCode
@Document(value = "form_responses")
public class FormResponseEntity {

    @Id
    private UUID id;

    @Indexed
    private UUID formId;

    @CreatedDate
    private Date submittedAt;

    private String formName;

    private List<QuestionResponse> questions;

    private List<FormAnswer> answers;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class FormAnswer {
        private UUID questionId;
        private Object answer;
    }
}