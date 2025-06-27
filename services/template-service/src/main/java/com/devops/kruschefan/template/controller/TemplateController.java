package com.devops.kruschefan.template.controller;

import org.springframework.web.bind.annotation.RestController;

import com.devops.kruschefan.openapi.api.TemplateApi;
import com.devops.kruschefan.openapi.api.TemplateApiDelegate;
import com.devops.kruschefan.template.service.TemplateService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class TemplateController implements TemplateApi {

    private final TemplateService templateService;

    @Override
    public TemplateApiDelegate getDelegate() {
        return templateService;
    }
}
