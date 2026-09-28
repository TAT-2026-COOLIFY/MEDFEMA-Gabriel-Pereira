package med.fema.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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
    @DisplayName("Deveria devolver codigo HTTP 200 ao excluir (inativar) medico")
    void excluir_cenarioValido() throws Exception {
        var medico = new Medico(new DadosCadastroMedico("Dr. Rodrigo", "rodrigo@email.com", "18999998888", "123456", Especialidade.CARDIOLOGIA, dadosEndereco()));
        when(repository.getReferenceById(1L)).thenReturn(medico);

        mvc.perform(delete("/medicos/1"))
                .andExpect(status().isOk());
    }
}
