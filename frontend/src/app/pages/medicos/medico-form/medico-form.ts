import { HttpErrorResponse } from '@angular/common/http';
import { Component, inject, OnInit, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { finalize } from 'rxjs';
import { ESPECIALIDADES, Especialidade, NOMES_ESPECIALIDADES } from '../../../models/medico';
import { MedicoService } from '../../../services/medico.service';
import { aplicarErrosDosCampos, mensagemDoCampo } from '../../../shared/erros-formulario';
import { mensagemDeErro } from '../../../shared/mensagem-erro';

// Mensagens para os campos com Validators.pattern, iguais às regras do backend.
const MENSAGENS_FORMATO: Record<string, string> = {
  crm: 'O CRM deve ter de 4 a 6 dígitos.',
  'endereco.cep': 'O CEP deve ter 8 dígitos, sem traço.',
};

@Component({
  selector: 'app-medico-form',
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './medico-form.html',
})
export class MedicoForm implements OnInit {
  private readonly service = inject(MedicoService);
  private readonly router = inject(Router);
  private readonly fb = inject(FormBuilder).nonNullable;

  // Na rota /medicos/:id/editar o id existe; em /medicos/novo ele fica null.
  protected readonly id = Number(inject(ActivatedRoute).snapshot.paramMap.get('id')) || null;

  protected readonly especialidades = ESPECIALIDADES;
  protected readonly nomesEspecialidades = NOMES_ESPECIALIDADES;
  protected readonly carregando = signal(false);
  protected readonly falhaAoCarregar = signal(false);
  protected readonly salvando = signal(false);
  protected readonly mensagemErro = signal<string | null>(null);

  protected readonly form = this.fb.group({
    nome: ['', Validators.required],
    email: ['', [Validators.required, Validators.email]],
    telefone: ['', Validators.required],
    crm: ['', [Validators.required, Validators.pattern(/^\d{4,6}$/)]],
    especialidade: this.fb.control<Especialidade | ''>('', Validators.required),
    endereco: this.fb.group({
      logradouro: ['', Validators.required],
      numero: [''],
      complemento: [''],
      bairro: ['', Validators.required],
      cidade: ['', Validators.required],
      uf: ['', Validators.required],
      cep: ['', [Validators.required, Validators.pattern(/^\d{8}$/)]],
    }),
  });

  ngOnInit(): void {
    if (!this.id) {
      return;
    }

    // O PUT da API não altera e-mail, CRM nem especialidade.
    this.form.controls.email.disable();
    this.form.controls.crm.disable();
    this.form.controls.especialidade.disable();

    this.carregando.set(true);
    this.service
      .detalhar(this.id)
      .pipe(finalize(() => this.carregando.set(false)))
      .subscribe({
        next: (medico) =>
          this.form.patchValue({
            nome: medico.nome,
            email: medico.email,
            telefone: medico.telefone,
            crm: medico.crm,
            especialidade: medico.especialidade,
            endereco: {
              ...medico.endereco,
              numero: medico.endereco.numero ?? '',
              complemento: medico.endereco.complemento ?? '',
            },
          }),
        error: (erro: HttpErrorResponse) => {
          this.falhaAoCarregar.set(true);
          this.mensagemErro.set(mensagemDeErro(erro));
        },
      });
  }

  protected salvar(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.salvando.set(true);
    this.mensagemErro.set(null);

    const valor = this.form.getRawValue();
    const requisicao = this.id
      ? this.service.atualizar({
          id: this.id,
          nome: valor.nome,
          telefone: valor.telefone,
          endereco: valor.endereco,
        })
      : this.service.cadastrar({ ...valor, especialidade: valor.especialidade as Especialidade });

    requisicao.pipe(finalize(() => this.salvando.set(false))).subscribe({
      next: () => this.router.navigate(['/medicos']),
      error: (erro: HttpErrorResponse) => this.tratarErro(erro),
    });
  }

  protected erroDoCampo(caminho: string): string | null {
    return mensagemDoCampo(this.form, caminho, MENSAGENS_FORMATO);
  }

  private tratarErro(erro: HttpErrorResponse): void {
    if (aplicarErrosDosCampos(this.form, erro)) {
      this.mensagemErro.set('Corrija os campos destacados.');
      return;
    }
    this.mensagemErro.set(mensagemDeErro(erro));
  }
}
