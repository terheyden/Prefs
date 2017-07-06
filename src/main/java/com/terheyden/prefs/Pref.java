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
public @interface Pref {

    /**
     * If set to true, this preference will be set system-wide instead of
     * associated with the current user.
     */
    boolean isGlobal() default false;

    /**
     * The name to give this value in the preference store.
     * If not set, this defaults to the field name.
     */
    String name() default "";

    /**
     * When this field has no value, what should we use as the default?
     * Must be a string, so for nums and bools do "3" and "true" etc.
     */
    String defaultVal() default "";
}
