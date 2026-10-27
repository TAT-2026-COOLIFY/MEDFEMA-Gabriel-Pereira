package med.fema.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import med.fema.api.consulta.Consulta;
import med.fema.api.consulta.ConsultaRepository;
import med.fema.api.consulta.MotivoCancelamento;
import med.fema.api.endereco.DadosEndereco;
import med.fema.api.paciente.DadosAtualizacaoPaciente;
import med.fema.api.paciente.DadosCadastroPaciente;
import med.fema.api.paciente.Paciente;
import med.fema.api.paciente.PacienteRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class PacienteControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PacienteRepository repository;

    @MockBean
    private ConsultaRepository consultaRepository;

    private DadosEndereco dadosEndereco() {
        return new DadosEndereco(
                "Rua Nove de Julho",
                "Vila Operária",
                "19806000",
                "Assis",
                "SP",
                null,
                "50"
        );
    }

    @Test
    @DisplayName("Deveria devolver codigo HTTP 400 quando informacoes estao invalidas no cadastro do paciente")
    void cadastrar_cenarioInvalido() throws Exception {
        mvc.perform(post("/pacientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Deveria devolver codigo HTTP 200 quando informacoes estao validas no cadastro do paciente")
    void cadastrar_cenarioValido() throws Exception {
        var dadosCadastro = new DadosCadastroPaciente(
                "Mariana Souza",
                "mariana@email.com",
                "18988887777",
                "123.456.789-00",
                dadosEndereco()
        );

        when(repository.save(any())).thenReturn(new Paciente(dadosCadastro));

        mvc.perform(post("/pacientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dadosCadastro)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Deveria devolver codigo HTTP 400 indicando o campo quando o CPF ja estiver cadastrado")
    void cadastrar_cenarioCpfDuplicado() throws Exception {
        var dadosCadastro = new DadosCadastroPaciente(
                "Mariana Souza",
                "mariana@email.com",
                "18988887777",
                "123.456.789-00",
                dadosEndereco()
        );

        var causa = new SQLIntegrityConstraintViolationException("Duplicate entry '123.456.789-00' for key 'pacientes.cpf'");
        when(repository.save(any())).thenThrow(new DataIntegrityViolationException("could not execute statement", causa));

        mvc.perform(post("/pacientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dadosCadastro)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].campo").value("cpf"));
    }

    @Test
    @DisplayName("Deveria reativar o paciente inativo quando o CPF dele for cadastrado de novo, mesmo sem pontuacao")
    void cadastrar_cenarioPacienteInativo() throws Exception {
        var dadosCadastro = new DadosCadastroPaciente(
                "Mariana Souza",
                "mariana@email.com",
                "18988887777",
                "12345678900",
                dadosEndereco()
        );
        var pacienteInativo = new Paciente(dadosCadastro);
        pacienteInativo.excluir();
        when(repository.findByCpfAndAtivoFalse("123.456.789-00")).thenReturn(Optional.of(pacienteInativo));

        mvc.perform(post("/pacientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dadosCadastro)))
                .andExpect(status().isOk());

        assertTrue(pacienteInativo.getAtivo());
        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("Deveria devolver codigo HTTP 200 ao listar pacientes")
    void listar_cenarioValido() throws Exception {
        var paciente = new Paciente(new DadosCadastroPaciente("Mariana", "mariana@email.com", "18988887777", "123.456.789-00", dadosEndereco()));
        when(repository.findAllByAtivoTrue(any(Pageable.class))).thenReturn(new PageImpl<>(List.of(paciente)));

        mvc.perform(get("/pacientes"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Deveria devolver codigo HTTP 200 ao atualizar dados do paciente")
    void atualizar_cenarioValido() throws Exception {
        var paciente = new Paciente(new DadosCadastroPaciente("Mariana", "mariana@email.com", "18988887777", "123.456.789-00", dadosEndereco()));
        when(repository.getReferenceById(1L)).thenReturn(paciente);

        var dadosAtualizacao = new DadosAtualizacaoPaciente(1L, "Mariana Souza Silva", "18981112222", dadosEndereco());

        mvc.perform(put("/pacientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dadosAtualizacao)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Deveria devolver codigo HTTP 400 quando o telefone vier em branco ou o CEP for invalido na atualizacao do paciente")
    void atualizar_cenarioInvalido() throws Exception {
        var endereco = new DadosEndereco("Rua Nove de Julho", "Vila Operária", "abc", "Assis", "SP", null, "50");
        var dadosAtualizacao = new DadosAtualizacaoPaciente(1L, null, "", endereco);

        mvc.perform(put("/pacientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dadosAtualizacao)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[*].campo", containsInAnyOrder("telefone", "endereco.cep")));
    }

    @Test
    @DisplayName("Deveria devolver codigo HTTP 200 ao excluir (inativar) paciente e cancelar as consultas futuras dele")
    void excluir_cenarioValido() throws Exception {
        var paciente = new Paciente(new DadosCadastroPaciente("Mariana", "mariana@email.com", "18988887777", "123.456.789-00", dadosEndereco()));
        var consulta = mock(Consulta.class);
        when(repository.getReferenceById(1L)).thenReturn(paciente);
        when(consultaRepository.findAllByPacienteIdAndDataAfterAndMotivoCancelamentoIsNull(eq(1L), any())).thenReturn(List.of(consulta));

        mvc.perform(delete("/pacientes/1"))
                .andExpect(status().isOk());

        verify(consulta).cancelar(MotivoCancelamento.PACIENTE_DESISTIU);
    }
}
