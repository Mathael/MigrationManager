package com.migrationmanager.documentation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.SOURCE;

/**
 * @author LEBOC Philippe
 */
@Target(TYPE)
@Retention(SOURCE)
public @interface TableInfo {

    /**
     * @return The table name
     */
    String name();

    /**
     * @return Table description
     */
    String descr() default "";
}
