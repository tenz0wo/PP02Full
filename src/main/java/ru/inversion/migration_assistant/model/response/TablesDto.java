package ru.inversion.migration_assistant.model.response;

import lombok.Data;

@Data
public class TablesDto {
    String version;
    String prefix;
    String schema;
    TableDto table = new TableDto();
}
