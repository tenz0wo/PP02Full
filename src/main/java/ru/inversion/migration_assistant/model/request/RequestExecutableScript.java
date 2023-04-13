package ru.inversion.migration_assistant.model.request;

import lombok.Data;
import ru.inversion.migration_assistant.model.common.DbConnectionParamsImpl;

@Data
public class RequestExecutableScript extends DbConnectionParamsImpl {
    ExecutableScript executableScript;

    public RequestExecutableScript(DbConnectionParamsImpl params){
        this.setUrl(params.getUrl());
        this.setUser(params.getUser());
        this.setPassword(params.getPassword());
    }
}
