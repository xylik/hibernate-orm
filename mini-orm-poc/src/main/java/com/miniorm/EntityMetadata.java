package com.miniorm;

import java.lang.reflect.Field;
import java.util.List;

/**
 * Metadata about an entity class.
 * 
 * Demonstrates the metamodel pattern for describing entity structure.
 */
public interface EntityMetadata {
    
    /**
     * Get the entity class.
     */
    Class<?> getEntityClass();
    
    /**
     * Get the entity name.
     */
    String getEntityName();
    
    /**
     * Get the table name.
     */
    String getTableName();
    
    /**
     * Get the identifier field.
     */
    Field getIdentifierField();
    
    /**
     * Get all persistent fields.
     */
    List<Field> getPersistentFields();
    
    /**
     * Get the column name for a field.
     */
    String getColumnName(Field field);
    
    /**
     * Check if a field is the identifier.
     */
    boolean isIdentifierField(Field field);
    
    /**
     * Get the identifier value from an entity instance.
     */
    Object getIdentifierValue(Object entity);
    
    /**
     * Set the identifier value on an entity instance.
     */
    void setIdentifierValue(Object entity, Object id);
}