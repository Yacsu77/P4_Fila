package com.example.P4_Fila.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes do Singleton Pattern - H2ConnectionSingleton")
class H2ConnectionSingletonTest {

    @Test
    @DisplayName("Deve retornar a mesma instância (Singleton)")
    void deveRetornarMesmaInstancia() {
        // When
        H2ConnectionSingleton instance1 = H2ConnectionSingleton.getInstance();
        H2ConnectionSingleton instance2 = H2ConnectionSingleton.getInstance();

        // Then
        assertNotNull(instance1);
        assertNotNull(instance2);
        assertSame(instance1, instance2);
    }

    @Test
    @DisplayName("Deve criar DataSource")
    void deveCriarDataSource() {
        // Given
        H2ConnectionSingleton singleton = H2ConnectionSingleton.getInstance();

        // When
        DataSource dataSource = singleton.getDataSource();

        // Then
        assertNotNull(dataSource);
    }
}

