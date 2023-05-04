package ru.inversion.migration_assistant.repo;

import io.swagger.v3.core.util.Json;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.inversion.migration_assistant.exec.DBExecHandler;
import ru.inversion.migration_assistant.exec.ExecutorParams;
import ru.inversion.migration_assistant.exec.repo.CheckConnectionExecutor;
import ru.inversion.migration_assistant.exec.repo.CheckTableExecutor;
import ru.inversion.migration_assistant.exec.repo.ExplainTableExecutor;
import ru.inversion.migration_assistant.exec.repo.SqlScriptExecutor;
import ru.inversion.migration_assistant.model.common.DbConnectionParams;
import ru.inversion.migration_assistant.model.common.ResponseObj;
import ru.inversion.migration_assistant.model.request.RequestCheckTable;
import ru.inversion.migration_assistant.model.request.RequestExecutableScript;
import ru.inversion.migration_assistant.model.request.RequestExplainTable;
import ru.inversion.migration_assistant.model.response.ResponseCheckTable;
import ru.inversion.migration_assistant.model.response.ResponseExecutableScript;
import ru.inversion.migration_assistant.model.response.ResponseExplainTable;

@Service
@Slf4j
public class TargetDBRepository {
    public ResponseObj<ResponseExecutableScript> executeSqlScript(RequestExecutableScript params){
        return new DBExecHandler<>(SqlScriptExecutor.class, new ExecutorParams<>(params)).exec();
    }

    public ResponseObj<ResponseCheckTable> checkTable(RequestCheckTable params) {
        return new DBExecHandler<>(CheckTableExecutor.class, new ExecutorParams<>(params)).exec();
    }

    public ResponseObj<String> checkConnection(DbConnectionParams params) {
        return new DBExecHandler<>(CheckConnectionExecutor.class, new ExecutorParams<>(params)).exec();
    }

    public ResponseObj<ResponseExplainTable> getExplainTable(RequestExplainTable params) {
        return new DBExecHandler<>(ExplainTableExecutor.class, new ExecutorParams<>(params)).exec();
    }
}
