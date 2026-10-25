import { Routes } from '@angular/router';
import { MedicoForm } from './pages/medicos/medico-form/medico-form';
import { MedicoLista } from './pages/medicos/medico-lista/medico-lista';

export const routes: Routes = [
  { path: '', pathMatch: 'full', redirectTo: 'medicos' },
  { path: 'medicos', component: MedicoLista, title: 'Médicos | MedFema' },
  { path: 'medicos/novo', component: MedicoForm, title: 'Novo médico | MedFema' },
  { path: 'medicos/:id/editar', component: MedicoForm, title: 'Editar médico | MedFema' },
  { path: '**', redirectTo: 'medicos' },
];
