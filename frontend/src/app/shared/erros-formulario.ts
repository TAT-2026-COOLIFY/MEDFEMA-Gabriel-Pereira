import { HttpErrorResponse } from '@angular/common/http';
import { AbstractControl } from '@angular/forms';
import { ErroValidacao } from '../models/erro-validacao';

// Mensagem de erro de um campo, ou null se ele estiver válido ou ainda não foi tocado.
// mensagensFormato traz o texto dos campos com Validators.pattern, pelo caminho do campo.
export function mensagemDoCampo(
  form: AbstractControl,
  caminho: string,
  mensagensFormato: Record<string, string> = {},
): string | null {
  const controle = form.get(caminho);
  if (!controle?.errors || !controle.touched) {
    return null;
  }

  const erros = controle.errors;
  if (erros['servidor']) {
    return erros['servidor'];
  }
  if (erros['required']) {
    return 'Campo obrigatório.';
  }
  if (erros['email']) {
    return 'Informe um e-mail válido.';
  }
  if (erros['pattern']) {
    return mensagensFormato[caminho] ?? 'Formato inválido.';
  }
  return 'Valor inválido.';
}

// Erros 400 por campo voltam como lista [{ campo, mensagem }] e são exibidos no campo
// correspondente; o campo usa o mesmo caminho do formulário (ex.: 'endereco.cep').
// Retorna false quando o erro não é desse tipo, para o componente tratar de outra forma.
export function aplicarErrosDosCampos(form: AbstractControl, erro: HttpErrorResponse): boolean {
  if (erro.status !== 400 || !Array.isArray(erro.error)) {
    return false;
  }
  for (const { campo, mensagem } of erro.error as ErroValidacao[]) {
    form.get(campo)?.setErrors({ servidor: mensagem });
  }
  form.markAllAsTouched();
  return true;
}
