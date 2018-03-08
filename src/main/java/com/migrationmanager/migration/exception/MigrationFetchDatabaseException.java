package com.migrationmanager.migration.exception;

import lombok.extern.slf4j.Slf4j;

/**
 * @author LEBOC Philippe
 */
@Slf4j
public class MigrationFetchDatabaseException extends RuntimeException {

    /**
     * Default constructor
     */
    public MigrationFetchDatabaseException() {
        super();
        log.error("Migration aborted !");
    }
}
