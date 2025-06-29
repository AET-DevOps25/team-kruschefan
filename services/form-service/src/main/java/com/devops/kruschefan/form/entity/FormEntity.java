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
@Document(value = "forms")
public class FormEntity {

    @Id
    private UUID id;

    @Indexed
    private UUID creatorId;

    @CreatedDate
    private Date createdOn;

    private String formName;

    private List<QuestionResponse> questions;
}