package ru.inversion.migration_assistant.model.response;

import lombok.Data;

import java.util.List;

@Data
public class ResponseColumnsHints {
    List<ColumnHints> ColumnsHints;
}
