package com.miniorm;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Implementation of Transaction interface.
 * 
 * Demonstrates transaction management and connection handling.
 */
public class TransactionImpl implements Transaction {
    
    private final Connection connection;
    private boolean active = true;
    private boolean rollbackOnly = false;
    
    public TransactionImpl(Connection connection) {
        this.connection = connection;
        try {
            connection.setAutoCommit(false);
        } catch (SQLException e) {
            throw new MiniOrmException("Failed to begin transaction", e);
        }
    }
    
    @Override
    public void commit() {
        checkActive();
        if (rollbackOnly) {
            rollback();
            throw new MiniOrmException("Transaction was marked for rollback only");
        }
        
        try {
            connection.commit();
            active = false;
        } catch (SQLException e) {
            rollback();
            throw new MiniOrmException("Failed to commit transaction", e);
        }
    }
    
    @Override
    public void rollback() {
        if (active) {
            try {
                connection.rollback();
            } catch (SQLException e) {
                throw new MiniOrmException("Failed to rollback transaction", e);
            } finally {
                active = false;
            }
        }
    }
    
    @Override
    public boolean isActive() {
        return active;
    }
    
    @Override
    public void setRollbackOnly() {
        checkActive();
        rollbackOnly = true;
    }
    
    @Override
    public boolean getRollbackOnly() {
        return rollbackOnly;
    }
    
    private void checkActive() {
        if (!active) {
            throw new MiniOrmException("Transaction is not active");
        }
    }
}