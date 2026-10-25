import { Endereco } from './endereco';

export interface DadosListagemPaciente {
  id: number;
  nome: string;
  email: string;
  cpf: string;
}

export interface DadosDetalhamentoPaciente {
  id: number;
  nome: string;
  email: string;
  cpf: string;
  telefone: string;
  endereco: Endereco;
}

export interface DadosCadastroPaciente {
  nome: string;
  email: string;
  telefone: string;
  cpf: string;
  endereco: Endereco;
}

// Somente nome, telefone e endereço podem ser alterados; campos omitidos não mudam.
export interface DadosAtualizacaoPaciente {
  id: number;
  nome?: string;
  telefone?: string;
  endereco?: Endereco;
}
