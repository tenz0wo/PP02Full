package ru.inversion.migration_assistant.model.request;

import lombok.Data;
import ru.inversion.migration_assistant.model.common.DbConnectionParamsImpl;

@Data
public class RequestCheckTable extends DbConnectionParamsImpl {
    boolean changeTableCase;
    String schema;
    String table;
}
