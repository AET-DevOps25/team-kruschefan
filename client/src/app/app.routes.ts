import { Routes } from '@angular/router';
import { FormFillComponent } from './components/form-fill/form-fill.component';
import { TemplateEditorComponent } from './components/template-editor/template-editor.component';

export const routes: Routes = [
  {
    path: '',
    redirectTo: 'editor',
    pathMatch: 'full',
  },
  {
    path: 'editor',
    component: TemplateEditorComponent,
  },
  {
    path: 'form/:formId',
    component: FormFillComponent,
  },
  {
    path: 'response/:responseId',
    component: FormFillComponent,
    data: { readonly: true },
  },
];
