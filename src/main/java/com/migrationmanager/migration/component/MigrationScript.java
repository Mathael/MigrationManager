package com.migrationmanager.migration.component;

import com.migrationmanager.migration.MigrationType;

import java.util.List;

/**
 * @author LEBOC Philippe
 */
public interface MigrationScript {

    /**
     * Unique class identifier. Don't duplicate in another class when its already defined !
     * @return The MigrationScript type
     */
    MigrationType getType();

    /**
     * files name in "resources/migration" folder
     * @return list with name of sql script files that must be executed during the migration process
     */
    List<String> migrationScripts();

    /**
     * This time will be compared with the last migrationTime from dabatase to known if was already migrated or not
     * @return a timestamp set manually by developer
     */
    long lastMigrationTime();

    /**
     * Define execution order
     * The execution order will execute the lowest order at first time
     * Please, don't set negative values
     *
     * This method must be improved to use enumeration instead of integer
     *
     * @return The natural value that define the execution order
     */
    default int getOrder() {
        return 1;
    }
}
