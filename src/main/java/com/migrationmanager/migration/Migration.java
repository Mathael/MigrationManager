package com.migrationmanager.migration;

import com.migrationmanager.documentation.ColumnInfo;
import com.migrationmanager.documentation.TableInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author LEBOC Philippe
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@TableInfo(name = "migration")
public class Migration {

    @ColumnInfo(name = "type", descr = "Primary Key. The migration type used to prevent from using str as descriptor")
    private MigrationType type;

    @ColumnInfo(name = "lastMigrationTime", descr = "The last migration timestamp")
    private long lastMigrationTime;
}
