import { Especialidade } from './medico';

export const MOTIVOS_CANCELAMENTO = ['PACIENTE_DESISTIU', 'MEDICO_CANCELOU', 'OUTROS'] as const;

export type MotivoCancelamento = (typeof MOTIVOS_CANCELAMENTO)[number];

// Sem idMedico, a API escolhe um médico livre da especialidade informada.
// A data segue o formato ISO local, ex.: '2027-03-15T14:00:00'.
export interface DadosAgendamentoConsulta {
  idMedico?: number | null;
  idPaciente: number;
  data: string;
  especialidade?: Especialidade | null;
}

export interface DadosDetalhamentoConsulta {
  id: number;
  idMedico: number;
  idPaciente: number;
  data: string;
}

export interface DadosCancelamentoConsulta {
  idConsulta: number;
  motivo: MotivoCancelamento;
}
