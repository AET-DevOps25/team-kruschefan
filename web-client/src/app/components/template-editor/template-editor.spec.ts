import { ComponentFixture, TestBed } from '@angular/core/testing';
import { TemplateEditorComponent } from './template-editor.component';
import { of, throwError } from 'rxjs';
import { GenAiService } from '../../services/gen-ai.service';
import { FormService } from '../../services/form.service';
import { TemplateService } from '../../services/template.service';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MatDialog } from '@angular/material/dialog';
import { ActivatedRoute } from '@angular/router';
import { NO_ERRORS_SCHEMA } from '@angular/core';

import { TemplateResponse } from '../../interfaces/Template';
import { FormResponse } from '../../interfaces/Form';

describe('TemplateEditorComponent', () => {
  let component: TemplateEditorComponent;
  let fixture: ComponentFixture<TemplateEditorComponent>;

  let mockGenAiService: jasmine.SpyObj<GenAiService>;
  let mockFormService: jasmine.SpyObj<FormService>;
  let mockTemplateService: jasmine.SpyObj<TemplateService>;
  let mockSnackBar: jasmine.SpyObj<MatSnackBar>;
  let mockDialog: jasmine.SpyObj<MatDialog>;
  let mockActivatedRoute;

  beforeEach(async () => {
    mockGenAiService = jasmine.createSpyObj('GenAiService', ['generateForm']);
    mockFormService = jasmine.createSpyObj('FormService', ['createForm']);
    mockTemplateService = jasmine.createSpyObj('TemplateService', [
      'getTemplateById',
      'createTemplate',
      'updateTemplate',
    ]);
    mockSnackBar = jasmine.createSpyObj('MatSnackBar', ['open']);
    mockDialog = jasmine.createSpyObj('MatDialog', ['open']);
    mockActivatedRoute = {
      params: of({ templateId: '123' }),
    };

    await TestBed.configureTestingModule({
      imports: [TemplateEditorComponent],
      providers: [
        { provide: GenAiService, useValue: mockGenAiService },
        { provide: FormService, useValue: mockFormService },
        { provide: TemplateService, useValue: mockTemplateService },
        { provide: MatSnackBar, useValue: mockSnackBar },
        { provide: MatDialog, useValue: mockDialog },
        { provide: ActivatedRoute, useValue: mockActivatedRoute },
      ],
      schemas: [NO_ERRORS_SCHEMA],
    }).compileComponents();

    fixture = TestBed.createComponent(TemplateEditorComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should fetch and set template on init', () => {
    const template: TemplateResponse = {
      id: '123',
      templateName: 'Test Template',
      questions: [],
      createdAt: '2024-01-01T00:00:00Z',
    };
    mockTemplateService.getTemplateById.and.returnValue(of(template));

    component.ngOnInit();

    expect(mockTemplateService.getTemplateById).toHaveBeenCalledWith('123');
  });

  it('should handle error if getTemplateById fails', () => {
    spyOn(console, 'error');
    mockTemplateService.getTemplateById.and.returnValue(
      throwError(() => new Error('404')),
    );
    component.ngOnInit();

    expect(console.error).toHaveBeenCalledWith(
      'Error fetching template:',
      jasmine.any(Error),
    );
  });

  it('should open dialog on form creation success', () => {
    const mockForm: FormResponse = {
      id: 'resp-1',
      formId: 'form-123',
      formName: 'Generated Form',
      questions: [],
      answers: [],
      submittedOn: new Date().toISOString(),
    };

    mockFormService.createForm.and.returnValue(of(mockForm));

    component['templateTitle'].set('My Form');
    component['questions'].set([]);

    component['openFormExportDialog']();

    expect(mockSnackBar.open).toHaveBeenCalledWith(
      'Form created successfully!',
      'Close',
      { duration: 3000 },
    );
    expect(mockDialog.open).toHaveBeenCalled();
  });

  it('should show error if form creation fails', () => {
    mockFormService.createForm.and.returnValue(
      throwError(() => new Error('error')),
    );
    spyOn(console, 'error');

    component['openFormExportDialog']();

    expect(console.error).toHaveBeenCalled();
    expect(mockSnackBar.open).toHaveBeenCalledWith(
      'Error creating form',
      'Close',
      { duration: 3000 },
    );
  });

  it('should create a new template if templateId is empty', () => {
    const newTemplate: TemplateResponse = {
      id: 'new-id',
      templateName: 'New Template',
      questions: [],
      createdAt: '2024-01-01T00:00:00Z',
    };

    mockTemplateService.createTemplate.and.returnValue(of(newTemplate));

    component['templateTitle'].set('New Template');
    component['questions'].set([]);
    component['saveTemplate']();

    expect(mockTemplateService.createTemplate).toHaveBeenCalledWith({
      templateName: 'New Template',
      questions: [],
    });
  });

  it('should update template if templateId is not empty', () => {
    component['templateId'] = 'existing-id';

    mockTemplateService.updateTemplate.and.returnValue(
      of({
        id: 'existing-id',
        templateName: 'Updated Template',
        questions: [],
        createdAt: '2024-01-01T00:00:00Z',
      }),
    );

    component['templateTitle'].set('Updated Template');
    component['questions'].set([]);
    component['saveTemplate']();

    expect(mockTemplateService.updateTemplate).toHaveBeenCalledWith(
      'existing-id',
      {
        templateName: 'Updated Template',
        questions: [],
      },
    );
  });

  it('should generate a form with fallback on error', () => {
    mockGenAiService.generateForm.and.returnValue(
      throwError(() => new Error('GenAI error')),
    );
    spyOn(console, 'error');

    component['prompt'].set('Create a form');
    component['generateForm']();

    expect(mockGenAiService.generateForm).toHaveBeenCalled();
    expect(console.error).toHaveBeenCalledWith(
      'Error generating form:',
      jasmine.any(Error),
    );
  });

  it('should not call generateForm if prompt is empty', () => {
    component['prompt'].set('');
    component['generateForm']();

    expect(mockGenAiService.generateForm).not.toHaveBeenCalled();
  });
});
