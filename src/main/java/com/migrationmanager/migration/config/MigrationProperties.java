package com.migrationmanager.migration.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author LEBOC Philippe
 */
@ConfigurationProperties("application.migration")
public class MigrationProperties {

    /**
     * The package that contains your Migration classes
     */
    private String[] packagesToScan;

    /**
     * The SQL files extension
     */
    private String filesExtension = "sql";

    /**
     * The SQL files location
     */
    private String filesLocation = "migration";

    public String[] getPackagesToScan() {
        return packagesToScan;
    }

    public void setPackagesToScan(final String[] packagesToScan) {
        this.packagesToScan = packagesToScan;
    }

    public String getFilesExtension() {
        return filesExtension;
    }

    public void setFilesExtension(final String filesExtension) {
        this.filesExtension = filesExtension;
    }

    public String getFilesLocation() {
        return filesLocation;
    }

    public void setFilesLocation(final String filesLocation) {
        this.filesLocation = filesLocation;
    }
}
