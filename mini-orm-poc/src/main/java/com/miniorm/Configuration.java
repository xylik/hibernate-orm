package com.miniorm;

import com.miniorm.metadata.EntityMetadataImpl;
import com.miniorm.service.ConnectionService;
import com.miniorm.service.ServiceRegistry;
import com.miniorm.service.SimpleConnectionService;
import com.miniorm.service.SimpleServiceRegistry;
import com.miniorm.annotations.Entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Configuration class for building a SessionFactory.
 * 
 * Demonstrates the builder pattern and configuration management.
 */
public class Configuration {
    
    private final Properties properties = new Properties();
    private final List<Class<?>> annotatedClasses = new ArrayList<>();
    
    /**
     * Set a configuration property.
     */
    public Configuration setProperty(String name, String value) {
        properties.setProperty(name, value);
        return this;
    }
    
    /**
     * Set multiple properties.
     */
    public Configuration setProperties(Properties properties) {
        this.properties.putAll(properties);
        return this;
    }
    
    /**
     * Add an annotated entity class.
     */
    public Configuration addAnnotatedClass(Class<?> annotatedClass) {
        if (!annotatedClass.isAnnotationPresent(Entity.class)) {
            throw new MiniOrmException("Class is not annotated with @Entity: " + annotatedClass.getName());
        }
        annotatedClasses.add(annotatedClass);
        return this;
    }
    
    /**
     * Build the SessionFactory.
     */
    public SessionFactory buildSessionFactory() {
        // Create service registry
        ServiceRegistry serviceRegistry = createServiceRegistry();
        
        // Create metamodel
        Metamodel metamodel = createMetamodel();
        
        // Create and return SessionFactory
        return new SessionFactoryImpl(serviceRegistry, metamodel);
    }
    
    private ServiceRegistry createServiceRegistry() {
        SimpleServiceRegistry registry = new SimpleServiceRegistry();
        
        // Register connection service
        SimpleConnectionService connectionService = new SimpleConnectionService();
        String url = properties.getProperty("database.url");
        String username = properties.getProperty("database.username", "");
        String password = properties.getProperty("database.password", "");
        
        if (url == null) {
            throw new MiniOrmException("database.url property is required");
        }
        
        connectionService.configure(url, username, password);
        registry.registerService(ConnectionService.class, connectionService);
        
        return registry;
    }
    
    private Metamodel createMetamodel() {
        Map<Class<?>, EntityMetadata> entityMetadata = new HashMap<>();
        
        for (Class<?> entityClass : annotatedClasses) {
            EntityMetadata metadata = new EntityMetadataImpl(entityClass);
            entityMetadata.put(entityClass, metadata);
        }
        
        return new MetamodelImpl(entityMetadata);
    }
}