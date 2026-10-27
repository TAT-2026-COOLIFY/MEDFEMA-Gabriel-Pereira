package med.fema.api.paciente;

import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface PacienteRepository extends JpaRepository<Paciente, Long> {
    Page<Paciente> findAllByAtivoTrue(Pageable paginacao);

    Optional<Paciente> findByCpfAndAtivoFalse(String cpf);

    @Query("""
            select p.ativo
            from Paciente p
            where p.id = :id
            """)
    Boolean findAtivoById(Long id);

    // Trava usada no agendamento (select ... for update), liberada no fim da transação.
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from Paciente p where p.id = :id")
    Paciente travarParaAgendamento(Long id);
}
