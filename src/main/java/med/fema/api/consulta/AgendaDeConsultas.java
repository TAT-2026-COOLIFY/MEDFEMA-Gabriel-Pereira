package med.fema.api.consulta;

import med.fema.api.consulta.validacoes.ValidadorAgendamentoDeConsulta;
import med.fema.api.consulta.validacoes.ValidadorCancelamentoDeConsulta;
import med.fema.api.infra.exception.ValidacaoException;
import med.fema.api.medico.Medico;
import med.fema.api.medico.MedicoRepository;
import med.fema.api.paciente.PacienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AgendaDeConsultas {

    @Autowired
    private ConsultaRepository consultaRepository;

    @Autowired
    private MedicoRepository medicoRepository;

    @Autowired
    private PacienteRepository pacienteRepository;

    @Autowired
    private List<ValidadorAgendamentoDeConsulta> validadores;

    @Autowired
    private List<ValidadorCancelamentoDeConsulta> validadoresCancelamento;

    public void cancelar(DadosCancelamentoConsulta dados) {
        if (!consultaRepository.existsById(dados.idConsulta())) {
            throw new ValidacaoException("Id da consulta informado não existe!");
        }

        validadoresCancelamento.forEach(v -> v.validar(dados));

        var consulta = consultaRepository.getReferenceById(dados.idConsulta());
        consulta.cancelar(dados.motivo());
    }

    public DadosDetalhamentoConsulta agendar(DadosAgendamentoConsulta dados) {
        travarParaAgendamento(dados);

        if (!pacienteRepository.existsById(dados.idPaciente())) {
            throw new ValidacaoException("Id do paciente informado não existe!");
        }

        if (dados.idMedico() != null && !medicoRepository.existsById(dados.idMedico())) {
            throw new ValidacaoException("Id do médico informado não existe!");
        }

        validadores.forEach(v -> v.validar(dados));

        var paciente = pacienteRepository.getReferenceById(dados.idPaciente());
        var medico = escolherMedico(dados);
        if (medico == null) {
            throw new ValidacaoException("Não existe médico disponível nessa data/hora!");
        }

        var consulta = new Consulta(null, medico, paciente, dados.data());
        consultaRepository.save(consulta);

        return new DadosDetalhamentoConsulta(consulta);
    }

    // Trava o paciente e o(s) médico(s) até o fim da transação, para que duas requisições
    // simultâneas não passem juntas pelos validadores e marquem o mesmo horário.
    // Precisa vir antes de qualquer outra consulta: no MySQL, as leituras seguintes só
    // enxergam o que a outra requisição gravou se começarem depois da trava.
    private void travarParaAgendamento(DadosAgendamentoConsulta dados) {
        pacienteRepository.travarParaAgendamento(dados.idPaciente());
        if (dados.idMedico() != null) {
            medicoRepository.travarParaAgendamento(dados.idMedico());
        } else if (dados.especialidade() != null) {
            medicoRepository.travarMedicosDaEspecialidade(dados.especialidade());
        }
    }

    private Medico escolherMedico(DadosAgendamentoConsulta dados) {
        if (dados.idMedico() != null) {
            return medicoRepository.getReferenceById(dados.idMedico());
        }

        if (dados.especialidade() == null) {
            throw new ValidacaoException("Especialidade é obrigatória quando o médico não for escolhido!");
        }

        // Mesmo intervalo do ValidadorMedicoComOutraConsultaNoMesmoHorario: a consulta dura 1 hora.
        var inicio = dados.data().minusMinutes(59);
        var fim = dados.data().plusMinutes(59);
        return medicoRepository.escolherMedicoAleatorioLivreNaData(dados.especialidade(), inicio, fim);
    }
}
