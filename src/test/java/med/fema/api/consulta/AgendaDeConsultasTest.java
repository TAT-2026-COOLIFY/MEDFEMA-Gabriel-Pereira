package med.fema.api.consulta;

import med.fema.api.consulta.validacoes.ValidadorAgendamentoDeConsulta;
import med.fema.api.consulta.validacoes.ValidadorCancelamentoDeConsulta;
import med.fema.api.infra.exception.ValidacaoException;
import med.fema.api.medico.Especialidade;
import med.fema.api.medico.Medico;
import med.fema.api.medico.MedicoRepository;
import med.fema.api.paciente.Paciente;
import med.fema.api.paciente.PacienteRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AgendaDeConsultasTest {

    @InjectMocks
    private AgendaDeConsultas agenda;

    @Mock
    private ConsultaRepository consultaRepository;

    @Mock
    private MedicoRepository medicoRepository;

    @Mock
    private PacienteRepository pacienteRepository;

    @Spy
    private List<ValidadorAgendamentoDeConsulta> validadores = new ArrayList<>();

    @Spy
    private List<ValidadorCancelamentoDeConsulta> validadoresCancelamento = new ArrayList<>();

    @Mock
    private Consulta consulta;

    @Mock
    private Medico medico;

    @Mock
    private Paciente paciente;

    @Test
    @DisplayName("Deveria lancar excecao ao tentar cancelar consulta inexistente")
    void cancelar_consultaInexistente() {
        var dados = new DadosCancelamentoConsulta(99L, MotivoCancelamento.PACIENTE_DESISTIU);
        when(consultaRepository.existsById(99L)).thenReturn(false);

        assertThrows(ValidacaoException.class, () -> agenda.cancelar(dados));
    }

    @Test
    @DisplayName("Deveria cancelar consulta com sucesso quando id existe")
    void cancelar_sucesso() {
        var dados = new DadosCancelamentoConsulta(1L, MotivoCancelamento.PACIENTE_DESISTIU);
        when(consultaRepository.existsById(1L)).thenReturn(true);
        when(consultaRepository.getReferenceById(1L)).thenReturn(consulta);

        assertDoesNotThrow(() -> agenda.cancelar(dados));
        verify(consulta).cancelar(MotivoCancelamento.PACIENTE_DESISTIU);
    }

    @Test
    @DisplayName("Deveria agendar consulta com sucesso quando dados estao corretos")
    void agendar_sucesso() {
        var data = LocalDateTime.now().plusDays(2);
        var dados = new DadosAgendamentoConsulta(1L, 2L, data, Especialidade.CARDIOLOGIA);

        when(pacienteRepository.existsById(2L)).thenReturn(true);
        when(medicoRepository.existsById(1L)).thenReturn(true);
        when(pacienteRepository.getReferenceById(2L)).thenReturn(paciente);
        when(medicoRepository.getReferenceById(1L)).thenReturn(medico);

        var detalhamento = agenda.agendar(dados);

        assertNotNull(detalhamento);
        verify(consultaRepository).save(any(Consulta.class));
    }
}
