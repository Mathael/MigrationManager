package com.migrationmanager;

import com.migrationmanager.migration.MigrationManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * @author LEBOC Philippe
 */
@Component
@ConditionalOnProperty(name = "application.migration.enabled", havingValue = "true")
public class StartupDataInitializer {

    @Autowired
    private MigrationManager migrationManager;

    @EventListener(ContextRefreshedEvent.class)
    public void onApplicationEvent(final ContextRefreshedEvent event) {
        migrationManager.initialize();
    }
}
