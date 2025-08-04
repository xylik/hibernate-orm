package com.miniorm;

/**
 * Represents a database transaction.
 * 
 * Key Hibernate patterns demonstrated:
 * - Transaction boundary management
 * - Resource lifecycle management
 * - Commit/rollback semantics
 */
public interface Transaction {
    
    /**
     * Commit the transaction, making all changes permanent.
     */
    void commit();
    
    /**
     * Rollback the transaction, undoing all changes.
     */
    void rollback();
    
    /**
     * Check if transaction is active.
     */
    boolean isActive();
    
    /**
     * Mark the transaction for rollback only.
     */
    void setRollbackOnly();
    
    /**
     * Check if transaction is marked for rollback.
     */
    boolean getRollbackOnly();
}