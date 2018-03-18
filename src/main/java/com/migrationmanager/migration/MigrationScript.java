package com.migrationmanager.migration;

import com.migrationmanager.migration.enums.MigrationType;

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
}
