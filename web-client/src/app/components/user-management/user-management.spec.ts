import { ComponentFixture, TestBed } from '@angular/core/testing';
import { UserManagementComponent } from './user-management.component';
import { TemplateService } from '../../services/template.service';
import { FormService } from '../../services/form.service';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MatDialog } from '@angular/material/dialog';
import { Router } from '@angular/router';
import { of } from 'rxjs';
import { NO_ERRORS_SCHEMA } from '@angular/core';

describe('UserManagementComponent', () => {
  let component: UserManagementComponent;
  let fixture: ComponentFixture<UserManagementComponent>;
  let mockTemplateService: jasmine.SpyObj<TemplateService>;
  let mockFormService: jasmine.SpyObj<FormService>;
  let mockSnackBar: jasmine.SpyObj<MatSnackBar>;
  let mockDialog: jasmine.SpyObj<MatDialog>;
  let mockRouter: jasmine.SpyObj<Router>;

  beforeEach(async () => {
    mockTemplateService = jasmine.createSpyObj('TemplateService', [
      'getTemplates',
    ]);
    mockFormService = jasmine.createSpyObj('FormService', [
      'getForms',
      'getFormsResponses',
    ]);
    mockSnackBar = jasmine.createSpyObj('MatSnackBar', ['open']);
    mockDialog = jasmine.createSpyObj('MatDialog', ['open']);
    mockRouter = jasmine.createSpyObj('Router', ['navigate']);

    await TestBed.configureTestingModule({
      imports: [UserManagementComponent],
      providers: [
        { provide: TemplateService, useValue: mockTemplateService },
        { provide: FormService, useValue: mockFormService },
        { provide: MatSnackBar, useValue: mockSnackBar },
        { provide: MatDialog, useValue: mockDialog },
        { provide: Router, useValue: mockRouter },
      ],
      schemas: [NO_ERRORS_SCHEMA],
    }).compileComponents();

    fixture = TestBed.createComponent(UserManagementComponent);
    component = fixture.componentInstance;
  });

  it('should initialize data on ngOnInit', () => {
    mockTemplateService.getTemplates.and.returnValue(of([]));
    mockFormService.getForms.and.returnValue(of([]));
    mockFormService.getFormsResponses.and.returnValue(of([]));

    component.ngOnInit();

    expect(mockTemplateService.getTemplates).toHaveBeenCalled();
    expect(mockFormService.getForms).toHaveBeenCalled();
    expect(mockFormService.getFormsResponses).toHaveBeenCalled();
  });

  it('should navigate to edit template', () => {
    const id = 'template-123';
    component['router'] = mockRouter;

    component['editTemplate'](id);

    expect(mockRouter.navigate).toHaveBeenCalledWith(['/editor', id]);
  });
});
