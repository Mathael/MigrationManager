package com.migrationmanager.migration.holder;

import com.migrationmanager.migration.MigrationScript;
import com.migrationmanager.migration.enums.MigrationPriority;
import com.migrationmanager.migration.enums.MigrationState;
import org.springframework.core.io.ClassPathResource;

import java.util.ArrayList;
import java.util.List;

/**
 * @author LEBOC Philippe
 */
public final class MigrationScriptHolder {

    private MigrationScript script;
    private MigrationPriority priority;
    private int order;

    private List<ClassPathResource> resources;
    private MigrationState state;

    /**
     * The default constructor include the Script and annotation attributes (default values if any)
     * @param script The script
     * @param priority The default or override priority
     * @param order The default or override order
     */
    public MigrationScriptHolder(MigrationScript script, MigrationPriority priority, int order) {
        this.script = script;
        this.priority = priority;
        this.order = order;
        this.state = MigrationState.NOT_DONE;
        this.resources = new ArrayList<>();
    }

    // Method

    /**
     * Define the valid condition for a script to be executed
     * @return The execution election validity
     */
    public boolean isValid() {
        return script != null && priority != null && resources != null && !resources.isEmpty() && state == MigrationState.NOT_DONE;
    }

    // Getter & Setters

    public MigrationScript getScript() {
        return script;
    }

    public void setScript(MigrationScript script) {
        this.script = script;
    }

    public MigrationPriority getPriority() {
        return priority;
    }

    public void setPriority(MigrationPriority priority) {
        this.priority = priority;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
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
