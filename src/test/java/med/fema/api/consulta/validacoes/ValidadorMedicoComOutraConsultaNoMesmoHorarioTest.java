package med.fema.api.consulta.validacoes;

import med.fema.api.consulta.ConsultaRepository;
import med.fema.api.consulta.DadosAgendamentoConsulta;
import med.fema.api.infra.exception.ValidacaoException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ValidadorMedicoComOutraConsultaNoMesmoHorarioTest {

    @InjectMocks
    private ValidadorMedicoComOutraConsultaNoMesmoHorario validador;

    @Mock
    private ConsultaRepository repository;

    @Test
    @DisplayName("Deveria lancar ValidacaoException quando o medico tiver consulta que comeca a menos de 1 hora do horario")
    void validar_consultaSobreposta() {
        var data = LocalDateTime.now().plusDays(2).withHour(14).withMinute(30).withSecond(0).withNano(0);
        var dados = new DadosAgendamentoConsulta(1L, 2L, data, null);

        when(repository.existsByMedicoIdAndDataBetweenAndMotivoCancelamentoIsNull(1L, data.minusMinutes(59), data.plusMinutes(59)))
                .thenReturn(true);

        assertThrows(ValidacaoException.class, () -> validador.validar(dados));
    }
}
