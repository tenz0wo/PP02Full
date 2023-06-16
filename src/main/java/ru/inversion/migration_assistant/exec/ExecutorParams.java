package ru.inversion.migration_assistant.exec;

public class ExecutorParams<T> {
    final T params;

    public ExecutorParams (T params) {
        this.params = params;
    }

    public T getWrappedParams() {
        return params;
    }
}
