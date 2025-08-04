package com.miniorm.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Specifies that an identifier should be generated automatically.
 * Equivalent to JPA's @GeneratedValue annotation.
 */
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface GeneratedValue {
    
    /**
     * The strategy for generating values.
     */
    GenerationType strategy() default GenerationType.AUTO;
}