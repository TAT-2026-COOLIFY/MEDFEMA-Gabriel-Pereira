import { Routes } from '@angular/router';
import { MedicoForm } from './pages/medicos/medico-form/medico-form';
import { MedicoLista } from './pages/medicos/medico-lista/medico-lista';
import { PacienteForm } from './pages/pacientes/paciente-form/paciente-form';
import { PacienteLista } from './pages/pacientes/paciente-lista/paciente-lista';

export const routes: Routes = [
  { path: '', pathMatch: 'full', redirectTo: 'medicos' },
  { path: 'medicos', component: MedicoLista, title: 'Médicos | MedFema' },
  { path: 'medicos/novo', component: MedicoForm, title: 'Novo médico | MedFema' },
  { path: 'medicos/:id/editar', component: MedicoForm, title: 'Editar médico | MedFema' },
  { path: 'pacientes', component: PacienteLista, title: 'Pacientes | MedFema' },
  { path: 'pacientes/novo', component: PacienteForm, title: 'Novo paciente | MedFema' },
  { path: 'pacientes/:id/editar', component: PacienteForm, title: 'Editar paciente | MedFema' },
  { path: '**', redirectTo: 'medicos' },
];
