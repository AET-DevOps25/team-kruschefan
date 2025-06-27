package com.devops.kruschefan.form.controller;

import com.devops.kruschefan.form.service.FormService;
import com.devops.kruschefan.openapi.api.FormApi;
import com.devops.kruschefan.openapi.api.FormApiDelegate;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class FormController implements FormApi {
    private final FormService formService;

    @Override
    public FormApiDelegate getDelegate() {
        return formService;
    }
}
