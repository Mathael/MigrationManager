package com.migrationmanager.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

/**
 * Migration Manager use Developer user to execute SQL update
 * DO NOT USE THIS DATASOURCE ! Migration purpose ONLY
 *
 * This class is never used when spring is started as "dev" or "test" profile.
 * It will run on "staging" or "prod" profiles
 *
 * @author LEBOC Philippe
 */
@Configuration
public class DataSourceMigrationConfiguration {

    @Bean(name = "migrationDataSource")
    @ConfigurationProperties(prefix="application.migration.datasource.mb")
    public DataSource migrationDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "migrationJdbcTemplate")
    public JdbcTemplate migrationJdbcTemplate() {
        return new JdbcTemplate(migrationDataSource());
    }
}

