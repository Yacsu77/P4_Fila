package com.example.P4_Fila.factory;

import com.example.P4_Fila.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes da Factory Pattern - EntityFactory")
class EntityFactoryTest {

    private EntityFactory factory;

    @BeforeEach
    void setUp() {
        factory = new EntityFactory();
    }

    @Test
    @DisplayName("Deve criar cliente com todos os atributos")
    void deveCriarCliente() {
        // Given
        String nome = "João Silva";
        Integer idade = 35;
        String cpf = "12345678900";
        String email = "joao@email.com";
        String senha = "1234";

        // When
        Cliente cliente = factory.createCliente(nome, idade, cpf, email, senha);

        // Then
        assertNotNull(cliente);
        assertEquals(nome, cliente.getNome());
        assertEquals(idade, cliente.getIdade());
        assertEquals(cpf, cliente.getCpf());
        assertEquals(email, cliente.getEmail());
        assertEquals(senha, cliente.getSenha());
    }

    @Test
    @DisplayName("Deve criar departamento")
    void deveCriarDepartamento() {
        // Given
        String nome = "Atendimento ao Cliente";

        // When
        Departamento departamento = factory.createDepartamento(nome);

        // Then
        assertNotNull(departamento);
        assertEquals(nome, departamento.getNome());
    }

    @Test
    @DisplayName("Deve criar colaborador")
    void deveCriarColaborador() {
        // Given
        String nome = "Maria Santos";
        String cpf = "98765432100";
        Integer idade = 28;
        Long departamentoId = 1L;
        String usuario = "maria";
        String senha = "123";

        // When
        Colaborador colaborador = factory.createColaborador(nome, cpf, idade, departamentoId, usuario, senha);

        // Then
        assertNotNull(colaborador);
        assertEquals(nome, colaborador.getNome());
        assertEquals(cpf, colaborador.getCpf());
        assertEquals(idade, colaborador.getIdade());
        assertEquals(departamentoId, colaborador.getDepartamentoId());
        assertEquals(usuario, colaborador.getUsuario());
        assertEquals(senha, colaborador.getSenha());
    }

    @Test
    @DisplayName("Deve criar seção normal com senha NA###")
    void deveCriarSecaoNormal() {
        // Given
        Long idCliente = 1L;
        String tipoSecao = "NORMAL";

        // When
        Secao secao = factory.createSecao(idCliente, tipoSecao);

        // Then
        assertNotNull(secao);
        assertEquals(idCliente, secao.getIdCliente());
        assertEquals(tipoSecao, secao.getTipoSecao());
        assertTrue(secao.getSenha().startsWith("NA"));
        assertTrue(secao.getAtivo());
        assertEquals("AGUARDANDO", secao.getStatus());
    }

    @Test
    @DisplayName("Deve criar seção prioritária com senha PX###")
    void deveCriarSecaoPrioritaria() {
        // Given
        Long idCliente = 1L;
        String tipoSecao = "PRIORITARIA";

        // When
        Secao secao = factory.createSecao(idCliente, tipoSecao);

        // Then
        assertNotNull(secao);
        assertEquals(idCliente, secao.getIdCliente());
        assertEquals(tipoSecao, secao.getTipoSecao());
        assertTrue(secao.getSenha().startsWith("PX"));
        assertTrue(secao.getAtivo());
        assertEquals("AGUARDANDO", secao.getStatus());
    }

    @Test
    @DisplayName("Deve criar atendimento")
    void deveCriarAtendimento() {
        // Given
        Long idSecao = 1L;
        UUID idColaborador = UUID.randomUUID();
        String descricao = "Atendimento realizado com sucesso";

        // When
        Atendimento atendimento = factory.createAtendimento(idSecao, idColaborador, descricao);

        // Then
        assertNotNull(atendimento);
        assertEquals(idSecao, atendimento.getIdSecao());
        assertEquals(idColaborador, atendimento.getIdColaborador());
        assertEquals(descricao, atendimento.getDescricaoAtendimento());
    }

    @Test
    @DisplayName("Deve criar fila")
    void deveCriarFila() {
        // Given
        Long idDepartamento = 1L;
        Long idSecao = 1L;

        // When
        Fila fila = factory.createFila(idDepartamento, idSecao);

        // Then
        assertNotNull(fila);
        assertEquals(idDepartamento, fila.getIdDepartamento());
        assertEquals(idSecao, fila.getIdSecao());
        assertTrue(fila.getEspera());
    }
}

