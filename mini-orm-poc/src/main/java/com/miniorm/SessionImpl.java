package com.miniorm;

import com.miniorm.service.ConnectionService;
import com.miniorm.service.ServiceRegistry;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * Implementation of Session interface.
 * 
 * Demonstrates Unit of Work pattern and Identity Map pattern.
 */
public class SessionImpl implements Session {
    
    private final ServiceRegistry serviceRegistry;
    private final Metamodel metamodel;
    private final Map<EntityKey, Object> identityMap = new HashMap<>();
    private Transaction currentTransaction;
    private volatile boolean open = true;
    
    public SessionImpl(ServiceRegistry serviceRegistry, Metamodel metamodel) {
        this.serviceRegistry = serviceRegistry;
        this.metamodel = metamodel;
    }
    
    @Override
    public void save(Object entity) {
        checkOpen();
        
        EntityMetadata metadata = getEntityMetadata(entity.getClass());
        Connection connection = getConnection();
        
        try {
            // Simple INSERT statement
            StringBuilder sql = new StringBuilder("INSERT INTO ");
            sql.append(metadata.getTableName()).append(" (");
            
            // Build column list (excluding ID if auto-generated)
            boolean first = true;
            for (Field field : metadata.getPersistentFields()) {
                if (metadata.isIdentifierField(field)) {
                    continue; // Skip auto-generated ID
                }
                if (!first) sql.append(", ");
                sql.append(metadata.getColumnName(field));
                first = false;
            }
            
            sql.append(") VALUES (");
            
            // Add parameter placeholders
            first = true;
            for (Field field : metadata.getPersistentFields()) {
                if (metadata.isIdentifierField(field)) {
                    continue;
                }
                if (!first) sql.append(", ");
                sql.append("?");
                first = false;
            }
            sql.append(")");
            
            try (PreparedStatement stmt = connection.prepareStatement(sql.toString())) {
                int paramIndex = 1;
                for (Field field : metadata.getPersistentFields()) {
                    if (metadata.isIdentifierField(field)) {
                        continue;
                    }
                    try {
                        Object value = field.get(entity);
                        stmt.setObject(paramIndex++, value);
                    } catch (IllegalAccessException e) {
                        throw new MiniOrmException("Failed to access field value", e);
                    }
                }
                
                stmt.executeUpdate();
            }
            
            // Add to identity map (in real implementation, we'd get the generated ID)
            Object id = metadata.getIdentifierValue(entity);
            if (id != null) {
                identityMap.put(new EntityKey(entity.getClass(), id), entity);
            }
            
        } catch (SQLException e) {
            throw new MiniOrmException("Failed to save entity", e);
        } finally {
            releaseConnection(connection);
        }
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public <T> T load(Class<T> entityClass, Object id) {
        checkOpen();
        
        // Check identity map first
        EntityKey key = new EntityKey(entityClass, id);
        T entity = (T) identityMap.get(key);
        if (entity != null) {
            return entity;
        }
        
        // Load from database
        EntityMetadata metadata = getEntityMetadata(entityClass);
        Connection connection = getConnection();
        
        try {
            String sql = "SELECT * FROM " + metadata.getTableName() + 
                        " WHERE " + metadata.getColumnName(metadata.getIdentifierField()) + " = ?";
            
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setObject(1, id);
                
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        entity = createEntityFromResultSet(entityClass, rs, metadata);
                        identityMap.put(key, entity);
                        return entity;
                    } else {
                        throw new MiniOrmException("Entity not found: " + entityClass.getName() + " with id " + id);
                    }
                }
            }
        } catch (SQLException e) {
            throw new MiniOrmException("Failed to load entity", e);
        } finally {
            releaseConnection(connection);
        }
    }
    
    @Override
    public <T> T get(Class<T> entityClass, Object id) {
        try {
            return load(entityClass, id);
        } catch (MiniOrmException e) {
            // Return null instead of throwing exception
            return null;
        }
    }
    
    @Override
    public void update(Object entity) {
        checkOpen();
        // In a real implementation, this would track dirty fields
        // and generate UPDATE statements accordingly
        throw new MiniOrmException("Update not yet implemented");
    }
    
    @Override
    public void delete(Object entity) {
        checkOpen();
        
        EntityMetadata metadata = getEntityMetadata(entity.getClass());
        Object id = metadata.getIdentifierValue(entity);
        
        Connection connection = getConnection();
        try {
            String sql = "DELETE FROM " + metadata.getTableName() + 
                        " WHERE " + metadata.getColumnName(metadata.getIdentifierField()) + " = ?";
            
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setObject(1, id);
                stmt.executeUpdate();
            }
            
            // Remove from identity map
            identityMap.remove(new EntityKey(entity.getClass(), id));
            
        } catch (SQLException e) {
            throw new MiniOrmException("Failed to delete entity", e);
        } finally {
            releaseConnection(connection);
        }
    }
    
    @Override
    public Query createQuery(String queryString) {
        checkOpen();
        return new QueryImpl(queryString, serviceRegistry, metamodel);
    }
    
    @Override
    public Transaction beginTransaction() {
        checkOpen();
        if (currentTransaction != null && currentTransaction.isActive()) {
            throw new MiniOrmException("Transaction already active");
        }
        currentTransaction = new TransactionImpl(getConnection());
        return currentTransaction;
    }
    
    @Override
    public void flush() {
        checkOpen();
        // In real implementation, this would flush all pending changes
    }
    
    @Override
    public void clear() {
        checkOpen();
        identityMap.clear();
    }
    
    @Override
    public boolean isOpen() {
        return open;
    }
    
    @Override
    public void close() {
        if (open) {
            open = false;
            if (currentTransaction != null && currentTransaction.isActive()) {
                currentTransaction.rollback();
            }
            identityMap.clear();
        }
    }
    
    @SuppressWarnings("unchecked")
    private <T> T createEntityFromResultSet(Class<T> entityClass, ResultSet rs, EntityMetadata metadata) 
            throws SQLException {
        try {
            T entity = entityClass.getDeclaredConstructor().newInstance();
            
            for (Field field : metadata.getPersistentFields()) {
                String columnName = metadata.getColumnName(field);
                Object value = rs.getObject(columnName);
                field.set(entity, value);
            }
            
            return entity;
        } catch (Exception e) {
            throw new MiniOrmException("Failed to create entity from ResultSet", e);
        }
    }
    
    private EntityMetadata getEntityMetadata(Class<?> entityClass) {
        return metamodel.getEntityMetadata(entityClass);
    }
    
    private Connection getConnection() {
        return serviceRegistry.getService(ConnectionService.class).getConnection();
    }
    
    private void releaseConnection(Connection connection) {
        serviceRegistry.getService(ConnectionService.class).releaseConnection(connection);
    }
    
    private void checkOpen() {
        if (!open) {
            throw new MiniOrmException("Session is closed");
        }
    }
    
    /**
     * Key for the identity map.
     */
    private static class EntityKey {
        private final Class<?> entityClass;
        private final Object id;
        
        public EntityKey(Class<?> entityClass, Object id) {
            this.entityClass = entityClass;
            this.id = id;
        }
        
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof EntityKey)) return false;
            EntityKey entityKey = (EntityKey) o;
            return entityClass.equals(entityKey.entityClass) && id.equals(entityKey.id);
        }
        
        @Override
        public int hashCode() {
            return entityClass.hashCode() * 31 + id.hashCode();
        }
    }
}