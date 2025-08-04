package com.miniorm;

import com.miniorm.service.ConnectionService;
import com.miniorm.service.ServiceRegistry;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implementation of Query interface.
 * 
 * Demonstrates query processing and parameter binding.
 */
public class QueryImpl implements Query {
    
    private final String queryString;
    private final ServiceRegistry serviceRegistry;
    private final Metamodel metamodel;
    private final Map<Integer, Object> positionalParameters = new HashMap<>();
    private final Map<String, Object> namedParameters = new HashMap<>();
    private int maxResults = -1;
    private int firstResult = 0;
    
    public QueryImpl(String queryString, ServiceRegistry serviceRegistry, Metamodel metamodel) {
        this.queryString = queryString;
        this.serviceRegistry = serviceRegistry;
        this.metamodel = metamodel;
    }
    
    @Override
    public Query setParameter(int position, Object value) {
        positionalParameters.put(position, value);
        return this;
    }
    
    @Override
    public Query setParameter(String name, Object value) {
        namedParameters.put(name, value);
        return this;
    }
    
    @Override
    public Query setMaxResults(int maxResults) {
        this.maxResults = maxResults;
        return this;
    }
    
    @Override
    public Query setFirstResult(int firstResult) {
        this.firstResult = firstResult;
        return this;
    }
    
    @Override
    public List<Object> list() {
        Connection connection = getConnection();
        try {
            // In a real implementation, this would parse HQL and convert to SQL
            // For simplicity, we assume the query string is already SQL
            String sql = processQuery();
            
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                bindParameters(stmt);
                
                try (ResultSet rs = stmt.executeQuery()) {
                    List<Object> results = new ArrayList<>();
                    int count = 0;
                    
                    // Skip to first result
                    for (int i = 0; i < firstResult && rs.next(); i++) {
                        // Skip rows
                    }
                    
                    while (rs.next() && (maxResults == -1 || count < maxResults)) {
                        // Simple result mapping - just return the first column
                        results.add(rs.getObject(1));
                        count++;
                    }
                    
                    return results;
                }
            }
        } catch (SQLException e) {
            throw new MiniOrmException("Failed to execute query", e);
        } finally {
            releaseConnection(connection);
        }
    }
    
    @Override
    public Object uniqueResult() {
        List<Object> results = list();
        if (results.isEmpty()) {
            return null;
        } else if (results.size() == 1) {
            return results.get(0);
        } else {
            throw new MiniOrmException("Query returned more than one result");
        }
    }
    
    @Override
    public int executeUpdate() {
        Connection connection = getConnection();
        try {
            String sql = processQuery();
            
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                bindParameters(stmt);
                return stmt.executeUpdate();
            }
        } catch (SQLException e) {
            throw new MiniOrmException("Failed to execute update", e);
        } finally {
            releaseConnection(connection);
        }
    }
    
    private String processQuery() {
        // Simple query processing - in reality this would be much more complex
        // with proper HQL to SQL translation
        return queryString;
    }
    
    private void bindParameters(PreparedStatement stmt) throws SQLException {
        // Bind positional parameters
        for (Map.Entry<Integer, Object> entry : positionalParameters.entrySet()) {
            stmt.setObject(entry.getKey(), entry.getValue());
        }
        
        // Named parameters would require more complex parsing
        // to find and replace them in the SQL
    }
    
    private Connection getConnection() {
        return serviceRegistry.getService(ConnectionService.class).getConnection();
    }
    
    private void releaseConnection(Connection connection) {
        serviceRegistry.getService(ConnectionService.class).releaseConnection(connection);
    }
}