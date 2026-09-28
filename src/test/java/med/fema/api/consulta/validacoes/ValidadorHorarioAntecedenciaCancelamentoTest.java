package med.fema.api.consulta.validacoes;

import med.fema.api.consulta.Consulta;
import med.fema.api.consulta.ConsultaRepository;
import med.fema.api.consulta.DadosCancelamentoConsulta;
import med.fema.api.consulta.MotivoCancelamento;
import med.fema.api.infra.exception.ValidacaoException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ValidadorHorarioAntecedenciaCancelamentoTest {

    @InjectMocks
    private ValidadorHorarioAntecedenciaCancelamento validador;

    @Mock
    private ConsultaRepository repository;

    @Mock
    private Consulta consulta;

    @Test
    @DisplayName("Deveria lancar ValidacaoException quando cancelamento for solicitado com menos de 24 horas de antecedencia")
    void validar_menosDe24Horas() {
        var dados = new DadosCancelamentoConsulta(1L, MotivoCancelamento.PACIENTE_DESISTIU);
        var dataConsulta = LocalDateTime.now().plusHours(12);

        when(repository.getReferenceById(1L)).thenReturn(consulta);
        when(consulta.getData()).thenReturn(dataConsulta);

        assertThrows(ValidacaoException.class, () -> validador.validar(dados));
    }

    @Test
    @DisplayName("Nao deveria lancar excecao quando cancelamento for solicitado com mais de 24 horas de antecedencia")
    void validar_maisDe24Horas() {
        var dados = new DadosCancelamentoConsulta(1L, MotivoCancelamento.PACIENTE_DESISTIU);
        var dataConsulta = LocalDateTime.now().plusHours(48);

        when(repository.getReferenceById(1L)).thenReturn(consulta);
        when(consulta.getData()).thenReturn(dataConsulta);

        assertDoesNotThrow(() -> validador.validar(dados));
    }
}
