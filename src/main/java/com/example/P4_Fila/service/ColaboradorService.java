package com.example.P4_Fila.service;

import com.example.P4_Fila.factory.EntityFactory;
import com.example.P4_Fila.model.Colaborador;
import com.example.P4_Fila.repository.ColaboradorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ColaboradorService {
    
    private final ColaboradorRepository colaboradorRepository;
    private final EntityFactory entityFactory;
    
    public Colaborador criarColaborador(String nome, String cpf, Integer idade, Long departamentoId, String usuario, String senha) {
        Colaborador colaborador = entityFactory.createColaborador(nome, cpf, idade, departamentoId, usuario, senha);
        return colaboradorRepository.save(colaborador);
    }
    
    public List<Colaborador> listarColaboradores() {
        return colaboradorRepository.findAll();
    }
    
    public Optional<Colaborador> buscarPorId(UUID id) {
        return colaboradorRepository.findById(id);
    }
    
    public List<Colaborador> buscarPorDepartamento(Long departamentoId) {
        return colaboradorRepository.findByDepartamentoId(departamentoId);
    }
}

