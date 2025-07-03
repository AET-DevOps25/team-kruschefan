import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormFillComponent } from './form-fill.component';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

describe('FormFillComponent (normal mode)', () => {
  let component: FormFillComponent;
  let fixture: ComponentFixture<FormFillComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [FormFillComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: {
            params: of({}),
            snapshot: {
              data: {},
              paramMap: {
                get: () => null,
              },
            },
          },
        },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(FormFillComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should initialize the form with 8 controls', () => {
    expect(component['form']).toBeDefined();
    expect(Object.keys(component['form'].controls).length).toBe(8);
  });

  it('should have enabled form in non-readonly mode', () => {
    expect(component['form'].disabled).toBeFalse();
  });
});

describe('FormFillComponent (readonly mode)', () => {
  let component: FormFillComponent;
  let fixture: ComponentFixture<FormFillComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [FormFillComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: {
            params: of({}),
            snapshot: {
              data: { readonly: true },
              paramMap: {
                get: () => null,
              },
            },
          },
        },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(FormFillComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should disable the form in readonly mode', () => {
    expect(component['form'].disabled).toBeTrue();
  });
});
