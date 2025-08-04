package com.miniorm.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Specifies the column name for a field or property.
 * Equivalent to JPA's @Column annotation.
 */
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Column {
    
    /**
     * The name of the column.
     */
    String name() default "";
    
    /**
     * Whether the column is nullable.
     */
    boolean nullable() default true;
    
    /**
     * The length of the column for string types.
     */
    int length() default 255;
}