package com.migrationmanager.migration.component;

import com.migrationmanager.migration.MigrationType;

import java.util.List;

import static java.util.Arrays.asList;

/**
 * @author LEBOC Philippe
 */
public class TestMigration implements MigrationScript {

    private static final String[] FILES = {
        "filename-test"
    };

    @Override
    public MigrationType getType() {
        return MigrationType.NONE;
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
