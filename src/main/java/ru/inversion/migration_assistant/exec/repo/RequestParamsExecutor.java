package ru.inversion.migration_assistant.exec.repo;

import org.apache.commons.lang3.StringUtils;
import ru.inversion.migration_assistant.exec.DBExecutor;
import ru.inversion.migration_assistant.exec.ExecutorParams;
import ru.inversion.migration_assistant.model.request.RequestParams;

public abstract class RequestParamsExecutor<T> extends DBExecutor<T> {
    RequestParams params;

    public RequestParamsExecutor(ExecutorParams<RequestParams> executorParams) {
        super(executorParams);
        params = executorParams.getWrappedParams();
        prePopulateParams();
    }

    void prePopulateParams () {
        if (StringUtils.isBlank(params.getI_prefix())) {
            params.setI_prefix("");
        }
        if (StringUtils.isBlank(params.getI_schema_name())) {
            params.setI_schema_name("");
        }
    }
}
