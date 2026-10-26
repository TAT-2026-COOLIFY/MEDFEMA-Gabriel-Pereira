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

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ValidadorConsultaJaCanceladaTest {

    @InjectMocks
    private ValidadorConsultaJaCancelada validador;

    @Mock
    private ConsultaRepository repository;

    @Mock
    private Consulta consulta;

    @Test
    @DisplayName("Deveria lancar ValidacaoException quando a consulta ja estiver cancelada")
    void validar_consultaJaCancelada() {
        var dados = new DadosCancelamentoConsulta(1L, MotivoCancelamento.MEDICO_CANCELOU);

        when(repository.getReferenceById(1L)).thenReturn(consulta);
        when(consulta.getMotivoCancelamento()).thenReturn(MotivoCancelamento.PACIENTE_DESISTIU);

        assertThrows(ValidacaoException.class, () -> validador.validar(dados));
    }

    @Test
    @DisplayName("Nao deveria lancar excecao quando a consulta ainda nao foi cancelada")
    void validar_consultaNaoCancelada() {
        var dados = new DadosCancelamentoConsulta(1L, MotivoCancelamento.MEDICO_CANCELOU);

        when(repository.getReferenceById(1L)).thenReturn(consulta);

        assertDoesNotThrow(() -> validador.validar(dados));
    }
}
