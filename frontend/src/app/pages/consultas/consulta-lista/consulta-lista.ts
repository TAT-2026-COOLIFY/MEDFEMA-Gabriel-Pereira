import { DatePipe } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { Component, inject, OnInit, signal } from '@angular/core';
import { FormControl, ReactiveFormsModule, Validators } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { finalize } from 'rxjs';
import {
  DadosListagemConsulta,
  MOTIVOS_CANCELAMENTO,
  MotivoCancelamento,
  NOMES_MOTIVOS_CANCELAMENTO,
} from '../../../models/consulta';
import { NOMES_ESPECIALIDADES } from '../../../models/medico';
import { Page } from '../../../models/page';
import { ConsultaService } from '../../../services/consulta.service';
import { mensagemDeErro } from '../../../shared/mensagem-erro';

@Component({
  selector: 'app-consulta-lista',
  imports: [DatePipe, ReactiveFormsModule, RouterLink],
  templateUrl: './consulta-lista.html',
})
export class ConsultaLista implements OnInit {
  private readonly service = inject(ConsultaService);

  protected readonly nomesEspecialidades = NOMES_ESPECIALIDADES;
  protected readonly motivos = MOTIVOS_CANCELAMENTO;
  protected readonly nomesMotivos = NOMES_MOTIVOS_CANCELAMENTO;

  protected readonly pagina = signal<Page<DadosListagemConsulta> | null>(null);
  protected readonly carregando = signal(false);
  protected readonly mensagemErro = signal<string | null>(null);

  protected readonly consultaEmCancelamento = signal<DadosListagemConsulta | null>(null);
  protected readonly cancelando = signal(false);
  protected readonly motivo = new FormControl<MotivoCancelamento | ''>('', {
    nonNullable: true,
    validators: Validators.required,
  });

  ngOnInit(): void {
    this.carregar(0);
  }

  protected carregar(numeroPagina: number): void {
    this.carregando.set(true);
    this.mensagemErro.set(null);
    this.service
      .listar(numeroPagina)
      .pipe(finalize(() => this.carregando.set(false)))
      .subscribe({
        next: (pagina) => this.pagina.set(pagina),
        error: (erro: HttpErrorResponse) => this.mensagemErro.set(mensagemDeErro(erro)),
      });
  }

  protected abrirCancelamento(consulta: DadosListagemConsulta): void {
    this.motivo.reset('');
    this.mensagemErro.set(null);
    this.consultaEmCancelamento.set(consulta);
  }

  protected fecharCancelamento(): void {
    this.consultaEmCancelamento.set(null);
  }

  protected confirmarCancelamento(): void {
    const consulta = this.consultaEmCancelamento();
    if (!consulta || this.motivo.invalid) {
      this.motivo.markAsTouched();
      return;
    }

    this.cancelando.set(true);
    this.mensagemErro.set(null);
    this.service
      .cancelar({ idConsulta: consulta.id, motivo: this.motivo.value as MotivoCancelamento })
      .pipe(finalize(() => this.cancelando.set(false)))
      .subscribe({
        next: () => {
          this.fecharCancelamento();
          this.carregar(this.paginaAposCancelamento());
        },
        error: (erro: HttpErrorResponse) => this.mensagemErro.set(mensagemDeErro(erro)),
      });
  }

  // Se a consulta cancelada era a única da página, volta para a página anterior.
  private paginaAposCancelamento(): number {
    const atual = this.pagina();
    if (!atual) {
      return 0;
    }
    return atual.content.length === 1 && atual.number > 0 ? atual.number - 1 : atual.number;
  }
}
