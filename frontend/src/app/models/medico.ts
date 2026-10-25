import { Endereco } from './endereco';

export const ESPECIALIDADES = ['ORTOPEDIA', 'CARDIOLOGIA', 'GINECOLOGIA', 'DERMATOLOGIA'] as const;

export type Especialidade = (typeof ESPECIALIDADES)[number];

export interface DadosListagemMedico {
  id: number;
  nome: string;
  email: string;
  crm: string;
  especialidade: Especialidade;
}

export interface DadosDetalhamentoMedico {
  id: number;
  nome: string;
  email: string;
  crm: string;
  telefone: string;
  especialidade: Especialidade;
  endereco: Endereco;
}

export interface DadosCadastroMedico {
  nome: string;
  email: string;
  telefone: string;
  crm: string;
  especialidade: Especialidade;
  endereco: Endereco;
}

// Somente nome, telefone e endereço podem ser alterados; campos omitidos não mudam.
export interface DadosAtualizacaoMedico {
  id: number;
  nome?: string;
  telefone?: string;
  endereco?: Endereco;
}
