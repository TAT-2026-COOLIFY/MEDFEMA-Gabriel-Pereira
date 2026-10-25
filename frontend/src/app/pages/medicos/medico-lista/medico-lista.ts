import { HttpErrorResponse } from '@angular/common/http';
import { Component, inject, OnInit, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { finalize } from 'rxjs';
import { DadosListagemMedico, NOMES_ESPECIALIDADES } from '../../../models/medico';
import { Page } from '../../../models/page';
import { MedicoService } from '../../../services/medico.service';
import { mensagemDeErro } from '../../../shared/mensagem-erro';

@Component({
  selector: 'app-medico-lista',
  imports: [RouterLink],
  templateUrl: './medico-lista.html',
})
export class MedicoLista implements OnInit {
  private readonly service = inject(MedicoService);

  protected readonly nomesEspecialidades = NOMES_ESPECIALIDADES;
  protected readonly pagina = signal<Page<DadosListagemMedico> | null>(null);
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

  protected excluir(medico: DadosListagemMedico): void {
    if (!confirm(`Deseja inativar o médico ${medico.nome}?`)) {
      return;
    }
    this.service.excluir(medico.id).subscribe({
      next: () => this.carregar(this.paginaAposExclusao()),
      error: (erro: HttpErrorResponse) => this.mensagemErro.set(mensagemDeErro(erro)),
    });
  }

  // Se o médico inativado era o único da página, volta para a página anterior.
  private paginaAposExclusao(): number {
    const atual = this.pagina();
    if (!atual) {
      return 0;
    }
    return atual.content.length === 1 && atual.number > 0 ? atual.number - 1 : atual.number;
  }
}
