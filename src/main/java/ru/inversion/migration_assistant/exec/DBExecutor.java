package ru.inversion.migration_assistant.exec;

import ru.inversion.migration_assistant.model.common.DbConnectionParams;

import java.sql.*;

public abstract class DBExecutor<T> implements Executor<T>{
    private final Object params;
    Connection connection = null;

    public DBExecutor(ExecutorParams<?> executorParams) {
        this.params = executorParams.getWrappedParams();
    }

    protected Connection getConnection() throws SQLException {
        if (connection == null) {
            connection = DriverManager.getConnection("jdbc:postgresql://pgpro.corp.inversion.ru:5432/pgdev", "xxi", "casper");
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

    public void executeUpdate(String query) throws Exception {
        Statement statement = createStatement();
        statement.executeUpdate(query);
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
