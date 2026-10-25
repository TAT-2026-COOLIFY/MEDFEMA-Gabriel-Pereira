import { HttpErrorResponse } from '@angular/common/http';
import { Component, inject, OnInit, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { finalize } from 'rxjs';
import { PacienteService } from '../../../services/paciente.service';
import { aplicarErrosDosCampos, mensagemDoCampo } from '../../../shared/erros-formulario';
import { mensagemDeErro } from '../../../shared/mensagem-erro';

// Mensagens para os campos com Validators.pattern, iguais às regras do backend.
const MENSAGENS_FORMATO: Record<string, string> = {
  cpf: 'Informe o CPF no formato 000.000.000-00 ou só os 11 dígitos.',
  'endereco.cep': 'O CEP deve ter 8 dígitos, sem traço.',
};

@Component({
  selector: 'app-paciente-form',
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './paciente-form.html',
})
export class PacienteForm implements OnInit {
  private readonly service = inject(PacienteService);
  private readonly router = inject(Router);
  private readonly fb = inject(FormBuilder).nonNullable;

  // Na rota /pacientes/:id/editar o id existe; em /pacientes/novo ele fica null.
  protected readonly id = Number(inject(ActivatedRoute).snapshot.paramMap.get('id')) || null;

  protected readonly carregando = signal(false);
  protected readonly falhaAoCarregar = signal(false);
  protected readonly salvando = signal(false);
  protected readonly mensagemErro = signal<string | null>(null);

  protected readonly form = this.fb.group({
    nome: ['', Validators.required],
    email: ['', [Validators.required, Validators.email]],
    telefone: ['', Validators.required],
    cpf: ['', [Validators.required, Validators.pattern(/^\d{3}\.?\d{3}\.?\d{3}-?\d{2}$/)]],
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

    // O PUT da API não altera e-mail nem CPF.
    this.form.controls.email.disable();
    this.form.controls.cpf.disable();

    this.carregando.set(true);
    this.service
      .detalhar(this.id)
      .pipe(finalize(() => this.carregando.set(false)))
      .subscribe({
        next: (paciente) =>
          this.form.patchValue({
            nome: paciente.nome,
            email: paciente.email,
            telefone: paciente.telefone,
            cpf: paciente.cpf,
            endereco: {
              ...paciente.endereco,
              numero: paciente.endereco.numero ?? '',
              complemento: paciente.endereco.complemento ?? '',
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
      : this.service.cadastrar(valor);

    requisicao.pipe(finalize(() => this.salvando.set(false))).subscribe({
      next: () => this.router.navigate(['/pacientes']),
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
