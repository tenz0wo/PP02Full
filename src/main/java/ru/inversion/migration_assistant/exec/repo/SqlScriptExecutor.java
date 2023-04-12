package ru.inversion.migration_assistant.exec.repo;

import ru.inversion.migration_assistant.exec.DBExecutor;
import ru.inversion.migration_assistant.exec.ExecutorParams;
import ru.inversion.migration_assistant.model.request.ExecutableScript;
import ru.inversion.migration_assistant.model.request.RequestCheckTable;
import ru.inversion.migration_assistant.model.request.RequestExecutableScripts;
import ru.inversion.migration_assistant.model.response.ResponseExecutableScripts;

import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

public class SqlScriptExecutor extends DBExecutor<List<ResponseExecutableScripts>> {
    RequestExecutableScripts params;

    public SqlScriptExecutor(ExecutorParams<RequestExecutableScripts> params) {
        super(params);
        this.params = params.getWrappedParams();
    }

    @Override
    public List<ResponseExecutableScripts> exec() throws Exception {
        List<ResponseExecutableScripts> response = new LinkedList<>();
        for (ExecutableScript executableScript : params.getExecutableScripts()){
            ResponseExecutableScripts responseExecutableScripts = new ResponseExecutableScripts();
            responseExecutableScripts.setScriptName(executableScript.getScriptName());
            execute(executableScript.getScript());
            responseExecutableScripts.setResponse("Ok");
            response.add(responseExecutableScripts);
            execute("commit;");
        }
        return response;
    }
}
