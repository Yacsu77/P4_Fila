package com.example.P4_Fila.factory;

import com.example.P4_Fila.model.*;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Factory Pattern para criação de entidades do sistema.
 * Centraliza a lógica de criação e inicialização dos objetos.
 * 
 * Padrão: Factory
 */
@Component
public class EntityFactory {
    
    /**
     * Cria um novo Cliente
     */
    public Cliente createCliente(String nome, Integer idade, String cpf, String email, String senha) {
        Cliente cliente = new Cliente();
        cliente.setNome(nome);
        cliente.setIdade(idade);
        cliente.setCpf(cpf);
        cliente.setEmail(email);
        cliente.setSenha(senha);
        return cliente;
    }
    
    /**
     * Cria um novo Departamento
     */
    public Departamento createDepartamento(String nome) {
        Departamento departamento = new Departamento();
        departamento.setNome(nome);
        return departamento;
    }
    
    /**
     * Cria um novo Colaborador
     */
    public Colaborador createColaborador(String nome, String cpf, Integer idade, Long departamentoId, String usuario, String senha) {
        Colaborador colaborador = new Colaborador();
        // Não setar ID aqui - deixar o JPA gerar automaticamente
        colaborador.setNome(nome);
        colaborador.setCpf(cpf);
        colaborador.setIdade(idade);
        colaborador.setDepartamentoId(departamentoId);
        colaborador.setUsuario(usuario);
        colaborador.setSenha(senha);
        return colaborador;
    }
    
    /**
     * Cria uma nova Seção com senha automática baseada no tipo
     */
    public Secao createSecao(Long idCliente, String tipoSecao) {
        Secao secao = new Secao();
        secao.setIdCliente(idCliente);
        secao.setTipoSecao(tipoSecao);
        secao.setSenha(generateSenha(tipoSecao));
        secao.setAtivo(true);
        secao.setStatus("AGUARDANDO");
        return secao;
    }
    
    /**
     * Gera senha automática baseada no tipo da seção
     * Normal: NA###, Prioritária: PX###
     */
    private String generateSenha(String tipoSecao) {
        if ("PRIORITARIA".equalsIgnoreCase(tipoSecao)) {
            long timestamp = System.currentTimeMillis() % 10000;
            return String.format("PX%03d", timestamp);
        } else {
            long timestamp = System.currentTimeMillis() % 10000;
            return String.format("NA%03d", timestamp);
        }
    }
    
    /**
     * Cria um novo Atendimento
     */
    public Atendimento createAtendimento(Long idSecao, UUID idColaborador, String descricao) {
        Atendimento atendimento = new Atendimento();
        atendimento.setIdSecao(idSecao);
        atendimento.setIdColaborador(idColaborador);
        atendimento.setDescricaoAtendimento(descricao);
        return atendimento;
    }
    
    /**
     * Cria uma nova Fila
     */
    public Fila createFila(Long idDepartamento, Long idSecao) {
        Fila fila = new Fila();
        fila.setIdDepartamento(idDepartamento);
        fila.setIdSecao(idSecao);
        fila.setEspera(true);
        return fila;
    }
    
    /**
     * Gera senha personalizada (para casos especiais)
     */
    public String generateCustomSenha(String prefixo, Long numero) {
        return String.format("%s%03d", prefixo.toUpperCase(), numero);
    }
}

