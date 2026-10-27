package med.fema.api.consulta;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface ConsultaRepository extends JpaRepository<Consulta, Long> {
    boolean existsByPacienteIdAndDataBetweenAndMotivoCancelamentoIsNull(Long idPaciente, LocalDateTime primeiroHorario, LocalDateTime ultimoHorario);

    boolean existsByMedicoIdAndDataBetweenAndMotivoCancelamentoIsNull(Long idMedico, LocalDateTime inicio, LocalDateTime fim);

    Page<Consulta> findAllByDataAfterAndMotivoCancelamentoIsNull(LocalDateTime data, Pageable paginacao);

    List<Consulta> findAllByMedicoIdAndDataAfterAndMotivoCancelamentoIsNull(Long idMedico, LocalDateTime data);

    List<Consulta> findAllByPacienteIdAndDataAfterAndMotivoCancelamentoIsNull(Long idPaciente, LocalDateTime data);
}
