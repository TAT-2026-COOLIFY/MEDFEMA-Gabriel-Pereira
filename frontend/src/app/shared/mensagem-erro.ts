import { HttpErrorResponse } from '@angular/common/http';

// Converte um erro HTTP da API em uma mensagem para mostrar na tela.
// Pelo proxy de desenvolvimento, a API fora do ar também chega como erro 500.
export function mensagemDeErro(erro: HttpErrorResponse): string {
  if (erro.status === 400 && typeof erro.error === 'string') {
    return erro.error;
  }
  if (erro.status === 404) {
    return 'Registro não encontrado.';
  }
  if (erro.status === 0 || erro.status >= 500) {
    return 'Não foi possível concluir a operação. Verifique se a API está rodando e tente novamente.';
  }
  return `Erro inesperado (status ${erro.status}).`;
}
