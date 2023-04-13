package ru.inversion.migration_assistant.exec.repo;

import ru.inversion.migration_assistant.exec.DBExecutor;
import ru.inversion.migration_assistant.exec.ExecutorParams;
import ru.inversion.migration_assistant.model.request.ExecutableScript;
import ru.inversion.migration_assistant.model.request.RequestExecutableScript;
import ru.inversion.migration_assistant.model.request.RequestExecutableScripts;
import ru.inversion.migration_assistant.model.response.ResponseExecutableScript;

import java.util.LinkedList;
import java.util.List;

public class SqlScriptExecutor extends DBExecutor<ResponseExecutableScript> {
    RequestExecutableScript params;

    public SqlScriptExecutor(ExecutorParams<RequestExecutableScript> params) {
        super(params);
        this.params = params.getWrappedParams();
    }

    @Override
    public ResponseExecutableScript exec() throws Exception {
        ExecutableScript executableScript = params.getExecutableScript();
        ResponseExecutableScript responseExecutableScript = new ResponseExecutableScript();
        responseExecutableScript.setScriptName(executableScript.getScriptName());
        boolean result = execute(executableScript.getScript());
        responseExecutableScript.setResponse(result ? "Ok" : "Fail");
        execute("commit");

        return responseExecutableScript;
    }
}
