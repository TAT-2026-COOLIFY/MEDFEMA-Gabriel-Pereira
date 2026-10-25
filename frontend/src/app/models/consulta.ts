import { Especialidade } from './medico';

export const MOTIVOS_CANCELAMENTO = ['PACIENTE_DESISTIU', 'MEDICO_CANCELOU', 'OUTROS'] as const;

export type MotivoCancelamento = (typeof MOTIVOS_CANCELAMENTO)[number];

export const NOMES_MOTIVOS_CANCELAMENTO: Record<MotivoCancelamento, string> = {
  PACIENTE_DESISTIU: 'Paciente desistiu',
  MEDICO_CANCELOU: 'Médico cancelou',
  OUTROS: 'Outros',
};

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

export interface DadosListagemConsulta {
  id: number;
  data: string;
  idMedico: number;
  nomeMedico: string;
  especialidade: Especialidade;
  idPaciente: number;
  nomePaciente: string;
}

export interface DadosCancelamentoConsulta {
  idConsulta: number;
  motivo: MotivoCancelamento;
}
