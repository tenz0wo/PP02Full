package ru.inversion.migration_assistant.model;

import lombok.Data;

import java.util.LinkedList;
import java.util.List;

@Data
public class RequestPGScripts {
    String host;
    String port;
    String dbName;
    String user;
    String password;

    List<PGScript> pgScripts = new LinkedList<>();
}
