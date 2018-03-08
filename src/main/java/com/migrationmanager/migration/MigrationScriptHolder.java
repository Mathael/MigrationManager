package com.migrationmanager.migration;

import com.migrationmanager.migration.component.MigrationScript;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.core.io.ClassPathResource;

import java.util.List;

/**
 * @author LEBOC Philippe
 */
@Getter
@Setter
@NoArgsConstructor
public class MigrationScriptHolder {

    private MigrationScript script;
    private List<ClassPathResource> resources;
    private MigrationState state;

    public MigrationScriptHolder(MigrationScript script, List<ClassPathResource> resources) {
        this.script = script;
        this.resources = resources;
        this.state = MigrationState.NOT_DONE;
    }
}
