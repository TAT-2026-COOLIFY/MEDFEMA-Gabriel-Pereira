import { HttpClient, HttpParams } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Page } from '../models/page';
import {
  DadosAtualizacaoPaciente,
  DadosCadastroPaciente,
  DadosDetalhamentoPaciente,
  DadosListagemPaciente,
} from '../models/paciente';

@Injectable({ providedIn: 'root' })
export class PacienteService {
  private readonly http = inject(HttpClient);
  private readonly url = '/api/pacientes';

  listar(pagina = 0): Observable<Page<DadosListagemPaciente>> {
    const params = new HttpParams().set('page', pagina);
    return this.http.get<Page<DadosListagemPaciente>>(this.url, { params });
  }

  detalhar(id: number): Observable<DadosDetalhamentoPaciente> {
    return this.http.get<DadosDetalhamentoPaciente>(`${this.url}/${id}`);
  }

  cadastrar(dados: DadosCadastroPaciente): Observable<void> {
    return this.http.post<void>(this.url, dados);
  }

  atualizar(dados: DadosAtualizacaoPaciente): Observable<void> {
    return this.http.put<void>(this.url, dados);
  }

  excluir(id: number): Observable<void> {
    return this.http.delete<void>(`${this.url}/${id}`);
  }
}
