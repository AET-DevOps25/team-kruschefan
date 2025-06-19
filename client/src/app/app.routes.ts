import { Routes } from '@angular/router';
import { FormFillComponent } from './components/form-fill/form-fill.component';
import { TemplateEditorComponent } from './components/template-editor/template-editor.component';
import { HomeComponent } from './components/home/home.component';
import { UserManagementComponent } from './components/user-management/user-management.component';

export const routes: Routes = [
  {
    path: '',
    redirectTo: 'home',
    pathMatch: 'full',
  },
  {
    path: 'home',
    component: HomeComponent,
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
  {
    path: 'user-management',
    component: UserManagementComponent,
  },
];
