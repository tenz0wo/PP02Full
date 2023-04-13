package ru.inversion.migration_assistant.exec;

import ru.inversion.migration_assistant.model.common.DbConnectionParams;

public class ExecutorParams<T extends DbConnectionParams> {
    final T params;

    public ExecutorParams (T params) {
        this.params = params;
    }

    public T getWrappedParams() {
        return params;
    }
}
