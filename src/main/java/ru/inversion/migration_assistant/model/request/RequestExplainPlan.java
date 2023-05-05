package ru.inversion.migration_assistant.model.request;

import lombok.Data;
import ru.inversion.migration_assistant.model.common.DbConnectionParamsImpl;

@Data
public class RequestExplainPlan extends DbConnectionParamsImpl {
    String query;
    boolean analyze;
    boolean verbose;
    boolean buffers;
    boolean settings;
    boolean wal;
    CallbackExplainPlan callback;
}
