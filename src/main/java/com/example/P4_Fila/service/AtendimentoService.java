package com.example.P4_Fila.service;

import com.example.P4_Fila.factory.EntityFactory;
import com.example.P4_Fila.model.Atendimento;
import com.example.P4_Fila.repository.AtendimentoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AtendimentoService {
    
    private final AtendimentoRepository atendimentoRepository;
    private final EntityFactory entityFactory;
    
    public Atendimento criarAtendimento(Long idSecao, UUID idColaborador, String descricao) {
        Atendimento atendimento = entityFactory.createAtendimento(idSecao, idColaborador, descricao);
        return atendimentoRepository.save(atendimento);
    }
    
    public List<Atendimento> listarAtendimentos() {
        return atendimentoRepository.findAll();
    }
    
    public Optional<Atendimento> buscarPorId(Long id) {
        return atendimentoRepository.findById(id);
    }
    
    public List<Atendimento> buscarPorColaborador(UUID idColaborador) {
        return atendimentoRepository.findByIdColaborador(idColaborador);
    }
}

