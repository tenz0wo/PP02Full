package ru.inversion.migration_assistant.exec.repo;

import ru.inversion.migration_assistant.exec.DBExecutor;
import ru.inversion.migration_assistant.exec.ExecutorParams;
import ru.inversion.migration_assistant.model.common.DbConnectionParams;
import ru.inversion.migration_assistant.model.common.DbConnectionParamsImpl;

import java.sql.Connection;

public class CheckConnectionExecutor extends DBExecutor<String> {
    public CheckConnectionExecutor(ExecutorParams<DbConnectionParams> executorParams) {
        super(executorParams);
    }

    @Override
    public String exec() throws Exception {
        Connection conn = getConnection();
        return conn.toString();
    }
}
