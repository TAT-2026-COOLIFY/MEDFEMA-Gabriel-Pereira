package med.fema.api.endereco;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

// Os @Size seguem o tamanho das colunas no banco.
public record DadosEndereco(
        @NotBlank
        @Size(max = 100)
        String logradouro,
        @NotBlank
        @Size(max = 100)
        String bairro,
        @NotBlank
        @Pattern(regexp = "\\d{8}")
        String cep,
        @NotBlank
        @Size(max = 100)
        String cidade,
        @NotBlank
        @Pattern(regexp = "[A-Za-z]{2}", message = "deve ter 2 letras")
        String uf,
        @Size(max = 100)
        String complemento,
        @Size(max = 20)
        String numero) {
}
