package com.example.P4_Fila.service;

import com.example.P4_Fila.factory.EntityFactory;
import com.example.P4_Fila.model.Secao;
import com.example.P4_Fila.repository.SecaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SecaoService {
    
    private final SecaoRepository secaoRepository;
    private final EntityFactory entityFactory;
    
    public Secao criarSecao(Long idCliente, String tipoSecao) {
        Secao secao = entityFactory.createSecao(idCliente, tipoSecao);
        return secaoRepository.save(secao);
    }
    
    public List<Secao> listarSecoes() {
        return secaoRepository.findAll();
    }
    
    public Optional<Secao> buscarPorId(Long id) {
        return secaoRepository.findById(id);
    }
    
    public Optional<Secao> buscarPorSenha(String senha) {
        return secaoRepository.findBySenha(senha);
    }
    
    public List<Secao> buscarSecoesAtivas() {
        return secaoRepository.findByAtivoTrue();
    }
    
    public void desativarSecao(Long id) {
        secaoRepository.findById(id).ifPresent(secao -> {
            secao.setAtivo(false);
            secaoRepository.save(secao);
        });
    }
}

