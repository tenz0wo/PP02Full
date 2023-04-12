package ru.inversion.migration_assistant.model.request;

import lombok.Data;
import ru.inversion.migration_assistant.model.common.DbConnectionParamsImpl;

import java.util.LinkedList;
import java.util.List;

@Data
public class RequestExecutableScripts extends DbConnectionParamsImpl {
    List<ExecutableScript> executableScripts = new LinkedList<>();
}
