package com.example.P4_Fila.service;

import com.example.P4_Fila.factory.EntityFactory;
import com.example.P4_Fila.model.Fila;
import com.example.P4_Fila.repository.FilaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FilaService {
    
    private final FilaRepository filaRepository;
    private final EntityFactory entityFactory;
    
    public Fila adicionarNaFila(Long idDepartamento, Long idSecao) {
        Fila fila = entityFactory.createFila(idDepartamento, idSecao);
        return filaRepository.save(fila);
    }
    
    public List<Fila> listarFilas() {
        return filaRepository.findAll();
    }
    
    public List<Fila> buscarPorDepartamento(Long idDepartamento) {
        return filaRepository.findByIdDepartamento(idDepartamento);
    }
    
    public List<Fila> buscarEmEspera() {
        return filaRepository.findByEsperaTrue();
    }
    
    public void removerDaFila(Long id) {
        filaRepository.findById(id).ifPresent(fila -> {
            fila.setEspera(false);
            filaRepository.save(fila);
        });
    }
}

