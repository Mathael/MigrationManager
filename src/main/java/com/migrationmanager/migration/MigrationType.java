package com.migrationmanager.migration;

import static java.util.stream.Stream.of;

/**
 * @author LEBOC Philippe
 */
public enum MigrationType {

    NONE,
    GLOBAL_CREATE;

    /**
     * Translate database value to an ENUM
     * @param str The database Enum as string
     * @return The corresponding ENUM otherwise NONE
     */
    public static MigrationType toMigrationType(String str) {
        if (str == null) return NONE;
        return of(values())
                .filter(enumeration -> enumeration != null && enumeration.name().equalsIgnoreCase(str))
                .findFirst()
                .orElse(NONE);
    }
}
