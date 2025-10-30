package com.example.P4_Fila.controller;

import com.example.P4_Fila.model.Fila;
import com.example.P4_Fila.service.FilaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/filas")
@RequiredArgsConstructor
public class FilaController {
    
    private final FilaService filaService;
    
    @PostMapping
    public ResponseEntity<Fila> adicionarNaFila(@RequestBody Fila fila) {
        Fila novaFila = filaService.adicionarNaFila(fila.getIdDepartamento(), fila.getIdSecao());
        return ResponseEntity.status(HttpStatus.CREATED).body(novaFila);
    }
    
    @GetMapping
    public ResponseEntity<List<Fila>> listarFilas() {
        return ResponseEntity.ok(filaService.listarFilas());
    }
    
    @GetMapping("/departamento/{idDepartamento}")
    public ResponseEntity<List<Fila>> buscarPorDepartamento(@PathVariable Long idDepartamento) {
        return ResponseEntity.ok(filaService.buscarPorDepartamento(idDepartamento));
    }
    
    @GetMapping("/espera")
    public ResponseEntity<List<Fila>> buscarEmEspera() {
        return ResponseEntity.ok(filaService.buscarEmEspera());
    }
    
    @PutMapping("/{id}/remover")
    public ResponseEntity<Void> removerDaFila(@PathVariable Long id) {
        filaService.removerDaFila(id);
        return ResponseEntity.ok().build();
    }
}

