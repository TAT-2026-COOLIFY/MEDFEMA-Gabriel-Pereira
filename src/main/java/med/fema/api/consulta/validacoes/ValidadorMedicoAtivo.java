package med.fema.api.consulta.validacoes;

import med.fema.api.consulta.DadosAgendamentoConsulta;
import med.fema.api.infra.exception.ValidacaoException;
import med.fema.api.medico.MedicoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ValidadorMedicoAtivo implements ValidadorAgendamentoDeConsulta {

    @Autowired
    private MedicoRepository repository;

    @Override
    public void validar(DadosAgendamentoConsulta dados) {
        if (dados.idMedico() == null) {
            return;
        }

        var medicoEstaAtivo = repository.findAtivoById(dados.idMedico());
        if (medicoEstaAtivo != null && !medicoEstaAtivo) {
            throw new ValidacaoException("Consulta não pode ser agendada com médico inativo no sistema");
        }
    }
}
