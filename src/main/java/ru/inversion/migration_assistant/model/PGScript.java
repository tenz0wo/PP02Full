package ru.inversion.migration_assistant.model;

import lombok.Data;

@Data
public class PGScript {
    String scriptName;
    String script;
}
