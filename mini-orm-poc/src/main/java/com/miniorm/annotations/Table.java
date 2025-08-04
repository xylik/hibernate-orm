package com.miniorm.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Specifies the table name for an entity.
 * Equivalent to JPA's @Table annotation.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Table {
    
    /**
     * The name of the table.
     */
    String name();
    
    /**
     * The schema of the table.
     */
    String schema() default "";
}