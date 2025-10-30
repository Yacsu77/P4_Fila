package com.example.P4_Fila.service;

import com.example.P4_Fila.factory.EntityFactory;
import com.example.P4_Fila.model.Cliente;
import com.example.P4_Fila.repository.ClienteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ClienteService {
    
    private final ClienteRepository clienteRepository;
    private final EntityFactory entityFactory;
    
    public Cliente criarCliente(String nome, Integer idade, String cpf, String email, String senha) {
        Cliente cliente = entityFactory.createCliente(nome, idade, cpf, email, senha);
        return clienteRepository.save(cliente);
    }
    
    public List<Cliente> listarClientes() {
        return clienteRepository.findAll();
    }
    
    public Optional<Cliente> buscarPorId(Long id) {
        return clienteRepository.findById(id);
    }
    
    public Optional<Cliente> buscarPorCpf(String cpf) {
        return clienteRepository.findByCpf(cpf);
    }
    
    public Cliente atualizarCliente(Long id, Cliente clienteAtualizado) {
        return clienteRepository.findById(id)
            .map(cliente -> {
                cliente.setNome(clienteAtualizado.getNome());
                cliente.setIdade(clienteAtualizado.getIdade());
                cliente.setEmail(clienteAtualizado.getEmail());
                return clienteRepository.save(cliente);
            })
            .orElse(null);
    }
    
    public void deletarCliente(Long id) {
        clienteRepository.deleteById(id);
    }
}

