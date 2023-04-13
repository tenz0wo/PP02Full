package ru.inversion.migration_assistant.model.request;

import lombok.Data;
import ru.inversion.migration_assistant.model.common.DbConnectionParamsImpl;

import java.util.LinkedList;
import java.util.List;

@Data
public class RequestExecutableScript extends DbConnectionParamsImpl {
    ExecutableScript executableScript;

    public RequestExecutableScript(DbConnectionParamsImpl params){
        this.setUrl(params.getUrl());
        this.setUser(params.getUser());
        this.setPassword(params.getPassword());
    }
}
