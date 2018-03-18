package com.migrationmanager.migration.annotation;

import com.migrationmanager.migration.enums.MigrationPriority;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @author LEBOC Philippe
 */
@Documented
@Target({TYPE})
@Retention(RUNTIME)
public @interface MigrationScriptFlag {

    /**
     * Define the Script priority execution
     * @return The script execution priority
     */
    MigrationPriority priority() default MigrationPriority.NORMAL;

    /**
     * Define the order for a same priority execution. The lowest value will be firstly executed.
     * Default value is 100
     * @return The script execution order
     */
    int order() default 100;
}
