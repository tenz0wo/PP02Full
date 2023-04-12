package ru.inversion.migration_assistant.exec;

public interface Executor<T> {
    T exec() throws Exception;
}
