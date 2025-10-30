package com.example.P4_Fila.repository;

import com.example.P4_Fila.model.Colaborador;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ColaboradorRepository extends JpaRepository<Colaborador, UUID> {
    Optional<Colaborador> findByCpf(String cpf);
    Optional<Colaborador> findByUsuario(String usuario);
    List<Colaborador> findByDepartamentoId(Long departamentoId);
}

