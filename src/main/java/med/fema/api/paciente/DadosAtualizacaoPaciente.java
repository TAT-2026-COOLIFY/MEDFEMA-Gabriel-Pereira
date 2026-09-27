package med.fema.api.paciente;

import jakarta.validation.constraints.NotNull;
import med.fema.api.endereco.DadosEndereco;

public record DadosAtualizacaoPaciente(
        @NotNull
        Long id,
        String nome,
        String telefone,
        DadosEndereco endereco
) {
}
