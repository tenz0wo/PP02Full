package ru.inversion.migration_assistant.model;

import lombok.Data;

import java.util.LinkedList;
import java.util.List;

@Data
public class TablesDto {
    String version;
    String prefix;
//    List<ru.inversion.migration_assistant.model.TableDto> tables = new LinkedList<>();
    TableDto table = new TableDto();
}
