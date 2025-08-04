package com.miniorm.annotations;

/**
 * Enumeration of identifier generation strategies.
 * Equivalent to JPA's GenerationType enum.
 */
public enum GenerationType {
    
    /**
     * Let the framework choose the appropriate strategy.
     */
    AUTO,
    
    /**
     * Use an identity column (auto-increment).
     */
    IDENTITY,
    
    /**
     * Use a database sequence.
     */
    SEQUENCE,
    
    /**
     * Use a table to generate identifiers.
     */
    TABLE
}