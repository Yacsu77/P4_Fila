package com.example.P4_Fila.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Entidade Atendimento - Registra atendimentos realizados
 */
@Entity
@Table(name = "atendimentos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Atendimento {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "id_secao")
    private Long idSecao;
    
    @Column(name = "id_colaborador")
    private UUID idColaborador;
    
    @Column(name = "descricao_atendimento", columnDefinition = "TEXT")
    private String descricaoAtendimento;
    
    @Column(name = "status")
    private String status; // "CHAMADA", "EM_ATENDIMENTO", "FINALIZADA", "TRANSFERIDA"
    
    @Column(name = "departamento_origem_id")
    private Long departamentoOrigemId;
    
    @Column(name = "departamento_destino_id")
    private Long departamentoDestinoId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_secao", insertable = false, updatable = false)
    private Secao secao;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_colaborador", insertable = false, updatable = false)
    private Colaborador colaborador;
}

