package com.migrationmanager.migration.model;

import com.migrationmanager.migration.enums.MigrationType;

/**
 * @author LEBOC Philippe
 */
public class Migration {

    private MigrationType type;
    private long lastMigrationTime;

    public Migration() {}

    public Migration(MigrationType type, long lastMigrationTime) {
        this.type = type;
        this.lastMigrationTime = lastMigrationTime;
    }

    public MigrationType getType() {
        return type;
    }

    public void setType(MigrationType type) {
        this.type = type;
    }

    public long getLastMigrationTime() {
        return lastMigrationTime;
    }

    public void setLastMigrationTime(long lastMigrationTime) {
        this.lastMigrationTime = lastMigrationTime;
    }
}
