package med.fema.api.consulta.validacoes;

import med.fema.api.consulta.DadosAgendamentoConsulta;
import med.fema.api.infra.exception.ValidacaoException;
import med.fema.api.paciente.PacienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ValidadorPacienteAtivo implements ValidadorAgendamentoDeConsulta {

    @Autowired
    private PacienteRepository repository;

    @Override
    public void validar(DadosAgendamentoConsulta dados) {
        var pacienteEstaAtivo = repository.findAtivoById(dados.idPaciente());
        if (pacienteEstaAtivo != null && !pacienteEstaAtivo) {
            throw new ValidacaoException("Consulta não pode ser agendada com paciente inativo no sistema");
        }
    }
}
