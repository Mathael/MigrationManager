package com.migrationmanager.migration;

import com.migrationmanager.migration.component.MigrationScript;
import org.springframework.core.io.ClassPathResource;

import java.util.List;

/**
 * @author LEBOC Philippe
 */
public final class MigrationScriptHolder {

    private MigrationScript script;
    private List<ClassPathResource> resources;
    private MigrationState state;

    public MigrationScriptHolder(MigrationScript script, List<ClassPathResource> resources) {
        this.script = script;
        this.resources = resources;
        this.state = MigrationState.NOT_DONE;
    }

    public MigrationScript getScript() {
        return script;
    }

    public void setScript(MigrationScript script) {
        this.script = script;
    }

    public List<ClassPathResource> getResources() {
        return resources;
    }

    public void setResources(List<ClassPathResource> resources) {
        this.resources = resources;
    }

    public MigrationState getState() {
        return state;
    }

    public void setState(MigrationState state) {
        this.state = state;
    }
}
