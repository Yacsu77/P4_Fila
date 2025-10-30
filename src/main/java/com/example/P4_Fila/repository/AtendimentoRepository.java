package com.example.P4_Fila.repository;

import com.example.P4_Fila.model.Atendimento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AtendimentoRepository extends JpaRepository<Atendimento, Long> {
    List<Atendimento> findByIdColaborador(UUID idColaborador);
    List<Atendimento> findByIdSecao(Long idSecao);
}

