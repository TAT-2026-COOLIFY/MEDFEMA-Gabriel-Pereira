package med.fema.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import med.fema.api.consulta.Consulta;
import med.fema.api.consulta.ConsultaRepository;
import med.fema.api.consulta.MotivoCancelamento;
import med.fema.api.endereco.DadosEndereco;
import med.fema.api.medico.DadosAtualizacaoMedico;
import med.fema.api.medico.DadosCadastroMedico;
import med.fema.api.medico.Especialidade;
import med.fema.api.medico.Medico;
import med.fema.api.medico.MedicoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.data.util.TypeInformation;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
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
class MedicoControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private MedicoRepository repository;

    @MockBean
    private ConsultaRepository consultaRepository;

    private DadosEndereco dadosEndereco() {
        return new DadosEndereco(
                "Rua das Flores",
                "Centro",
                "19800000",
                "Assis",
                "SP",
                "Apto 1",
                "100"
        );
    }

    @Test
    @DisplayName("Deveria devolver codigo HTTP 400 quando informacoes estao invalidas no cadastro")
    void cadastrar_cenarioInvalido() throws Exception {
        mvc.perform(post("/medicos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Deveria devolver codigo HTTP 200 quando informacoes estao validas no cadastro")
    void cadastrar_cenarioValido() throws Exception {
        var dadosCadastro = new DadosCadastroMedico(
                "Dr. Rodrigo Ferreira",
                "rodrigo@medfema.com",
                "18999998888",
                "123456",
                Especialidade.CARDIOLOGIA,
                dadosEndereco()
        );

        when(repository.save(any())).thenReturn(new Medico(dadosCadastro));

        mvc.perform(post("/medicos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dadosCadastro)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Deveria devolver codigo HTTP 400 indicando o campo quando o CRM ja estiver cadastrado")
    void cadastrar_cenarioCrmDuplicado() throws Exception {
        var dadosCadastro = new DadosCadastroMedico(
                "Dr. Rodrigo Ferreira",
                "rodrigo@medfema.com",
                "18999998888",
                "123456",
                Especialidade.CARDIOLOGIA,
                dadosEndereco()
        );

        var causa = new SQLIntegrityConstraintViolationException("Duplicate entry '123456' for key 'medicos.crm'");
        when(repository.save(any())).thenThrow(new DataIntegrityViolationException("could not execute statement", causa));

        mvc.perform(post("/medicos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dadosCadastro)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].campo").value("crm"));
    }

    @Test
    @DisplayName("Deveria devolver codigo HTTP 400 indicando o campo quando a UF nao tiver 2 letras no cadastro")
    void cadastrar_cenarioUfInvalida() throws Exception {
        var endereco = new DadosEndereco("Rua das Flores", "Centro", "19800000", "Assis", "SPX", null, null);
        var dadosCadastro = new DadosCadastroMedico(
                "Dr. Rodrigo Ferreira",
                "rodrigo@medfema.com",
                "18999998888",
                "123456",
                Especialidade.CARDIOLOGIA,
                endereco
        );

        mvc.perform(post("/medicos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dadosCadastro)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].campo").value("endereco.uf"));
    }

    @Test
    @DisplayName("Deveria devolver codigo HTTP 400 ao listar medicos ordenando por campo inexistente")
    void listar_ordenacaoInvalida() throws Exception {
        when(repository.findAllByAtivoTrue(any(Pageable.class)))
                .thenThrow(new PropertyReferenceException("xyz", TypeInformation.of(Medico.class), List.of()));

        mvc.perform(get("/medicos").param("sort", "xyz"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Deveria devolver codigo HTTP 200 ao listar medicos")
    void listar_cenarioValido() throws Exception {
        var medico = new Medico(new DadosCadastroMedico("Dr. Rodrigo", "rodrigo@email.com", "18999998888", "123456", Especialidade.CARDIOLOGIA, dadosEndereco()));
        when(repository.findAllByAtivoTrue(any(Pageable.class))).thenReturn(new PageImpl<>(List.of(medico)));

        mvc.perform(get("/medicos"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Deveria devolver codigo HTTP 200 ao atualizar dados do medico")
    void atualizar_cenarioValido() throws Exception {
        var medico = new Medico(new DadosCadastroMedico("Dr. Rodrigo", "rodrigo@email.com", "18999998888", "123456", Especialidade.CARDIOLOGIA, dadosEndereco()));
        when(repository.getReferenceById(1L)).thenReturn(medico);

        var dadosAtualizacao = new DadosAtualizacaoMedico(1L, "Dr. Rodrigo Novo", "18988887777", dadosEndereco());

        mvc.perform(put("/medicos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dadosAtualizacao)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Deveria devolver codigo HTTP 400 quando o nome vier em branco ou o CEP for invalido na atualizacao")
    void atualizar_cenarioInvalido() throws Exception {
        var endereco = new DadosEndereco("Rua das Flores", "Centro", "abc", "Assis", "SP", null, null);
        var dadosAtualizacao = new DadosAtualizacaoMedico(1L, " ", null, endereco);

        mvc.perform(put("/medicos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dadosAtualizacao)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[*].campo", containsInAnyOrder("nome", "endereco.cep")));
    }

    @Test
    @DisplayName("Deveria devolver codigo HTTP 200 ao excluir (inativar) medico e cancelar as consultas futuras dele")
    void excluir_cenarioValido() throws Exception {
        var medico = new Medico(new DadosCadastroMedico("Dr. Rodrigo", "rodrigo@email.com", "18999998888", "123456", Especialidade.CARDIOLOGIA, dadosEndereco()));
        var consulta = mock(Consulta.class);
        when(repository.getReferenceById(1L)).thenReturn(medico);
        when(consultaRepository.findAllByMedicoIdAndDataAfterAndMotivoCancelamentoIsNull(eq(1L), any())).thenReturn(List.of(consulta));

        mvc.perform(delete("/medicos/1"))
                .andExpect(status().isOk());

        verify(consulta).cancelar(MotivoCancelamento.MEDICO_CANCELOU);
    }
}
