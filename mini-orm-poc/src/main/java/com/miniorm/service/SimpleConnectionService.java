package com.miniorm.service;

import com.miniorm.MiniOrmException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Simple implementation of ConnectionService using JDBC DriverManager.
 * 
 * In a real implementation, this would use connection pooling.
 */
public class SimpleConnectionService implements ConnectionService {
    
    private String url;
    private String username;
    private String password;
    
    @Override
    public void configure(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }
    
    @Override
    public Connection getConnection() {
        if (url == null) {
            throw new MiniOrmException("ConnectionService not configured");
        }
        
        try {
            return DriverManager.getConnection(url, username, password);
        } catch (SQLException e) {
            throw new MiniOrmException("Failed to get database connection", e);
        }
    }
    
    @Override
    public void releaseConnection(Connection connection) {
        if (connection != null) {
            try {
                if (!connection.isClosed()) {
                    connection.close();
                }
            } catch (SQLException e) {
                // Log error in real implementation
                System.err.println("Error closing connection: " + e.getMessage());
            }
        }
    }
}