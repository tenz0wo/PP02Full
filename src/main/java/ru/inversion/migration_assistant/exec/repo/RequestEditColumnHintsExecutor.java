package ru.inversion.migration_assistant.exec.repo;

import org.apache.commons.lang3.StringUtils;
import ru.inversion.migration_assistant.exec.DBExecutor;
import ru.inversion.migration_assistant.exec.ExecutorParams;
import ru.inversion.migration_assistant.model.common.DbConnectionParams;
import ru.inversion.migration_assistant.model.request.RequestEditColumnHints;

public abstract class RequestEditColumnHintsExecutor<T> extends DBExecutor<T> {
    final RequestEditColumnHints params;

    public RequestEditColumnHintsExecutor(ExecutorParams<? extends DbConnectionParams> executorParams) {
        super(executorParams);
        params = (RequestEditColumnHints) executorParams.getWrappedParams();
        prePopulateParams();
    }

    void prePopulateParams () {
        if (StringUtils.isBlank(params.getI_table_owner())) {
            params.setI_table_owner("");
        }
        if (StringUtils.isBlank(params.getI_column_name())) {
            params.setI_column_name("");
        }
    }
}
