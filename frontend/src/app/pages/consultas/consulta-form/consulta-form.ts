import { HttpErrorResponse } from '@angular/common/http';
import { Component, computed, inject, OnInit, signal } from '@angular/core';
import { takeUntilDestroyed, toSignal } from '@angular/core/rxjs-interop';
import { FormBuilder, ReactiveFormsModule, ValidatorFn, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { finalize, forkJoin } from 'rxjs';
import {
  DadosListagemMedico,
  ESPECIALIDADES,
  Especialidade,
  NOMES_ESPECIALIDADES,
} from '../../../models/medico';
import { DadosListagemPaciente } from '../../../models/paciente';
import { ConsultaService } from '../../../services/consulta.service';
import { MedicoService } from '../../../services/medico.service';
import { PacienteService } from '../../../services/paciente.service';
import { aplicarErrosDosCampos, mensagemDoCampo } from '../../../shared/erros-formulario';
import { mensagemDeErro } from '../../../shared/mensagem-erro';

// Quantidade máxima de médicos e pacientes carregados para as listas de seleção.
const TAMANHO_LISTAS = 100;

// Sem médico escolhido, a API precisa da especialidade para sortear um médico livre.
const medicoOuEspecialidade: ValidatorFn = (grupo) =>
  grupo.get('idMedico')?.value || grupo.get('especialidade')?.value
    ? null
    : { medicoOuEspecialidade: true };

// Data e hora atuais no formato do input datetime-local (ex.: '2027-03-15T14:00').
function agoraLocal(): string {
  const agora = new Date();
  agora.setMinutes(agora.getMinutes() - agora.getTimezoneOffset());
  return agora.toISOString().slice(0, 16);
}

@Component({
  selector: 'app-consulta-form',
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './consulta-form.html',
})
export class ConsultaForm implements OnInit {
  private readonly consultaService = inject(ConsultaService);
  private readonly medicoService = inject(MedicoService);
  private readonly pacienteService = inject(PacienteService);
  private readonly router = inject(Router);
  private readonly fb = inject(FormBuilder).nonNullable;

  protected readonly especialidades = ESPECIALIDADES;
  protected readonly nomesEspecialidades = NOMES_ESPECIALIDADES;
  protected readonly dataMinima = agoraLocal();

  protected readonly pacientes = signal<DadosListagemPaciente[]>([]);
  protected readonly medicos = signal<DadosListagemMedico[]>([]);
  protected readonly carregando = signal(false);
  protected readonly salvando = signal(false);
  protected readonly mensagemErro = signal<string | null>(null);

  protected readonly form = this.fb.group(
    {
      idPaciente: this.fb.control<number | null>(null, Validators.required),
      especialidade: this.fb.control<Especialidade | ''>(''),
      idMedico: this.fb.control<number | null>(null),
      data: ['', Validators.required],
    },
    { validators: medicoOuEspecialidade },
  );

  private readonly especialidadeEscolhida = toSignal(
    this.form.controls.especialidade.valueChanges,
    { initialValue: this.form.controls.especialidade.value },
  );

  // Com uma especialidade escolhida, a lista de médicos mostra só os dela.
  protected readonly medicosDaEspecialidade = computed(() => {
    const especialidade = this.especialidadeEscolhida();
    return especialidade
      ? this.medicos().filter((medico) => medico.especialidade === especialidade)
      : this.medicos();
  });

  constructor() {
    // Ao trocar a especialidade, desmarca o médico se ele não for dela.
    this.form.controls.especialidade.valueChanges
      .pipe(takeUntilDestroyed())
      .subscribe((especialidade) => {
        const medico = this.medicos().find((m) => m.id === this.form.controls.idMedico.value);
        if (especialidade && medico && medico.especialidade !== especialidade) {
          this.form.controls.idMedico.setValue(null);
        }
      });
  }

  ngOnInit(): void {
    this.carregando.set(true);
    forkJoin({
      pacientes: this.pacienteService.listar(0, TAMANHO_LISTAS),
      medicos: this.medicoService.listar(0, TAMANHO_LISTAS),
    })
      .pipe(finalize(() => this.carregando.set(false)))
      .subscribe({
        next: ({ pacientes, medicos }) => {
          this.pacientes.set(pacientes.content);
          this.medicos.set(medicos.content);
        },
        error: (erro: HttpErrorResponse) => this.mensagemErro.set(mensagemDeErro(erro)),
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
    this.consultaService
      .agendar({
        idPaciente: valor.idPaciente as number,
        idMedico: valor.idMedico,
        especialidade: valor.especialidade || null,
        data: valor.data,
      })
      .pipe(finalize(() => this.salvando.set(false)))
      .subscribe({
        next: () => this.router.navigate(['/consultas']),
        error: (erro: HttpErrorResponse) => this.tratarErro(erro),
      });
  }

  protected erroDoCampo(caminho: string): string | null {
    return mensagemDoCampo(this.form, caminho);
  }

  protected faltaMedicoOuEspecialidade(): boolean {
    const { especialidade, idMedico } = this.form.controls;
    return (
      this.form.hasError('medicoOuEspecialidade') && (especialidade.touched || idMedico.touched)
    );
  }

  private tratarErro(erro: HttpErrorResponse): void {
    if (aplicarErrosDosCampos(this.form, erro)) {
      this.mensagemErro.set('Corrija os campos destacados.');
      return;
    }
    this.mensagemErro.set(mensagemDeErro(erro));
  }
}
