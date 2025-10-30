package com.example.P4_Fila.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Singleton para gerenciar a conexão com o banco H2.
 * Garante que apenas uma instância de conexão seja criada e reutilizada.
 * 
 * Padrão: Singleton
 */
@Component
public class H2ConnectionSingleton {
    
    private static H2ConnectionSingleton instance;
    private DataSource dataSource;
    private Connection connection;
    
    @Value("${spring.datasource.url}")
    private String url;
    
    @Value("${spring.datasource.username}")
    private String username;
    
    @Value("${spring.datasource.password}")
    private String password;
    
    private H2ConnectionSingleton() {
        // Construtor privado para evitar instanciação direta
    }
    
    /**
     * Retorna a instância única do Singleton (Thread-safe)
     */
    public static synchronized H2ConnectionSingleton getInstance() {
        if (instance == null) {
            instance = new H2ConnectionSingleton();
        }
        return instance;
    }
    
    /**
     * Cria ou retorna a conexão do H2
     */
    public DataSource getDataSource() {
        if (dataSource == null) {
            dataSource = new SimpleDataSource();
        }
        return dataSource;
    }
    
    /**
     * Classe interna para gerenciar DataSource
     */
    private class SimpleDataSource implements DataSource {
        @Override
        public Connection getConnection() throws SQLException {
            return H2ConnectionSingleton.this.getConnection();
        }
        
        @Override
        public Connection getConnection(String username, String password) throws SQLException {
            return DriverManager.getConnection(url, username, password);
        }
        
        // Implementações vazias para outros métodos da interface
        @Override
        public java.io.PrintWriter getLogWriter() throws SQLException { return null; }
        @Override
        public void setLogWriter(java.io.PrintWriter out) throws SQLException {}
        @Override
        public int getLoginTimeout() throws SQLException { return 0; }
        @Override
        public void setLoginTimeout(int seconds) throws SQLException {}
        @Override
        public java.util.logging.Logger getParentLogger() { return null; }
        @Override
        public <T> T unwrap(Class<T> iface) throws SQLException { return null; }
        @Override
        public boolean isWrapperFor(Class<?> iface) throws SQLException { return false; }
    }
    
    /**
     * Retorna a conexão ativa com o banco de dados
     */
    public Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(url, username, password);
        }
        return connection;
    }
    
    /**
     * Fecha a conexão atual (se existir)
     */
    public void closeConnection() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
            connection = null;
        }
    }
    
    /**
     * Verifica se a conexão está ativa
     */
    public boolean isConnectionActive() throws SQLException {
        return connection != null && !connection.isClosed();
    }
}

