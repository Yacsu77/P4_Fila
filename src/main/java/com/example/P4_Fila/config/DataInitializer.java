package com.example.P4_Fila.config;

import com.example.P4_Fila.factory.EntityFactory;
import com.example.P4_Fila.model.Colaborador;
import com.example.P4_Fila.model.Departamento;
import com.example.P4_Fila.repository.ColaboradorRepository;
import com.example.P4_Fila.repository.DepartamentoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * Componente que inicializa dados ao iniciar a aplicação
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {
    
    private final EntityFactory entityFactory;
    private final DepartamentoRepository departamentoRepository;
    private final ColaboradorRepository colaboradorRepository;
    
    @Override
    public void run(String... args) throws Exception {
        log.info("🚀 Iniciando cadastro de colaboradores padrão...");
        
        // Criar departamento padrão se não existir
        Departamento departamento = departamentoRepository.findAll().stream()
            .findFirst()
            .orElseGet(() -> {
                departamentoRepository.save(entityFactory.createDepartamento("Atendimento ao Cliente"));
                return departamentoRepository.findAll().get(0);
            });
        
        // Verificar se já existem colaboradores
        if (colaboradorRepository.count() == 0) {
            log.info("📝 Cadastrando colaboradores padrão...");
            
            // Colaborador 1
            Colaborador colaborador1 = entityFactory.createColaborador(
                "João Silva",
                "11122233344",
                30,
                departamento.getId(),
                "1",
                "123"
            );
            colaboradorRepository.save(colaborador1);
            log.info("✅ Colaborador 1 cadastrado - Usuário: 1");
            
            // Colaborador 2
            Colaborador colaborador2 = entityFactory.createColaborador(
                "Maria Santos",
                "22233344455",
                28,
                departamento.getId(),
                "2",
                "123"
            );
            colaboradorRepository.save(colaborador2);
            log.info("✅ Colaborador 2 cadastrado - Usuário: 2");
            
            // Colaborador 3
            Colaborador colaborador3 = entityFactory.createColaborador(
                "Pedro Oliveira",
                "33344455566",
                35,
                departamento.getId(),
                "3",
                "123"
            );
            colaboradorRepository.save(colaborador3);
            log.info("✅ Colaborador 3 cadastrado - Usuário: 3");
            
            log.info("🎉 Colaboradores cadastrados com sucesso!");
            log.info("📋 Credenciais de acesso:");
            log.info("   Usuário: 1 | Senha: 123 | Nome: João Silva");
            log.info("   Usuário: 2 | Senha: 123 | Nome: Maria Santos");
            log.info("   Usuário: 3 | Senha: 123 | Nome: Pedro Oliveira");
        } else {
            log.info("✅ Colaboradores já existem no banco de dados");
        }
    }
}

