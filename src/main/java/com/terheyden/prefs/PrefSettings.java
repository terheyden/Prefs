package com.terheyden.prefs;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Declare above your class to set {@link Prefs} settings.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface PrefSettings {

    /**
     * The path you would like to store your preferences under, for example, "/com/yourname/yourapp".
     * Defaults to the package your class is in (even if you don't use this annotation).
     */
    String path() default "";
}
