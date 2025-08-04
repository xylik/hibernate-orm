package com.miniorm;

/**
 * A SessionFactory is responsible for creating Session instances.
 * This is a heavy-weight object that holds the metamodel and
 * configuration for the ORM framework.
 * 
 * Key Hibernate patterns demonstrated:
 * - Factory pattern for session creation
 * - Singleton-like behavior (one per application)
 * - Service registry integration
 * - Metamodel management
 */
public interface SessionFactory extends AutoCloseable {
    
    /**
     * Open a new session.
     * Each session represents a unit of work.
     */
    Session openSession();
    
    /**
     * Get the metamodel containing entity mappings.
     * Demonstrates metadata management pattern.
     */
    Metamodel getMetamodel();
    
    /**
     * Check if the factory is closed.
     */
    boolean isClosed();
    
    /**
     * Close the session factory and release all resources.
     */
    @Override
    void close();
}