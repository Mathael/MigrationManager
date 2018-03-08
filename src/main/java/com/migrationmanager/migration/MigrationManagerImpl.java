package com.migrationmanager.migration;

import com.migrationmanager.annotation.Prod;
import com.migrationmanager.annotation.Staging;
import com.migrationmanager.migration.component.MigrationScript;
import com.migrationmanager.migration.component.TestMigration;
import com.migrationmanager.migration.exception.MigrationFetchDatabaseException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.init.ScriptException;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static java.util.Comparator.comparing;
import static java.util.Comparator.comparingInt;

/**
 * Migration Manager will never start on @Dev profile
 * The Manager will start on all theses rules (inclusive)
 * - Staging or Prod Spring profiles
 * - application.migration.enabled = true in properties OR override by JVM property
 *
 * @author LEBOC Philippe
 */
@Prod
@Staging
@Slf4j
@Service
@ConditionalOnProperty("${application.migration.enabled:false}")
public class MigrationManagerImpl implements MigrationManager {

    private static final String SQL_MIGRATION_SELECT_ALL = "SELECT * FROM migration";
    private static final String SQL_MIGRATION_UPDATE = "UPDATE migration SET lastMigrationTime = ? WHERE type = ?";

    @Autowired
    @Qualifier("migrationJdbcTemplate")
    private JdbcTemplate database;

    @Override
    public void initialize() {
        final List<Migration> migrations = scanDatabase();
        final List<MigrationScript> scripts = scanMigrationPackage();
        final List<MigrationScript> migrationScriptList = new ArrayList<>();

        scripts
            .stream()
            .sorted(comparing(MigrationScript::getOrder))
            .forEachOrdered(getMigrationScriptToBeExecuted(migrations, migrationScriptList));

        final List<MigrationScriptHolder> scriptHolders = scanResourceScript(migrationScriptList);

        migrate(scriptHolders);
        updateMigrationTable(scriptHolders);
    }

    @Override
    public List<Migration> scanDatabase() {
        final List<Migration> migrations;
        try {
            migrations = database.queryForList(SQL_MIGRATION_SELECT_ALL, Migration.class);
        } catch (Exception ex) {
            log.error("Cannot query migration table.", ex);
            throw new MigrationFetchDatabaseException();
        }
        return migrations;
    }

    @Override
    public List<MigrationScript> scanMigrationPackage() {
        final List<MigrationScript> scripts = new ArrayList<>();
        scripts.add(new TestMigration());
        return scripts;
    }

    @Override
    public List<MigrationScriptHolder> scanResourceScript(final List<MigrationScript> scriptList) {
        final List<MigrationScriptHolder> holders = new ArrayList<>();
        for (MigrationScript script : scriptList) {
            final List<ClassPathResource> files = new ArrayList<>();
            for (String s : script.migrationScripts()) {
                if (s != null && !s.trim().isEmpty()) {
                    final ClassPathResource file = new ClassPathResource("migration/" + s + ".sql");
                    if (file.exists()) {
                        files.add(file);
                    } else {
                        log.warn("file [{}] does not exist", "migration/" + s + ".sql");
                    }
                }
            }
            holders.add(new MigrationScriptHolder(script, files));
        }
        return holders;
    }

    @Override
    public void migrate(final List<MigrationScriptHolder> scriptHolders) {
        scriptHolders.stream().sorted(comparingInt(m -> m.getScript().getOrder())).forEachOrdered(holder -> {
            for (ClassPathResource file : holder.getResources()) {
                try {
                    log.info("Executing migration script [{}] : files [{}]", holder.getScript().getType().name(), file.getFilename());
                    ScriptUtils.executeSqlScript(database.getDataSource().getConnection(), file);
                    holder.setState(MigrationState.DONE);
                } catch (ScriptException | SQLException ex) {
                    log.error("Cannot execute script [" + file.getFilename() + "]", ex);
                }
            }
        });
    }

    @Override
    public void updateMigrationTable(final List<MigrationScriptHolder> scriptHolders) {
        scriptHolders
                .stream()
                .filter(holder -> holder != null && holder.getState() == MigrationState.DONE)
                .forEach(holder -> database.update(SQL_MIGRATION_UPDATE, holder.getScript().lastMigrationTime(), holder.getScript().getType().name()));
    }

    /**
     * Consumer to retrieve the proper script list to be executed
     *
     * @param migrations          The database current values for each Migration object {type, timestamp}
     * @param migrationScriptList The script list to be populated with the scripts that must be executed
     * @return the list of script that are waiting to upgrade
     */
    private Consumer<MigrationScript> getMigrationScriptToBeExecuted(final List<Migration> migrations, final List<MigrationScript> migrationScriptList) {
        return script -> {
            final Migration migration = migrations
                    .stream()
                    .filter(m -> m != null && m.getType() == script.getType())
                    .findFirst().orElse(null);

            if (migration != null && migration.getLastMigrationTime() < script.lastMigrationTime()) {
                migrationScriptList.add(script);
            }
        };
    }
}
