import { Routes } from '@angular/router';
import { ConsultaForm } from './pages/consultas/consulta-form/consulta-form';
import { ConsultaLista } from './pages/consultas/consulta-lista/consulta-lista';
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
  { path: 'consultas', component: ConsultaLista, title: 'Consultas | MedFema' },
  { path: 'consultas/nova', component: ConsultaForm, title: 'Agendar consulta | MedFema' },
  { path: '**', redirectTo: 'medicos' },
];
