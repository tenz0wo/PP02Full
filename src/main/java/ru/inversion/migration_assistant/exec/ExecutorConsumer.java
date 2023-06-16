package ru.inversion.migration_assistant.exec;

@FunctionalInterface
public interface ExecutorConsumer<T> {
    void accept (T t) throws Exception;
}
