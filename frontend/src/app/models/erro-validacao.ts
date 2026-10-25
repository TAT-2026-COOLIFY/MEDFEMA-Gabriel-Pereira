// Item da lista retornada no erro 400 quando algum campo da requisição é inválido.
export interface ErroValidacao {
  campo: string;
  mensagem: string;
}
