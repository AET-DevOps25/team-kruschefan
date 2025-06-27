import { Routes } from '@angular/router';
import { FormFillComponent } from './components/form-fill/form-fill.component';
import { TemplateEditorComponent } from './components/template-editor/template-editor.component';
import { HomeComponent } from './components/home/home.component';
import { UserManagementComponent } from './components/user-management/user-management.component';
import { AuthGuard } from './guards/auth.guard';

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
    canActivate: [AuthGuard],
  },
  {
    path: 'editor/:templateId',
    component: TemplateEditorComponent,
    canActivate: [AuthGuard],
  },
  {
    path: 'form/:formId',
    component: FormFillComponent,
  },
  {
    path: 'response/:responseId',
    component: FormFillComponent,
    data: { readonly: true },
    canActivate: [AuthGuard],
  },
  {
    path: 'user-management',
    component: UserManagementComponent,
    canActivate: [AuthGuard],
  },
];
