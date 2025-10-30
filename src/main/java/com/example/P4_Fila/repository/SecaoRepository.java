package com.example.P4_Fila.repository;

import com.example.P4_Fila.model.Secao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SecaoRepository extends JpaRepository<Secao, Long> {
    Optional<Secao> findBySenha(String senha);
    List<Secao> findByIdCliente(Long idCliente);
    List<Secao> findByAtivoTrue();
}

