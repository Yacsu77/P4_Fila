package com.example.P4_Fila.controller;

import com.example.P4_Fila.model.Departamento;
import com.example.P4_Fila.service.DepartamentoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/departamentos")
@RequiredArgsConstructor
public class DepartamentoController {
    
    private final DepartamentoService departamentoService;
    
    @PostMapping
    public ResponseEntity<Departamento> criarDepartamento(@RequestBody Departamento departamento) {
        Departamento novoDepartamento = departamentoService.criarDepartamento(departamento.getNome());
        return ResponseEntity.status(HttpStatus.CREATED).body(novoDepartamento);
    }
    
    @GetMapping
    public ResponseEntity<List<Departamento>> listarDepartamentos() {
        return ResponseEntity.ok(departamentoService.listarDepartamentos());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Departamento> buscarDepartamento(@PathVariable Long id) {
        return departamentoService.buscarPorId(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
}

