package ru.inversion.migration_assistant.exec.repo;

import ru.inversion.migration_assistant.exec.DBExecutor;
import ru.inversion.migration_assistant.exec.ExecutorParams;
import ru.inversion.migration_assistant.model.request.ExecutableScript;
import ru.inversion.migration_assistant.model.request.RequestExecutableScript;
import ru.inversion.migration_assistant.model.response.ResponseExecutableScript;

public class SqlScriptExecutor extends DBExecutor<ResponseExecutableScript> {
    final RequestExecutableScript params;

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
        responseExecutableScript.setResponse("Ok");
        execute("commit");

        return responseExecutableScript;
    }
}
