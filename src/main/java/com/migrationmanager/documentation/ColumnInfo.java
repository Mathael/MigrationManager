package com.migrationmanager.documentation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.SOURCE;

/**
 * @author LEBOC Philippe
 */
@Documented
@Target(FIELD)
@Retention(SOURCE)
public @interface ColumnInfo {

    /**
     * @return The column name
     */
    String name();

    /**
     * @return Table description
     */
    String descr() default "";

    /**
     * Defines several {@code @NotEmpty} annotations on the same element.
     */
    @Target(FIELD)
    @Retention(SOURCE)
    @Documented
    @interface List {
        ColumnInfo[] value();
    }
}
