import { HttpErrorResponse } from '@angular/common/http';
import { Component, inject, OnInit, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { finalize } from 'rxjs';
import { DadosListagemPaciente } from '../../../models/paciente';
import { Page } from '../../../models/page';
import { PacienteService } from '../../../services/paciente.service';
import { mensagemDeErro } from '../../../shared/mensagem-erro';

@Component({
  selector: 'app-paciente-lista',
  imports: [RouterLink],
  templateUrl: './paciente-lista.html',
})
export class PacienteLista implements OnInit {
  private readonly service = inject(PacienteService);

  protected readonly pagina = signal<Page<DadosListagemPaciente> | null>(null);
  protected readonly carregando = signal(false);
  protected readonly mensagemErro = signal<string | null>(null);

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

  protected excluir(paciente: DadosListagemPaciente): void {
    if (
      !confirm(
        `Deseja inativar o paciente ${paciente.nome}? As consultas futuras serão canceladas.`,
      )
    ) {
      return;
    }
    this.service.excluir(paciente.id).subscribe({
      next: () => this.carregar(this.paginaAposExclusao()),
      error: (erro: HttpErrorResponse) => this.mensagemErro.set(mensagemDeErro(erro)),
    });
  }

  // Se o paciente inativado era o único da página, volta para a página anterior.
  private paginaAposExclusao(): number {
    const atual = this.pagina();
    if (!atual) {
      return 0;
    }
    return atual.content.length === 1 && atual.number > 0 ? atual.number - 1 : atual.number;
  }
}
