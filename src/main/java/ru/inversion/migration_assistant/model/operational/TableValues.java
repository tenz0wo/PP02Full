package ru.inversion.migration_assistant.model.operational;

import lombok.Data;

@Data
public class TableValues {
    String tableName;
    String schemaName;
    String tableNameForQuery;
}
