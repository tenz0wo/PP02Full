package ru.inversion.migration_assistant.model.common;

public interface DbConnectionParams {
    String getUrl();
    String getUser();
    String getPassword();
}
