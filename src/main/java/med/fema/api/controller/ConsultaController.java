package med.fema.api.controller;

import jakarta.validation.Valid;
import med.fema.api.consulta.AgendaDeConsultas;
import med.fema.api.consulta.ConsultaRepository;
import med.fema.api.consulta.DadosAgendamentoConsulta;
import med.fema.api.consulta.DadosCancelamentoConsulta;
import med.fema.api.consulta.DadosListagemConsulta;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("consultas")
public class ConsultaController {

    @Autowired
    private AgendaDeConsultas agenda;

    @Autowired
    private ConsultaRepository consultaRepository;

    @GetMapping
    public Page<DadosListagemConsulta> listar(@PageableDefault(size = 10, sort = {"data"}) Pageable paginacao) {
        return consultaRepository.findAllByMotivoCancelamentoIsNull(paginacao).map(DadosListagemConsulta::new);
    }

    @PostMapping
    @Transactional
    public ResponseEntity agendar(@RequestBody @Valid DadosAgendamentoConsulta dados) {
        var dto = agenda.agendar(dados);
        return ResponseEntity.ok(dto);
    }

    @DeleteMapping
    @Transactional
    public ResponseEntity cancelar(@RequestBody @Valid DadosCancelamentoConsulta dados) {
        agenda.cancelar(dados);
        return ResponseEntity.noContent().build();
    }
}
