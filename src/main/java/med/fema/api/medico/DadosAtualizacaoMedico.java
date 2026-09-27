package med.fema.api.medico;

import jakarta.validation.constraints.NotNull;
import med.fema.api.endereco.DadosEndereco;

public record DadosAtualizacaoMedico(
        @NotNull
        Long id,
        String nome,
        String telefone,
        DadosEndereco endereco
) {
}
