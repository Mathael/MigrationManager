package com.migrationmanager.migration.service;

import com.migrationmanager.migration.MigrationScript;
import com.migrationmanager.migration.annotation.MigrationScriptFlag;
import com.migrationmanager.migration.enums.MigrationState;
import com.migrationmanager.migration.exception.MigrationFetchDatabaseException;
import com.migrationmanager.migration.holder.MigrationScriptHolder;
import com.migrationmanager.migration.mapper.MigrationMapper;
import com.migrationmanager.migration.model.Migration;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.init.ScriptException;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

/**
 * @author LEBOC Philippe
 */
@Service
public class MigrationManagerImpl implements MigrationManager {

    // Logger
    private static final Logger log = LoggerFactory.getLogger(MigrationManagerImpl.class);

    // Logger messages
    private static final String ERROR_CANNOT_QUERY_MIGRATION_TABLE = "Cannot query migration table.";

    // SQL
    private static final String SQL_MIGRATION_SELECT_ALL = "SELECT * FROM migration";
    private static final String SQL_MIGRATION_UPDATE = "UPDATE migration SET lastMigrationTime = ? WHERE type = ?";

    // System message
    private static final String WARN_MISSING_INTERFACE = "The class [{}] is annotated as MigrationScript but cannot be interpreted. It should implement MigrationScript interface !";

    @Autowired
    private JdbcTemplate database;

    private String[] packagesToScan;
    private String filesLocation;
    private String filesExtension;

    public MigrationManagerImpl(final String[] packagesToScan, final String filesLocation, final String filesExtension) {
        this.packagesToScan = packagesToScan;
        this.filesExtension = filesExtension;
        this.filesLocation = filesLocation;
        log.info("Migration Manager Bean created");
    }

    @Override
    public void initialize() {
        final List<Migration> migrations = scanDatabase();
        final List<MigrationScriptHolder> scripts = scanMigrationPackage();
        final List<MigrationScriptHolder> selected = new ArrayList<>();

        scripts.forEach(getMigrationScriptToBeExecuted(migrations, selected));

        scanResourceScript(selected);
        migrate(selected);
        updateMigrationTable(selected);
    }

    @Override
    public List<Migration> scanDatabase() {
        final List<Migration> migrations;
        try {
            migrations = database.query(SQL_MIGRATION_SELECT_ALL, new MigrationMapper());
        } catch (Exception ex) {
            log.error(ERROR_CANNOT_QUERY_MIGRATION_TABLE, ex);
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

            if (null == flag) {
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
        for (final MigrationScriptHolder script : scriptList) {
            for (final String s : script.getScript().migrationScripts()) {
                if (s != null && !s.trim().isEmpty()) {
                    final ClassPathResource file = new ClassPathResource(filesLocation + "/" + s + "." + filesExtension);
                    if (file.exists()) {
                        script.getResources().add(file);
                    } else {
                        log.warn("file [{}] does not exist", "migration/" + s + "." + filesExtension);
                    }
                }
            }
        }
    }

    @Override
    public void migrate(final List<MigrationScriptHolder> scriptHolders) {
        scriptHolders
                .stream()
                .sorted(Comparator.<MigrationScriptHolder>comparingInt(s -> s.getPriority().ordinal()).thenComparingInt(MigrationScriptHolder::getOrder))
                .forEachOrdered(holder ->
                {
                    for (final ClassPathResource file : holder.getResources()) {
                        try {
                            log.info("Executing migration script priority = [{}] order = [{}] type = [{}] : files [{}]", holder.getPriority(), holder.getOrder(), holder.getScript().getType().name(), file.getFilename());
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
    private Consumer<MigrationScriptHolder> getMigrationScriptToBeExecuted(final List<Migration> migrations, final List<MigrationScriptHolder> migrationScriptList) {
        return holder -> {
            final Migration migration = migrations
                    .stream()
                    .filter(m -> m != null && m.getType() == holder.getScript().getType())
                    .findFirst().orElse(null);

            if (migration != null && migration.getLastMigrationTime() < holder.getScript().lastMigrationTime()) {
                migrationScriptList.add(holder);
            }
        };
    }
}
