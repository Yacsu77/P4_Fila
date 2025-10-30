package com.example.P4_Fila.controller;

import com.example.P4_Fila.model.Colaborador;
import com.example.P4_Fila.service.ColaboradorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/colaboradores")
@RequiredArgsConstructor
public class ColaboradorController {
    
    private final ColaboradorService colaboradorService;
    
    @PostMapping
    public ResponseEntity<Colaborador> criarColaborador(@RequestBody Colaborador colaborador) {
        Colaborador novoColaborador = colaboradorService.criarColaborador(
            colaborador.getNome(),
            colaborador.getCpf(),
            colaborador.getIdade(),
            colaborador.getDepartamentoId(),
            colaborador.getUsuario(),
            colaborador.getSenha()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(novoColaborador);
    }
    
    @GetMapping
    public ResponseEntity<List<Colaborador>> listarColaboradores() {
        return ResponseEntity.ok(colaboradorService.listarColaboradores());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Colaborador> buscarColaborador(@PathVariable UUID id) {
        return colaboradorService.buscarPorId(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/departamento/{idDepartamento}")
    public ResponseEntity<List<Colaborador>> buscarPorDepartamento(@PathVariable Long idDepartamento) {
        return ResponseEntity.ok(colaboradorService.buscarPorDepartamento(idDepartamento));
    }
}

