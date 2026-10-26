package med.fema.api.consulta.validacoes;

import med.fema.api.consulta.ConsultaRepository;
import med.fema.api.consulta.DadosCancelamentoConsulta;
import med.fema.api.infra.exception.ValidacaoException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ValidadorConsultaJaCancelada implements ValidadorCancelamentoDeConsulta {

    @Autowired
    private ConsultaRepository repository;

    @Override
    public void validar(DadosCancelamentoConsulta dados) {
        var consulta = repository.getReferenceById(dados.idConsulta());
        if (consulta.getMotivoCancelamento() != null) {
            throw new ValidacaoException("Consulta já está cancelada!");
        }
    }
}
