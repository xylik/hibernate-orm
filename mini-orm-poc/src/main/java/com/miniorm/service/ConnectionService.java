package com.miniorm.service;

import java.sql.Connection;

/**
 * Service for managing database connections.
 * 
 * Demonstrates connection management and resource lifecycle.
 */
public interface ConnectionService extends Service {
    
    /**
     * Get a database connection.
     */
    Connection getConnection();
    
    /**
     * Release a database connection.
     */
    void releaseConnection(Connection connection);
    
    /**
     * Configure the connection service with database URL and credentials.
     */
    void configure(String url, String username, String password);
}