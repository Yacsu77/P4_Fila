package com.example.P4_Fila.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Entidade Colaborador - Representa funcionário responsável por atendimentos
 */
@Entity
@Table(name = "colaboradores")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Colaborador {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    
    @Column(nullable = false)
    private String nome;
    
    @Column(unique = true)
    private String cpf;
    
    private Integer idade;
    
    @Column(name = "departamento_id")
    private Long departamentoId;
    
    @Column(unique = true, nullable = false)
    private String usuario;
    
    private String senha;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "departamento_id", insertable = false, updatable = false)
    private Departamento departamento;
}

