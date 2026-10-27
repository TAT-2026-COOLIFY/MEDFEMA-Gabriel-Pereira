package med.fema.api.controller;

import jakarta.validation.Valid;
import med.fema.api.consulta.ConsultaRepository;
import med.fema.api.consulta.MotivoCancelamento;
import med.fema.api.paciente.DadosAtualizacaoPaciente;
import med.fema.api.paciente.DadosCadastroPaciente;
import med.fema.api.paciente.DadosDetalhamentoPaciente;
import med.fema.api.paciente.DadosListagemPaciente;
import med.fema.api.paciente.Paciente;
import med.fema.api.paciente.PacienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("pacientes")
public class PacienteController {

    @Autowired
    private PacienteRepository repository;

    @Autowired
    private ConsultaRepository consultaRepository;

    @PostMapping
    @Transactional
    public void cadastrar(@RequestBody @Valid DadosCadastroPaciente dados) {
        // O CPF é único no banco, então o recadastro de um paciente inativado reativa o cadastro antigo.
        var inativo = repository.findByCpfAndAtivoFalse(Paciente.formatarCpf(dados.cpf()));
        if (inativo.isPresent()) {
            inativo.get().reativar(dados);
            return;
        }

        repository.save(new Paciente(dados));
    }

    @GetMapping
    public Page<DadosListagemPaciente> listar(@PageableDefault(size = 10, sort = {"nome"}) Pageable paginacao) {
        return repository.findAllByAtivoTrue(paginacao).map(DadosListagemPaciente::new);
    }

    @GetMapping("/{id}")
    public DadosDetalhamentoPaciente detalhar(@PathVariable Long id) {
        var paciente = repository.getReferenceById(id);
        return new DadosDetalhamentoPaciente(paciente);
    }

    @PutMapping
    @Transactional
    public void atualizar(@RequestBody @Valid DadosAtualizacaoPaciente dados) {
        var paciente = repository.getReferenceById(dados.id());
        paciente.atualizarInformacoes(dados);
    }

    @DeleteMapping("/{id}")
    @Transactional
    public void excluir(@PathVariable Long id) {
        var paciente = repository.getReferenceById(id);
        paciente.excluir();

        // As consultas futuras do paciente inativado não vão acontecer, então são canceladas.
        consultaRepository.findAllByPacienteIdAndDataAfterAndMotivoCancelamentoIsNull(id, LocalDateTime.now())
                .forEach(consulta -> consulta.cancelar(MotivoCancelamento.PACIENTE_DESISTIU));
    }
}
