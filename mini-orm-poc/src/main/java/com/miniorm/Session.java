package com.miniorm;

/**
 * A session represents a unit of work and maintains a first-level cache
 * of persistent objects. This is the main interface for working with
 * the Mini-ORM framework.
 * 
 * Key Hibernate patterns demonstrated:
 * - Unit of Work pattern
 * - Identity Map pattern
 * - Session-per-request pattern
 */
public interface Session extends AutoCloseable {
    
    /**
     * Save an entity to the database.
     * Corresponds to Hibernate's Session.save() and Session.persist()
     */
    void save(Object entity);
    
    /**
     * Load an entity by its identifier.
     * Demonstrates lazy loading and proxy creation.
     */
    <T> T load(Class<T> entityClass, Object id);
    
    /**
     * Find an entity by its identifier.
     * Returns null if not found (unlike load which throws exception).
     */
    <T> T get(Class<T> entityClass, Object id);
    
    /**
     * Update an entity in the database.
     * Demonstrates dirty checking and change detection.
     */
    void update(Object entity);
    
    /**
     * Delete an entity from the database.
     */
    void delete(Object entity);
    
    /**
     * Create a query using a simple query language.
     * Demonstrates query parsing and SQL generation.
     */
    Query createQuery(String queryString);
    
    /**
     * Begin a transaction.
     * Demonstrates transaction management pattern.
     */
    Transaction beginTransaction();
    
    /**
     * Flush pending changes to the database.
     * Demonstrates change tracking and batch processing.
     */
    void flush();
    
    /**
     * Clear the session cache.
     * Demonstrates session lifecycle management.
     */
    void clear();
    
    /**
     * Check if session is open.
     */
    boolean isOpen();
    
    /**
     * Close the session and release resources.
     */
    @Override
    void close();
}