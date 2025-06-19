import { Routes } from '@angular/router';
import { FormFillComponent } from './components/form-fill/form-fill.component';
import { FormEditorComponent } from './components/form-editor/form-editor.component';

export const routes: Routes = [
  {
    path: '',
    redirectTo: 'editor',
    pathMatch: 'full',
  },
  {
    path: 'editor',
    component: FormEditorComponent,
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
