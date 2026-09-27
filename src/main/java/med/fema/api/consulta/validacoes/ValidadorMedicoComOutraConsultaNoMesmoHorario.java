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

        var medicoPossuiOutraConsultaNoMesmoHorario = repository.existsByMedicoIdAndDataAndMotivoCancelamentoIsNull(dados.idMedico(), dados.data());
        if (medicoPossuiOutraConsultaNoMesmoHorario) {
            throw new ValidacaoException("Médico já possui outra consulta agendada nesse mesmo horário");
        }
    }
}
