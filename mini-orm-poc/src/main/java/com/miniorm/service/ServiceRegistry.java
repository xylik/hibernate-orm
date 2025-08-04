package com.miniorm.service;

/**
 * A registry for managing services.
 * 
 * Demonstrates dependency injection and service lifecycle management.
 */
public interface ServiceRegistry {
    
    /**
     * Get a service by its type.
     */
    <T extends Service> T getService(Class<T> serviceType);
    
    /**
     * Register a service.
     */
    <T extends Service> void registerService(Class<T> serviceType, T service);
    
    /**
     * Check if a service is registered.
     */
    boolean hasService(Class<? extends Service> serviceType);
    
    /**
     * Close the registry and release all services.
     */
    void close();
}