package med.fema.api.consulta.validacoes;

import med.fema.api.consulta.DadosAgendamentoConsulta;
import med.fema.api.infra.exception.ValidacaoException;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;

@Component
public class ValidadorHorarioFuncionamentoClinica implements ValidadorAgendamentoDeConsulta {

    @Override
    public void validar(DadosAgendamentoConsulta dados) {
        var dataConsulta = dados.data();
        var domingo = dataConsulta.getDayOfWeek().equals(DayOfWeek.SUNDAY);
        var antesDaAberturaDaClinica = dataConsulta.getHour() < 7;
        var depoisDoEncerramentoDaClinica = dataConsulta.getHour() > 18 || (dataConsulta.getHour() == 18 && dataConsulta.getMinute() > 0);

        if (domingo || antesDaAberturaDaClinica || depoisDoEncerramentoDaClinica) {
            throw new ValidacaoException("Consulta fora do horário de funcionamento da clínica (segunda a sábado, das 07:00 às 19:00)");
        }
    }
}
