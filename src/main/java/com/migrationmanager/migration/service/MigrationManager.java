package com.migrationmanager.migration.service;

import com.migrationmanager.migration.holder.MigrationScriptHolder;
import com.migrationmanager.migration.model.Migration;

import java.util.List;

/**
 * The Migration Manager has a goal of updating database
 * May be later it will be improved to make some action on file system
 *
 * @author LEBOC Philippe
 */
public interface MigrationManager {

    /**
     * Initialize the Migration Manager
     * - Scan database for corresponding table
     * - Scan migration package for migrations classes
     * - Compare timestamp to select to migration that will be performed
     * - Search SQL files
     * - Execute SQL scripts
     * - Update SQL migration table
     */
    void initialize();

    /**
     * Scan database for corresponding table to compare with our class timestamp and choose candidates
     * @return List of migration model from database
     */
    List<Migration> scanDatabase();

    /**
     * This method should be UPDATED to really scan package + manage annotated class from parent project
     *
     * Scan migration package for migrations classes
     * @return every class found in the scanned package
     */
    List<MigrationScriptHolder> scanMigrationPackage();

    /**
     * Search SQL files
     * @param scriptList The script list candidates that contains files to be found in classpath
     */
    void scanResourceScript(final List<MigrationScriptHolder> scriptList);

    /**
     * Execute SQL scripts
     * @param scriptHolders The scripts candidates for a migration
     */
    void migrate(final List<MigrationScriptHolder> scriptHolders);

    /**
     * Update SQL migration table
     * @param scriptHolders The already updated scripts without errors
     */
    void updateMigrationTable(final List<MigrationScriptHolder> scriptHolders);
}
