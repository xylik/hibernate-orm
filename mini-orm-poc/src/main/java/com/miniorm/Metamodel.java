package com.miniorm;

import java.util.Map;

/**
 * Represents the metamodel containing entity mappings and metadata.
 * 
 * Key Hibernate patterns demonstrated:
 * - Metadata-driven framework
 * - Runtime metamodel
 * - Entity mapping abstraction
 */
public interface Metamodel {
    
    /**
     * Get entity metadata for a given entity class.
     */
    EntityMetadata getEntityMetadata(Class<?> entityClass);
    
    /**
     * Get all entity metadata.
     */
    Map<Class<?>, EntityMetadata> getAllEntityMetadata();
    
    /**
     * Check if a class is mapped as an entity.
     */
    boolean isEntityClass(Class<?> clazz);
    
    /**
     * Get the entity name for a given class.
     */
    String getEntityName(Class<?> entityClass);
}