package com.devops.kruschefan.template.controller;

import com.devops.kruschefan.openapi.api.TemplateApi;
import com.devops.kruschefan.openapi.api.TemplateApiDelegate;
import com.devops.kruschefan.template.service.TemplateService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/template")
@RequiredArgsConstructor
public class TemplateController implements TemplateApi {
    private final TemplateService templateService;

    @Override
    public TemplateApiDelegate getDelegate() {
        return templateService;
    }
}
