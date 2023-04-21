package ru.inversion.migration_assistant.model.response;

import lombok.Data;

@Data
public class ResponseTableColumn {
    String columnName;
    String columnType;
}
