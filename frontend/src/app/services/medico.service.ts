import { HttpClient, HttpParams } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import {
  DadosAtualizacaoMedico,
  DadosCadastroMedico,
  DadosDetalhamentoMedico,
  DadosListagemMedico,
} from '../models/medico';
import { Page } from '../models/page';

@Injectable({ providedIn: 'root' })
export class MedicoService {
  private readonly http = inject(HttpClient);
  private readonly url = '/api/medicos';

  listar(pagina = 0, tamanho = 10): Observable<Page<DadosListagemMedico>> {
    const params = new HttpParams().set('page', pagina).set('size', tamanho);
    return this.http.get<Page<DadosListagemMedico>>(this.url, { params });
  }

  detalhar(id: number): Observable<DadosDetalhamentoMedico> {
    return this.http.get<DadosDetalhamentoMedico>(`${this.url}/${id}`);
  }

  cadastrar(dados: DadosCadastroMedico): Observable<void> {
    return this.http.post<void>(this.url, dados);
  }

  atualizar(dados: DadosAtualizacaoMedico): Observable<void> {
    return this.http.put<void>(this.url, dados);
  }

  excluir(id: number): Observable<void> {
    return this.http.delete<void>(`${this.url}/${id}`);
  }
}
