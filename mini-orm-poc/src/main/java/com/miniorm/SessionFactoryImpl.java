package com.miniorm;

import com.miniorm.service.ServiceRegistry;

/**
 * Implementation of SessionFactory.
 * 
 * Demonstrates factory pattern and resource management.
 */
public class SessionFactoryImpl implements SessionFactory {
    
    private final ServiceRegistry serviceRegistry;
    private final Metamodel metamodel;
    private volatile boolean closed = false;
    
    public SessionFactoryImpl(ServiceRegistry serviceRegistry, Metamodel metamodel) {
        this.serviceRegistry = serviceRegistry;
        this.metamodel = metamodel;
    }
    
    @Override
    public Session openSession() {
        checkNotClosed();
        return new SessionImpl(serviceRegistry, metamodel);
    }
    
    @Override
    public Metamodel getMetamodel() {
        checkNotClosed();
        return metamodel;
    }
    
    @Override
    public boolean isClosed() {
        return closed;
    }
    
    @Override
    public void close() {
        if (!closed) {
            closed = true;
            serviceRegistry.close();
        }
    }
    
    private void checkNotClosed() {
        if (closed) {
            throw new MiniOrmException("SessionFactory is closed");
        }
    }
}