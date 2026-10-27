package med.fema.api.medico;

import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface MedicoRepository extends JpaRepository<Medico, Long> {
    Page<Medico> findAllByAtivoTrue(Pageable paginacao);

    Optional<Medico> findByCrmAndAtivoFalse(String crm);

    // Travas usadas no agendamento (select ... for update), liberadas no fim da transação.
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select m from Medico m where m.id = :id")
    Medico travarParaAgendamento(Long id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select m from Medico m where m.especialidade = :especialidade")
    List<Medico> travarMedicosDaEspecialidade(Especialidade especialidade);

    @Query("""
            select m from Medico m
            where
            m.ativo = true
            and
            m.especialidade = :especialidade
            and
            m.id not in(
                select c.medico.id from Consulta c
                where
                c.data between :inicio and :fim
                and
                c.motivoCancelamento is null
            )
            order by rand()
            limit 1
            """)
    Medico escolherMedicoAleatorioLivreNaData(Especialidade especialidade, LocalDateTime inicio, LocalDateTime fim);

    @Query("""
            select m.ativo
            from Medico m
            where
            m.id = :id
            """)
    Boolean findAtivoById(Long id);
}
