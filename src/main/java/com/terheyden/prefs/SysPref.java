package com.terheyden.prefs;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.prefs.Preferences;

/**
 * Annotation for binding class fields to a {@link Preferences} setting.
 * Used by my {@link Prefs} class.
 */
@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface SysPref {

    /**
     * The system-wide preference key to bind this field to.
     * If not set, this defaults to the field name.
     */
    String key() default "";

    /**
     * Pref vals can never be null, so what is the default?
     * Must be a string, so for nums and bools do "3" and "true" etc.
     */
    String defaultVal() default "";
}
