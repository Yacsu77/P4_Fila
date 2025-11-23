package com.example.P4_Fila.service;

import com.example.P4_Fila.factory.EntityFactory;
import com.example.P4_Fila.model.Secao;
import com.example.P4_Fila.repository.SecaoRepository;
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
@DisplayName("Testes do Service - SecaoService")
class SecaoServiceTest {

    @Mock
    private SecaoRepository secaoRepository;

    @Mock
    private EntityFactory entityFactory;

    @InjectMocks
    private SecaoService secaoService;

    private Secao secao;

    @BeforeEach
    void setUp() {
        secao = new Secao();
        secao.setId(1L);
        secao.setIdCliente(1L);
        secao.setSenha("NA001");
        secao.setTipoSecao("NORMAL");
        secao.setStatus("AGUARDANDO");
        secao.setAtivo(true);
    }

    @Test
    @DisplayName("Deve criar nova seção")
    void deveCriarSecao() {
        // Given
        Long idCliente = 1L;
        String tipoSecao = "NORMAL";
        when(entityFactory.createSecao(idCliente, tipoSecao)).thenReturn(secao);
        when(secaoRepository.save(any(Secao.class))).thenReturn(secao);

        // When
        Secao resultado = secaoService.criarSecao(idCliente, tipoSecao);

        // Then
        assertNotNull(resultado);
        assertEquals("NA001", resultado.getSenha());
        verify(entityFactory).createSecao(idCliente, tipoSecao);
        verify(secaoRepository).save(secao);
    }

    @Test
    @DisplayName("Deve listar todas as seções")
    void deveListarSecoes() {
        // Given
        List<Secao> secoes = Arrays.asList(secao);
        when(secaoRepository.findAll()).thenReturn(secoes);

        // When
        List<Secao> resultado = secaoService.listarSecoes();

        // Then
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        verify(secaoRepository).findAll();
    }

    @Test
    @DisplayName("Deve buscar seção por ID")
    void deveBuscarSecaoPorId() {
        // Given
        Long id = 1L;
        when(secaoRepository.findById(id)).thenReturn(Optional.of(secao));

        // When
        Optional<Secao> resultado = secaoService.buscarPorId(id);

        // Then
        assertTrue(resultado.isPresent());
        assertEquals("NA001", resultado.get().getSenha());
        verify(secaoRepository).findById(id);
    }

    @Test
    @DisplayName("Deve buscar seção por senha")
    void deveBuscarSecaoPorSenha() {
        // Given
        String senha = "NA001";
        when(secaoRepository.findBySenha(senha)).thenReturn(Optional.of(secao));

        // When
        Optional<Secao> resultado = secaoService.buscarPorSenha(senha);

        // Then
        assertTrue(resultado.isPresent());
        assertEquals("NA001", resultado.get().getSenha());
        verify(secaoRepository).findBySenha(senha);
    }

    @Test
    @DisplayName("Deve buscar seções ativas")
    void deveBuscarSecoesAtivas() {
        // Given
        List<Secao> secoesAtivas = Arrays.asList(secao);
        when(secaoRepository.findByAtivoTrue()).thenReturn(secoesAtivas);

        // When
        List<Secao> resultado = secaoService.buscarSecoesAtivas();

        // Then
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertTrue(resultado.get(0).getAtivo());
        verify(secaoRepository).findByAtivoTrue();
    }

    @Test
    @DisplayName("Deve desativar seção")
    void deveDesativarSecao() {
        // Given
        Long id = 1L;
        when(secaoRepository.findById(id)).thenReturn(Optional.of(secao));
        when(secaoRepository.save(any(Secao.class))).thenReturn(secao);

        // When
        secaoService.desativarSecao(id);

        // Then
        assertFalse(secao.getAtivo());
        verify(secaoRepository).findById(id);
        verify(secaoRepository).save(secao);
    }
}

