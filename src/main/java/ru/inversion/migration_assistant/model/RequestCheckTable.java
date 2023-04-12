package ru.inversion.migration_assistant.model;

import lombok.Data;

@Data
public class RequestCheckTable {
    String driver;
    String url;
    String user;
    String password;
    String schema;
    String table;
}
