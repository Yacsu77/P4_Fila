package com.example.P4_Fila.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entidade Seção - Representa a "senha" ou sessão de atendimento
 */
@Entity
@Table(name = "secoes")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Secao {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "id_cliente")
    private Long idCliente;
    
    @Column(unique = true)
    private String senha;
    
    @Column(name = "tipo_secao")
    private String tipoSecao; // "NORMAL" ou "PRIORITARIA"
    
    private Boolean ativo;
    
    @Column(name = "status")
    private String status; // "AGUARDANDO", "CHAMADA", "EM_ATENDIMENTO", "FINALIZADA", "TRANSFERIDA"
    
    @Column(name = "guiche_chamada")
    private String guicheChamada; // Nome ou identificador do guichê que chamou
    
    @Column(name = "colaborador_chamada")
    private String colaboradorChamada; // Nome do colaborador que chamou
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_cliente", insertable = false, updatable = false)
    private Cliente cliente;
}

