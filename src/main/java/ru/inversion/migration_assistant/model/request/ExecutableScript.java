package ru.inversion.migration_assistant.model.request;

import lombok.Data;

@Data
public class ExecutableScript {
    String scriptName;
    String script;
}
