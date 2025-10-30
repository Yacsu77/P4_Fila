package com.example.P4_Fila.repository;

import com.example.P4_Fila.model.Fila;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FilaRepository extends JpaRepository<Fila, Long> {
    List<Fila> findByIdDepartamento(Long idDepartamento);
    List<Fila> findByEsperaTrue();
    Optional<Fila> findByIdSecaoAndEsperaTrue(Long idSecao);
}

