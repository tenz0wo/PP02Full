package ru.inversion.migration_assistant.model.request;

import lombok.Data;
import ru.inversion.migration_assistant.model.common.DbConnectionParamsImpl;

@Data
public class RequestDependencies extends DbConnectionParamsImpl {
    String owner;
    String referencedOwner;
    String name;
    String type;

}
