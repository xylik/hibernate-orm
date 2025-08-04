package com.miniorm;

import java.util.List;

/**
 * Represents a query that can be executed against the database.
 * 
 * Key Hibernate patterns demonstrated:
 * - Query abstraction layer
 * - Parameter binding
 * - Result mapping
 * - Lazy result iteration
 */
public interface Query {
    
    /**
     * Set a parameter value by position.
     */
    Query setParameter(int position, Object value);
    
    /**
     * Set a parameter value by name.
     */
    Query setParameter(String name, Object value);
    
    /**
     * Set the maximum number of results to retrieve.
     */
    Query setMaxResults(int maxResults);
    
    /**
     * Set the position of the first result to retrieve.
     */
    Query setFirstResult(int firstResult);
    
    /**
     * Execute the query and return a list of results.
     */
    List<Object> list();
    
    /**
     * Execute the query and return a single result.
     * Throws exception if more than one result is found.
     */
    Object uniqueResult();
    
    /**
     * Execute an update/delete query.
     * Returns the number of affected rows.
     */
    int executeUpdate();
}