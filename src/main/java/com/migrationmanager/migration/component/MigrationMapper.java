package com.migrationmanager.migration.component;

import com.migrationmanager.migration.Migration;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import static com.migrationmanager.migration.MigrationType.toMigrationType;

/**
 * @author LEBOC Philippe
 */
public class MigrationMapper implements RowMapper<Migration> {

    @Override
    public Migration mapRow(ResultSet rs, int rowNum) throws SQLException {
        final Migration migration = new Migration();
        migration.setType(toMigrationType(rs.getString("type")));
        migration.setLastMigrationTime(rs.getLong("lastMigrationTime"));
        return migration;
    }
}
