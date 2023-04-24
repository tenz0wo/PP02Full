package ru.inversion.migration_assistant.model.response;

import lombok.Data;

import java.util.List;

@Data
public class ResponseTableColumns {
    List<DoubleParam> tableColumns;
}
