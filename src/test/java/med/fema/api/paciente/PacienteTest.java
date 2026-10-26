package med.fema.api.paciente;

import med.fema.api.endereco.DadosEndereco;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PacienteTest {

    @Test
    @DisplayName("Deveria gravar o CPF no formato 000.000.000-00 mesmo quando informado sem pontuacao")
    void cadastrar_cpfSemPontuacao() {
        var endereco = new DadosEndereco("Rua Nove de Julho", "Vila Operária", "19806000", "Assis", "SP", null, "50");
        var paciente = new Paciente(new DadosCadastroPaciente("Mariana", "mariana@email.com", "18988887777", "12345678900", endereco));

        assertEquals("123.456.789-00", paciente.getCpf());
    }
}
