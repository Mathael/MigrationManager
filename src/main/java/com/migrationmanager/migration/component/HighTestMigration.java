package com.migrationmanager.migration.component;

import com.migrationmanager.annotation.MigrationScriptFlag;
import com.migrationmanager.migration.MigrationPriority;
import com.migrationmanager.migration.MigrationType;

import java.util.List;

import static java.util.Arrays.asList;

/**
 * Should be execution on High priority and as the first script
 * @author LEBOC Philippe
 */
@MigrationScriptFlag(priority = MigrationPriority.HIGH, order = 1)
public class HighTestMigration implements MigrationScript {

    private static final String[] FILES = {
        "create"
    };

    @Override
    public MigrationType getType() {
        return MigrationType.GLOBAL_CREATE;
    }

    @Override
    public List<String> migrationScripts() {
        return asList(FILES);
    }

    @Override
    public long lastMigrationTime() {
        return 0;
    }
}
