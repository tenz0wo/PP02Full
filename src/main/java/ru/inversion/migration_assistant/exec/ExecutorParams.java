package ru.inversion.migration_assistant.exec;

import lombok.Getter;
import ru.inversion.migration_assistant.model.common.DbConnectionParams;
import ru.inversion.migration_assistant.model.common.DbConnectionParamsImpl;

public class ExecutorParams<T extends DbConnectionParams> {
    T params;

    public ExecutorParams (T params) {
        this.params = params;
    }

    public T getWrappedParams() {
        return params;
    }
}
