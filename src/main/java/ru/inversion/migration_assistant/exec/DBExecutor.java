package ru.inversion.migration_assistant.exec;

import ru.inversion.migration_assistant.db.DBType;
import ru.inversion.migration_assistant.model.common.DbConnectionParams;

import java.sql.*;

public abstract class DBExecutor<T> implements Executor<T>{
    Connection connection = null;

    final DbConnectionParams params;

    protected final DBType dbType;

    public DBExecutor(ExecutorParams<? extends DbConnectionParams> executorParams) {
        this.params = executorParams.getWrappedParams();
        this.dbType = DBType.defineByUrl(params.getUrl());
    }

    protected Connection getConnection() throws SQLException {
        if (connection == null) {
            connection = DriverManager.getConnection(params.getUrl(), params.getUser(), params.getPassword());
        }
        return connection;
    }

    private Statement createStatement() throws SQLException {
        return getConnection().createStatement();
    }

    public void executeQuery(String query, ExecutorConsumer<ResultSet> execConsumer) throws Exception {
        Statement statement = createStatement();
        ResultSet rs = statement.executeQuery(query);
        execConsumer.accept(rs);
    }

    public void executeCallable(String callableStr,
                                ExecutorConsumer<CallableStatement> prepareCallConsumer,
                                ExecutorConsumer<CallableStatement> resultCallConsumer) throws Exception {
        CallableStatement callableStatement = getConnection().prepareCall(callableStr);
        prepareCallConsumer.accept(callableStatement);
        callableStatement.execute();
        resultCallConsumer.accept(callableStatement);
    }

    public boolean execute(String script) throws SQLException {
        Statement statement = createStatement();
        return statement.execute(script);
    }

    public void closeConnection() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

}
