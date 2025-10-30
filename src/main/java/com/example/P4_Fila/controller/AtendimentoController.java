package com.example.P4_Fila.controller;

import com.example.P4_Fila.model.Atendimento;
import com.example.P4_Fila.service.AtendimentoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/atendimentos")
@RequiredArgsConstructor
public class AtendimentoController {
    
    private final AtendimentoService atendimentoService;
    
    @PostMapping
    public ResponseEntity<Atendimento> criarAtendimento(@RequestBody Atendimento atendimento) {
        Atendimento novoAtendimento = atendimentoService.criarAtendimento(
            atendimento.getIdSecao(),
            atendimento.getIdColaborador(),
            atendimento.getDescricaoAtendimento()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(novoAtendimento);
    }
    
    @GetMapping
    public ResponseEntity<List<Atendimento>> listarAtendimentos() {
        return ResponseEntity.ok(atendimentoService.listarAtendimentos());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Atendimento> buscarAtendimento(@PathVariable Long id) {
        return atendimentoService.buscarPorId(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/colaborador/{idColaborador}")
    public ResponseEntity<List<Atendimento>> buscarPorColaborador(@PathVariable UUID idColaborador) {
        return ResponseEntity.ok(atendimentoService.buscarPorColaborador(idColaborador));
    }
}

