package med.fema.api.consulta.validacoes;

import med.fema.api.consulta.ConsultaRepository;
import med.fema.api.consulta.DadosAgendamentoConsulta;
import med.fema.api.infra.exception.ValidacaoException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ValidadorPacienteSemOutraConsultaNoDia implements ValidadorAgendamentoDeConsulta {

    @Autowired
    private ConsultaRepository repository;

    @Override
    public void validar(DadosAgendamentoConsulta dados) {
        var primeiroHorario = dados.data().withHour(7).withMinute(0).withSecond(0);
        var ultimoHorario = dados.data().withHour(18).withMinute(59).withSecond(59);
        var pacientePossuiOutraConsultaNoDia = repository.existsByPacienteIdAndDataBetweenAndMotivoCancelamentoIsNull(dados.idPaciente(), primeiroHorario, ultimoHorario);
        if (pacientePossuiOutraConsultaNoDia) {
            throw new ValidacaoException("Paciente já possui uma consulta agendada nesse dia");
        }
    }
}
