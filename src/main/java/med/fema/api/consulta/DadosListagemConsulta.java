package med.fema.api.consulta;

import med.fema.api.medico.Especialidade;

import java.time.LocalDateTime;

public record DadosListagemConsulta(
        Long id,
        LocalDateTime data,
        Long idMedico,
        String nomeMedico,
        Especialidade especialidade,
        Long idPaciente,
        String nomePaciente
) {

    public DadosListagemConsulta(Consulta consulta) {
        this(consulta.getId(), consulta.getData(),
                consulta.getMedico().getId(), consulta.getMedico().getNome(), consulta.getMedico().getEspecialidade(),
                consulta.getPaciente().getId(), consulta.getPaciente().getNome());
    }
}
