package ru.inversion.migration_assistant.model.response;

import lombok.Data;

@Data
public class ColumnHints {
    String tableName;
    String tableOwner;
    String columnName;
    String columnDataType;
    String originalDataType;
}
