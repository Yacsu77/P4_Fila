package com.example.P4_Fila.service;

import com.example.P4_Fila.factory.EntityFactory;
import com.example.P4_Fila.model.Cliente;
import com.example.P4_Fila.repository.ClienteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes do Service - ClienteService")
class ClienteServiceTest {

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private EntityFactory entityFactory;

    @InjectMocks
    private ClienteService clienteService;

    private Cliente cliente;

    @BeforeEach
    void setUp() {
        cliente = new Cliente();
        cliente.setId(1L);
        cliente.setNome("João Silva");
        cliente.setIdade(35);
        cliente.setCpf("12345678900");
        cliente.setEmail("joao@email.com");
        cliente.setSenha("1234");
    }

    @Test
    @DisplayName("Deve criar novo cliente")
    void deveCriarCliente() {
        // Given
        when(entityFactory.createCliente(anyString(), anyInt(), anyString(), anyString(), anyString()))
            .thenReturn(cliente);
        when(clienteRepository.save(any(Cliente.class))).thenReturn(cliente);

        // When
        Cliente resultado = clienteService.criarCliente("João Silva", 35, "12345678900", "joao@email.com", "1234");

        // Then
        assertNotNull(resultado);
        assertEquals("João Silva", resultado.getNome());
        verify(entityFactory).createCliente(anyString(), anyInt(), anyString(), anyString(), anyString());
        verify(clienteRepository).save(cliente);
    }

    @Test
    @DisplayName("Deve listar todos os clientes")
    void deveListarClientes() {
        // Given
        List<Cliente> clientes = Arrays.asList(cliente);
        when(clienteRepository.findAll()).thenReturn(clientes);

        // When
        List<Cliente> resultado = clienteService.listarClientes();

        // Then
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        verify(clienteRepository).findAll();
    }

    @Test
    @DisplayName("Deve buscar cliente por ID")
    void deveBuscarClientePorId() {
        // Given
        Long id = 1L;
        when(clienteRepository.findById(id)).thenReturn(Optional.of(cliente));

        // When
        Optional<Cliente> resultado = clienteService.buscarPorId(id);

        // Then
        assertTrue(resultado.isPresent());
        assertEquals("João Silva", resultado.get().getNome());
        verify(clienteRepository).findById(id);
    }

    @Test
    @DisplayName("Deve atualizar cliente")
    void deveAtualizarCliente() {
        // Given
        Long id = 1L;
        Cliente clienteAtualizado = new Cliente();
        clienteAtualizado.setNome("João Silva Atualizado");
        clienteAtualizado.setIdade(36);
        
        when(clienteRepository.findById(id)).thenReturn(Optional.of(cliente));
        when(clienteRepository.save(any(Cliente.class))).thenReturn(cliente);

        // When
        Cliente resultado = clienteService.atualizarCliente(id, clienteAtualizado);

        // Then
        assertNotNull(resultado);
        verify(clienteRepository).findById(id);
        verify(clienteRepository).save(any(Cliente.class));
    }

    @Test
    @DisplayName("Deve deletar cliente")
    void deveDeletarCliente() {
        // Given
        Long id = 1L;
        doNothing().when(clienteRepository).deleteById(id);

        // When
        clienteService.deletarCliente(id);

        // Then
        verify(clienteRepository).deleteById(id);
    }
}

