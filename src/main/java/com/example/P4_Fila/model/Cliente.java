package com.example.P4_Fila.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entidade Cliente - Representa o usuário que solicita atendimento
 */
@Entity
@Table(name = "clientes")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Cliente {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String nome;
    
    private Integer idade;
    
    @Column(unique = true)
    private String cpf;
    
    @Column(unique = true)
    private String email;
    
    private String senha;
}

