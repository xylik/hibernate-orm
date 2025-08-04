package com.miniorm.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a class as a persistent entity.
 * Equivalent to JPA's @Entity annotation.
 * 
 * Demonstrates annotation-driven configuration pattern.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Entity {
    
    /**
     * The name of the entity. Defaults to the simple class name.
     */
    String name() default "";
}