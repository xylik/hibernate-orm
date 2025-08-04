package com.miniorm.service;

import com.miniorm.MiniOrmException;

import java.util.HashMap;
import java.util.Map;

/**
 * Simple implementation of ServiceRegistry.
 * 
 * Demonstrates basic service registry pattern with dependency injection.
 */
public class SimpleServiceRegistry implements ServiceRegistry {
    
    private final Map<Class<? extends Service>, Service> services = new HashMap<>();
    private volatile boolean closed = false;
    
    @Override
    @SuppressWarnings("unchecked")
    public <T extends Service> T getService(Class<T> serviceType) {
        checkNotClosed();
        T service = (T) services.get(serviceType);
        if (service == null) {
            throw new MiniOrmException("Service not found: " + serviceType.getName());
        }
        return service;
    }
    
    @Override
    public <T extends Service> void registerService(Class<T> serviceType, T service) {
        checkNotClosed();
        services.put(serviceType, service);
    }
    
    @Override
    public boolean hasService(Class<? extends Service> serviceType) {
        checkNotClosed();
        return services.containsKey(serviceType);
    }
    
    @Override
    public void close() {
        if (!closed) {
            closed = true;
            // Close all services that implement AutoCloseable
            for (Service service : services.values()) {
                if (service instanceof AutoCloseable) {
                    try {
                        ((AutoCloseable) service).close();
                    } catch (Exception e) {
                        // Log error in real implementation
                        System.err.println("Error closing service: " + e.getMessage());
                    }
                }
            }
            services.clear();
        }
    }
    
    private void checkNotClosed() {
        if (closed) {
            throw new MiniOrmException("ServiceRegistry is closed");
        }
    }
}