package med.fema.api.consulta.validacoes;

import med.fema.api.consulta.ConsultaRepository;
import med.fema.api.consulta.DadosAgendamentoConsulta;
import med.fema.api.infra.exception.ValidacaoException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ValidadorMedicoComOutraConsultaNoMesmoHorario implements ValidadorAgendamentoDeConsulta {

    @Autowired
    private ConsultaRepository repository;

    @Override
    public void validar(DadosAgendamentoConsulta dados) {
        if (dados.idMedico() == null) {
            return;
        }

        // A consulta dura 1 hora, então outra consulta do médico que comece
        // até 59 minutos antes ou depois ocupa o mesmo horário.
        var inicio = dados.data().minusMinutes(59);
        var fim = dados.data().plusMinutes(59);
        var medicoPossuiOutraConsultaNoMesmoHorario = repository.existsByMedicoIdAndDataBetweenAndMotivoCancelamentoIsNull(dados.idMedico(), inicio, fim);
        if (medicoPossuiOutraConsultaNoMesmoHorario) {
            throw new ValidacaoException("Médico já possui outra consulta agendada nesse mesmo horário");
        }
    }
}
