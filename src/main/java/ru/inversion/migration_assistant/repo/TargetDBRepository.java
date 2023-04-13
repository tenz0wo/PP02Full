package ru.inversion.migration_assistant.repo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.inversion.migration_assistant.exec.DBExecHandler;
import ru.inversion.migration_assistant.exec.ExecutorParams;
import ru.inversion.migration_assistant.exec.repo.CheckTableExecutor;
import ru.inversion.migration_assistant.exec.repo.SqlScriptExecutor;
import ru.inversion.migration_assistant.model.common.ResponseObj;
import ru.inversion.migration_assistant.model.request.RequestCheckTable;
import ru.inversion.migration_assistant.model.request.RequestExecutableScript;
import ru.inversion.migration_assistant.model.response.ResponseCheckTable;
import ru.inversion.migration_assistant.model.response.ResponseExecutableScript;

import java.util.List;

@Service
@Slf4j
public class TargetDBRepository {
    public ResponseObj<ResponseExecutableScript> executeSqlScript(RequestExecutableScript params){
        return new DBExecHandler<>(SqlScriptExecutor.class, new ExecutorParams<>(params)).exec();
    }

    public ResponseObj<ResponseCheckTable> checkTable(RequestCheckTable params) {
        return new DBExecHandler<>(CheckTableExecutor.class, new ExecutorParams<>(params)).exec();
    }
}
