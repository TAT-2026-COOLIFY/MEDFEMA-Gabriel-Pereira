package med.fema.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import med.fema.api.consulta.AgendaDeConsultas;
import med.fema.api.consulta.Consulta;
import med.fema.api.consulta.ConsultaRepository;
import med.fema.api.consulta.DadosAgendamentoConsulta;
import med.fema.api.consulta.DadosCancelamentoConsulta;
import med.fema.api.consulta.DadosDetalhamentoConsulta;
import med.fema.api.consulta.MotivoCancelamento;
import med.fema.api.endereco.DadosEndereco;
import med.fema.api.medico.DadosCadastroMedico;
import med.fema.api.medico.Especialidade;
import med.fema.api.medico.Medico;
import med.fema.api.paciente.DadosCadastroPaciente;
import med.fema.api.paciente.Paciente;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ConsultaControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AgendaDeConsultas agendaDeConsultas;

    @MockBean
    private ConsultaRepository consultaRepository;

    @Test
    @DisplayName("Deveria devolver codigo HTTP 200 com os nomes do medico e do paciente ao listar consultas")
    void listar_cenarioValido() throws Exception {
        var endereco = new DadosEndereco("Rua das Flores", "Centro", "19800000", "Assis", "SP", null, "100");
        var medico = new Medico(new DadosCadastroMedico("Dr. Rodrigo", "rodrigo@email.com", "18999998888", "123456", Especialidade.CARDIOLOGIA, endereco));
        var paciente = new Paciente(new DadosCadastroPaciente("Mariana Souza", "mariana@email.com", "18988887777", "123.456.789-00", endereco));
        var consulta = new Consulta(1L, medico, paciente, LocalDateTime.now().plusDays(3));
        when(consultaRepository.findAllByDataAfterAndMotivoCancelamentoIsNull(any(LocalDateTime.class), any(Pageable.class))).thenReturn(new PageImpl<>(List.of(consulta)));

        mvc.perform(get("/consultas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].nomeMedico").value("Dr. Rodrigo"))
                .andExpect(jsonPath("$.content[0].especialidade").value("CARDIOLOGIA"))
                .andExpect(jsonPath("$.content[0].nomePaciente").value("Mariana Souza"));
    }

    @Test
    @DisplayName("Deveria devolver codigo HTTP 400 quando informacoes estao invalidas no agendamento")
    void agendar_cenarioInvalido() throws Exception {
        mvc.perform(post("/consultas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Deveria devolver codigo HTTP 200 quando informacoes estao validas no agendamento")
    void agendar_cenarioValido() throws Exception {
        var data = LocalDateTime.now().plusDays(3);
        var dadosDetalhamento = new DadosDetalhamentoConsulta(10L, 2L, 5L, data);

        when(agendaDeConsultas.agendar(any())).thenReturn(dadosDetalhamento);

        var dadosAgendamento = new DadosAgendamentoConsulta(2L, 5L, data, Especialidade.CARDIOLOGIA);

        mvc.perform(post("/consultas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dadosAgendamento)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10L))
                .andExpect(jsonPath("$.idMedico").value(2L))
                .andExpect(jsonPath("$.idPaciente").value(5L));
    }

    @Test
    @DisplayName("Deveria devolver codigo HTTP 204 quando cancelamento for realizado com sucesso")
    void cancelar_cenarioValido() throws Exception {
        var dadosCancelamento = new DadosCancelamentoConsulta(1L, MotivoCancelamento.PACIENTE_DESISTIU);
        doNothing().when(agendaDeConsultas).cancelar(any());

        mvc.perform(delete("/consultas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dadosCancelamento)))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Deveria devolver codigo HTTP 400 quando motivo de cancelamento for nulo")
    void cancelar_semMotivo() throws Exception {
        mvc.perform(delete("/consultas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"idConsulta\": 1}"))
                .andExpect(status().isBadRequest());
    }
}
