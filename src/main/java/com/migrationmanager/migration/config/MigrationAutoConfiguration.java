package com.migrationmanager.migration.config;

import com.migrationmanager.migration.service.MigrationManager;
import com.migrationmanager.migration.service.MigrationManagerImpl;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * @author LEBOC Philippe.
 */
@Configuration
@ConditionalOnClass({
    MigrationManagerImpl.class,
    JdbcTemplate.class
})
@EnableConfigurationProperties(MigrationProperties.class)
public class MigrationAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public MigrationManager migrationManager(MigrationProperties props) {
        return new MigrationManagerImpl(props.getPackagesToScan(), props.getFilesLocation(), props.getFilesExtension());
    }
}
