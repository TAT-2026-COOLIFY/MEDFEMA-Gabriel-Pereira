package med.fema.api.infra.exception;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
public class TratadorDeErros {

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity tratarErro404() {
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity tratarErro400(MethodArgumentNotValidException ex) {
        var erros = ex.getFieldErrors().stream().map(DadosErroValidacao::new).toList();
        return ResponseEntity.badRequest().body(erros);
    }

    // E-mail, CRM e CPF são únicos no banco, e o MySQL informa a coluna na mensagem,
    // ex.: "Duplicate entry '123456' for key 'medicos.crm'".
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity tratarErroDuplicidade(DataIntegrityViolationException ex) {
        var mensagem = String.valueOf(ex.getMostSpecificCause().getMessage());
        for (var campo : List.of("email", "crm", "cpf")) {
            if (mensagem.contains("." + campo + "'")) {
                return ResponseEntity.badRequest().body(List.of(new DadosErroValidacao(campo, "já está cadastrado")));
            }
        }
        return ResponseEntity.badRequest().body("Os dados informados violam uma restrição do banco de dados.");
    }

    @ExceptionHandler(ValidacaoException.class)
    public ResponseEntity tratarErroRegraDeNegocio(ValidacaoException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    private record DadosErroValidacao(String campo, String mensagem) {

        public DadosErroValidacao(FieldError erro) {
            this(erro.getField(), erro.getDefaultMessage());
        }
    }
}
