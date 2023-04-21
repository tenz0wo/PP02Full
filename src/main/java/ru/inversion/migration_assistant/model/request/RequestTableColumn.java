package ru.inversion.migration_assistant.model.request;

import lombok.Data;
import ru.inversion.migration_assistant.model.common.DbConnectionParamsImpl;

@Data
public class RequestTableColumn extends DbConnectionParamsImpl {
    String schema;
    String table_name;
}
