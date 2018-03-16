package com.migrationmanager.migration;

import com.migrationmanager.annotation.MigrationScriptFlag;
import com.migrationmanager.annotation.Prod;
import com.migrationmanager.annotation.Staging;
import com.migrationmanager.migration.component.MigrationScript;
import com.migrationmanager.migration.exception.MigrationFetchDatabaseException;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.init.ScriptException;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
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
@Service
@ConditionalOnProperty(name = "application.migration.enabled", havingValue = "true")
public class MigrationManagerImpl implements MigrationManager {

    // Logger
    private static final Logger log = LoggerFactory.getLogger(MigrationManagerImpl.class);

    // SQL
    private static final String SQL_MIGRATION_SELECT_ALL = "SELECT * FROM migration";
    private static final String SQL_MIGRATION_UPDATE = "UPDATE migration SET lastMigrationTime = ? WHERE type = ?";

    // System message
    private static final String WARN_MISSING_INTERFACE = "The class [{}] is annotated as MigrationScript but cannot be interpreted. It should implement MigrationScript interface !";

    @Autowired
    @Qualifier("migrationJdbcTemplate")
    private JdbcTemplate database;

    @Value("${application.migration.script.package}")
    public String[] packagesToScan;

    public MigrationManagerImpl() {
        log.info("Initializing migration manager");
    }

    @Override
    public void initialize() {

        // TODO: re visit this code

        final List<Migration> migrations = scanDatabase();
        final List<MigrationScriptHolder> scripts = scanMigrationPackage();

        final List<MigrationScript> list = new ArrayList<>();
        scripts.stream().forEach(getMigrationScriptToBeExecuted(migrations, list));

        scanResourceScript(scripts);
        migrate(scripts);
        updateMigrationTable(scripts);
    }

    @Override
    public List<Migration> scanDatabase() {
        final List<Migration> migrations;
        try {
            migrations = database.query(SQL_MIGRATION_SELECT_ALL, new MigrationMapper());
        } catch (Exception ex) {
            log.error("Cannot query migration table.", ex);
            throw new MigrationFetchDatabaseException();
        }
        return migrations;
    }

    @Override
    public List<MigrationScriptHolder> scanMigrationPackage() {

        final List<MigrationScriptHolder> scripts = new ArrayList<>();
        final Reflections reflections = new Reflections(packagesToScan);
        final Set<Class<?>> annotated = reflections.getTypesAnnotatedWith(MigrationScriptFlag.class);

        annotated.forEach(aClass ->
        {
            if (!MigrationScript.class.isAssignableFrom(aClass)) {
                log.warn(WARN_MISSING_INTERFACE, aClass.getName());
                return;
            }

            final MigrationScriptFlag flag = (MigrationScriptFlag) Arrays.stream(aClass.getAnnotations())
                    .filter(annotation -> annotation instanceof MigrationScriptFlag)
                    .findFirst().orElse(null);

            if(null == flag) {
                log.warn("An annotation cannot disappear at Runtime !");
                return;
            }

            try {
                final MigrationScript script = (MigrationScript) aClass.newInstance();
                scripts.add(new MigrationScriptHolder(script, flag.priority(), flag.order()));
            } catch (InstantiationException e) {
                log.warn("Cannot instantiate the class : " + aClass.getName(), e);
            } catch (IllegalAccessException e) {
                log.warn("The class should contains a public constructor without args", e);
            }
        });

        return scripts;
    }

    @Override
    public void scanResourceScript(final List<MigrationScriptHolder> scriptList) {
        for (MigrationScriptHolder script : scriptList) {
            for (String s : script.getScript().migrationScripts()) {
                if (s != null && !s.trim().isEmpty()) {
                    final ClassPathResource file = new ClassPathResource("migration/" + s + ".sql");
                    if (file.exists()) {
                        script.getResources().add(file);
                    } else {
                        log.warn("file [{}] does not exist", "migration/" + s + ".sql");
                    }
                }
            }
        }
    }

    @Override
    public void migrate(final List<MigrationScriptHolder> scriptHolders) {
        scriptHolders.forEach(holder -> {
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
