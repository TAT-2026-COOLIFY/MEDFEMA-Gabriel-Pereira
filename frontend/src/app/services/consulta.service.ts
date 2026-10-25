import { HttpClient, HttpParams } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import {
  DadosAgendamentoConsulta,
  DadosCancelamentoConsulta,
  DadosDetalhamentoConsulta,
  DadosListagemConsulta,
} from '../models/consulta';
import { Page } from '../models/page';

@Injectable({ providedIn: 'root' })
export class ConsultaService {
  private readonly http = inject(HttpClient);
  private readonly url = '/api/consultas';

  listar(pagina = 0): Observable<Page<DadosListagemConsulta>> {
    const params = new HttpParams().set('page', pagina);
    return this.http.get<Page<DadosListagemConsulta>>(this.url, { params });
  }

  agendar(dados: DadosAgendamentoConsulta): Observable<DadosDetalhamentoConsulta> {
    return this.http.post<DadosDetalhamentoConsulta>(this.url, dados);
  }

  // A API recebe o id e o motivo no corpo do DELETE, e não na URL.
  cancelar(dados: DadosCancelamentoConsulta): Observable<void> {
    return this.http.delete<void>(this.url, { body: dados });
  }
}
