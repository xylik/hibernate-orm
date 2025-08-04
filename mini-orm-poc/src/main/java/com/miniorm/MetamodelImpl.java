package com.miniorm;

import java.util.Map;

/**
 * Implementation of Metamodel interface.
 */
public class MetamodelImpl implements Metamodel {
    
    private final Map<Class<?>, EntityMetadata> entityMetadata;
    
    public MetamodelImpl(Map<Class<?>, EntityMetadata> entityMetadata) {
        this.entityMetadata = entityMetadata;
    }
    
    @Override
    public EntityMetadata getEntityMetadata(Class<?> entityClass) {
        EntityMetadata metadata = entityMetadata.get(entityClass);
        if (metadata == null) {
            throw new MiniOrmException("Entity not found: " + entityClass.getName());
        }
        return metadata;
    }
    
    @Override
    public Map<Class<?>, EntityMetadata> getAllEntityMetadata() {
        return entityMetadata;
    }
    
    @Override
    public boolean isEntityClass(Class<?> clazz) {
        return entityMetadata.containsKey(clazz);
    }
    
    @Override
    public String getEntityName(Class<?> entityClass) {
        return getEntityMetadata(entityClass).getEntityName();
    }
}