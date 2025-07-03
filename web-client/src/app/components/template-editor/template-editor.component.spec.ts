import { ComponentFixture, TestBed } from '@angular/core/testing';
import { TemplateEditorComponent } from './template-editor.component';
import { GenAiService } from '../../services/gen-ai.service';
import { of, throwError } from 'rxjs';
import { MatDialog } from '@angular/material/dialog';
import { CdkDragDrop } from '@angular/cdk/drag-drop';
import { QuestionType } from '../../interfaces/Question';

describe('TemplateEditorComponent', () => {
  let component: TemplateEditorComponent;
  let fixture: ComponentFixture<TemplateEditorComponent>;
  let genAiServiceSpy: jasmine.SpyObj<GenAiService>;
  let matDialogSpy: jasmine.SpyObj<MatDialog>;

  beforeEach(async () => {
    genAiServiceSpy = jasmine.createSpyObj('GenAiService', ['generateForm']);
    matDialogSpy = jasmine.createSpyObj('MatDialog', ['open']);

    await TestBed.configureTestingModule({
      imports: [TemplateEditorComponent],
      providers: [
        { provide: GenAiService, useValue: genAiServiceSpy },
        { provide: MatDialog, useValue: matDialogSpy },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(TemplateEditorComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create the component', () => {
    expect(component).toBeTruthy();
  });

  it('should not generate form if prompt is empty', () => {
    component['prompt'].set('');
    component['generateForm']();
    expect(genAiServiceSpy.generateForm).not.toHaveBeenCalled();
  });

  it('should generate form with API response', () => {
    const mockForm = {
      questions: [{ id: '1', label: 'Name?', type: QuestionType.TEXT }],
      title: 'Sample Form',
    };

    genAiServiceSpy.generateForm.and.returnValue(of(mockForm));
    component['prompt'].set('create a form');

    component['generateForm']();

    expect(genAiServiceSpy.generateForm).toHaveBeenCalledWith('create a form');
    expect(component['questions']()).toEqual(mockForm.questions);
    expect(component['templateTitle']()).toEqual(mockForm.title);
  });

  it('should fallback to random questions on error', () => {
    spyOn(console, 'error');

    genAiServiceSpy.generateForm.and.returnValue(
      throwError(() => new Error('fail')),
    );
    component['prompt'].set('something');

    component['generateForm']();

    expect(component['questions']().length).toBeGreaterThan(0);
    expect(component['templateTitle']()).toBe('Introduction Form');
  });

  it('should open export dialog', () => {
    component['openFormExportDialog']();
    expect(matDialogSpy.open).toHaveBeenCalled();
  });

  it('should delete question at given index', () => {
    const questions = [
      { id: '1', label: 'Q1', type: QuestionType.TEXT },
      { id: '2', label: 'Q2', type: QuestionType.NUMBER },
    ];
    component['questions'].set(JSON.parse(JSON.stringify(questions)));

    // Delete the first question
    component['deleteQuestion'](0);

    expect(component['questions']().length).toBe(1);
    expect(component['questions']()[0].id).toBe('2');
  });

  it('should reorder questions in same container onDrop', () => {
    const questions = [
      { id: '1', label: 'Q1', type: QuestionType.TEXT },
      { id: '2', label: 'Q2', type: QuestionType.TEXT },
    ];
    component['questions'].set(JSON.parse(JSON.stringify(questions)));

    const dropEvent: CdkDragDrop<any> = {
      previousIndex: 0,
      currentIndex: 1,
      item: { data: {} },
      previousContainer: { id: 'q', data: questions },
      container: { id: 'q', data: questions },
      isPointerOverContainer: true,
      distance: { x: 0, y: 0 },
    } as any;

    component['onDrop'](dropEvent);

    expect(component['questions']()[0].id).toBe('2');
    expect(component['questions']()[1].id).toBe('1');
  });

  it('should insert new question when dropped from different container', () => {
    component['questions'].set([]);

    const dropEvent: CdkDragDrop<any> = {
      previousIndex: 0,
      currentIndex: 0,
      item: { data: { label: QuestionType.DROPDOWN } },
      previousContainer: { id: 'toolbox', data: [] },
      container: { id: 'editor', data: [] },
      isPointerOverContainer: true,
      distance: { x: 0, y: 0 },
    } as any;

    component['onDrop'](dropEvent);

    const result = component['questions']();
    expect(result.length).toBe(1);
    expect(result[0].type).toBe(QuestionType.DROPDOWN);
    expect(result[0].options?.length).toBe(3);
  });
});
