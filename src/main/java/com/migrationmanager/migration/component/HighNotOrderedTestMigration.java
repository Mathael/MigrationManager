package com.migrationmanager.migration.component;

import com.migrationmanager.annotation.MigrationScriptFlag;
import com.migrationmanager.migration.MigrationPriority;
import com.migrationmanager.migration.MigrationType;

import java.util.List;

import static java.util.Arrays.asList;

/**
 * @author LEBOC Philippe
 */
@MigrationScriptFlag(priority = MigrationPriority.HIGH)
public class HighNotOrderedTestMigration implements MigrationScript {

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
