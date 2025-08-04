package com.miniorm;

/**
 * Core exception class for Mini-ORM framework.
 * Equivalent to HibernateException in Hibernate.
 */
public class MiniOrmException extends RuntimeException {
    
    public MiniOrmException(String message) {
        super(message);
    }
    
    public MiniOrmException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public MiniOrmException(Throwable cause) {
        super(cause);
    }
}