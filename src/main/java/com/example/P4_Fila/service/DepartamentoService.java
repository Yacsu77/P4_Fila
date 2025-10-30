package com.example.P4_Fila.service;

import com.example.P4_Fila.factory.EntityFactory;
import com.example.P4_Fila.model.Departamento;
import com.example.P4_Fila.repository.DepartamentoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DepartamentoService {
    
    private final DepartamentoRepository departamentoRepository;
    private final EntityFactory entityFactory;
    
    public Departamento criarDepartamento(String nome) {
        Departamento departamento = entityFactory.createDepartamento(nome);
        return departamentoRepository.save(departamento);
    }
    
    public List<Departamento> listarDepartamentos() {
        return departamentoRepository.findAll();
    }
    
    public Optional<Departamento> buscarPorId(Long id) {
        return departamentoRepository.findById(id);
    }
}

