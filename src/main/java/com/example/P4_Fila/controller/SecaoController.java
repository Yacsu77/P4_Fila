package com.example.P4_Fila.controller;

import com.example.P4_Fila.model.Secao;
import com.example.P4_Fila.service.SecaoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/secoes")
@RequiredArgsConstructor
public class SecaoController {
    
    private final SecaoService secaoService;
    
    @PostMapping
    public ResponseEntity<Secao> criarSecao(@RequestBody Secao secao) {
        Secao novaSecao = secaoService.criarSecao(secao.getIdCliente(), secao.getTipoSecao());
        return ResponseEntity.status(HttpStatus.CREATED).body(novaSecao);
    }
    
    @GetMapping
    public ResponseEntity<List<Secao>> listarSecoes() {
        return ResponseEntity.ok(secaoService.listarSecoes());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Secao> buscarSecao(@PathVariable Long id) {
        return secaoService.buscarPorId(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/ativas")
    public ResponseEntity<List<Secao>> buscarSecoesAtivas() {
        return ResponseEntity.ok(secaoService.buscarSecoesAtivas());
    }
    
    @PutMapping("/{id}/desativar")
    public ResponseEntity<Void> desativarSecao(@PathVariable Long id) {
        secaoService.desativarSecao(id);
        return ResponseEntity.ok().build();
    }
}

