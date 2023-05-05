package ru.inversion.migration_assistant.repo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.inversion.migration_assistant.exec.DBExecHandler;
import ru.inversion.migration_assistant.exec.ExecutorParams;
import ru.inversion.migration_assistant.exec.repo.*;
import ru.inversion.migration_assistant.model.common.DbConnectionParams;
import ru.inversion.migration_assistant.model.common.ResponseObj;
import ru.inversion.migration_assistant.model.request.RequestCheckTable;
import ru.inversion.migration_assistant.model.request.RequestExecutableScript;
import ru.inversion.migration_assistant.model.request.RequestExplainPlan;
import ru.inversion.migration_assistant.model.response.ResponseCheckTable;
import ru.inversion.migration_assistant.model.response.ResponseExecutableScript;
import ru.inversion.migration_assistant.model.response.ResponseExplainPlan;

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

    public ResponseObj<ResponseExplainPlan> explainPlan(RequestExplainPlan params) {
        return new DBExecHandler<>(ExplainPlanExecutor.class, new ExecutorParams<>(params)).exec();
    }
}
