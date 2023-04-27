package ru.inversion.migration_assistant.model.response;

import lombok.Data;

@Data
public class TableDto {
    String schemaName;
    String tableName;
    String ddlTabPg;
    String ddlConPg;
    String ddlTabFdw;
    String script;
    String rowidStat;
}
