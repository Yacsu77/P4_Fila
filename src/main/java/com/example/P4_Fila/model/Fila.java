package com.example.P4_Fila.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entidade Fila - Controla o fluxo de espera de seções
 */
@Entity
@Table(name = "filas")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Fila {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "id_departamento")
    private Long idDepartamento;
    
    @Column(name = "id_secao")
    private Long idSecao;
    
    private Boolean espera;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_departamento", insertable = false, updatable = false)
    private Departamento departamento;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_secao", insertable = false, updatable = false)
    private Secao secao;
}

