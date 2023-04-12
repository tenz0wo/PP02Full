package ru.inversion.migration_assistant.db;

public enum DBType {
    ORACLE,
    POSTGRES,
    UNKNOWN;

    public static DBType defineByUrl(String url) {
        if (url.toLowerCase().contains("oracle")) {
            return ORACLE;
        } else if (url.toLowerCase().contains("postgres")) {
            return POSTGRES;
        } else {
            return UNKNOWN;
        }
    }
}
