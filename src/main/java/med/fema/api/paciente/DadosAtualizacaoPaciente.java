package med.fema.api.paciente;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import med.fema.api.endereco.DadosEndereco;

// Os campos são opcionais (null mantém o valor atual), mas se vierem não podem estar em branco.
public record DadosAtualizacaoPaciente(
        @NotNull
        Long id,

        @Pattern(regexp = ".*\\S.*", message = "não deve estar em branco")
        @Size(max = 100)
        String nome,

        @Pattern(regexp = ".*\\S.*", message = "não deve estar em branco")
        @Size(max = 20)
        String telefone,

        @Valid
        DadosEndereco endereco
) {
}
