package ru.inversion.migration_assistant.model.response;

import lombok.Data;

@Data
public class TablesDto {
    String version;
    String prefix;
    TableDto table = new TableDto();
}
