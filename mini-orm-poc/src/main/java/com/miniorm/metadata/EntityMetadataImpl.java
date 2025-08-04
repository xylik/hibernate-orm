package com.miniorm.metadata;

import com.miniorm.EntityMetadata;
import com.miniorm.MiniOrmException;
import com.miniorm.annotations.*;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Default implementation of EntityMetadata.
 * 
 * Demonstrates annotation processing and metadata extraction.
 */
public class EntityMetadataImpl implements EntityMetadata {
    
    private final Class<?> entityClass;
    private final String entityName;
    private final String tableName;
    private final Field identifierField;
    private final List<Field> persistentFields;
    
    public EntityMetadataImpl(Class<?> entityClass) {
        this.entityClass = entityClass;
        this.entityName = extractEntityName(entityClass);
        this.tableName = extractTableName(entityClass);
        this.identifierField = findIdentifierField(entityClass);
        this.persistentFields = findPersistentFields(entityClass);
    }
    
    private String extractEntityName(Class<?> clazz) {
        Entity entity = clazz.getAnnotation(Entity.class);
        if (entity != null && !entity.name().isEmpty()) {
            return entity.name();
        }
        return clazz.getSimpleName();
    }
    
    private String extractTableName(Class<?> clazz) {
        Table table = clazz.getAnnotation(Table.class);
        if (table != null) {
            return table.name();
        }
        return clazz.getSimpleName().toLowerCase();
    }
    
    private Field findIdentifierField(Class<?> clazz) {
        for (Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(Id.class)) {
                field.setAccessible(true);
                return field;
            }
        }
        throw new MiniOrmException("No @Id field found in entity: " + clazz.getName());
    }
    
    private List<Field> findPersistentFields(Class<?> clazz) {
        List<Field> fields = new ArrayList<>();
        for (Field field : clazz.getDeclaredFields()) {
            // Skip static and transient fields
            if (java.lang.reflect.Modifier.isStatic(field.getModifiers()) ||
                java.lang.reflect.Modifier.isTransient(field.getModifiers())) {
                continue;
            }
            field.setAccessible(true);
            fields.add(field);
        }
        return fields;
    }
    
    @Override
    public Class<?> getEntityClass() {
        return entityClass;
    }
    
    @Override
    public String getEntityName() {
        return entityName;
    }
    
    @Override
    public String getTableName() {
        return tableName;
    }
    
    @Override
    public Field getIdentifierField() {
        return identifierField;
    }
    
    @Override
    public List<Field> getPersistentFields() {
        return persistentFields;
    }
    
    @Override
    public String getColumnName(Field field) {
        Column column = field.getAnnotation(Column.class);
        if (column != null && !column.name().isEmpty()) {
            return column.name();
        }
        return field.getName().toLowerCase();
    }
    
    @Override
    public boolean isIdentifierField(Field field) {
        return field.equals(identifierField);
    }
    
    @Override
    public Object getIdentifierValue(Object entity) {
        try {
            return identifierField.get(entity);
        } catch (IllegalAccessException e) {
            throw new MiniOrmException("Failed to get identifier value", e);
        }
    }
    
    @Override
    public void setIdentifierValue(Object entity, Object id) {
        try {
            identifierField.set(entity, id);
        } catch (IllegalAccessException e) {
            throw new MiniOrmException("Failed to set identifier value", e);
        }
    }
}