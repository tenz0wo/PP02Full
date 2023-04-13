package ru.inversion.migration_assistant.exec.repo;

import ru.inversion.migration_assistant.db.DBType;
import ru.inversion.migration_assistant.exec.DBExecutor;
import ru.inversion.migration_assistant.exec.ExecutorConsumer;
import ru.inversion.migration_assistant.exec.ExecutorParams;
import ru.inversion.migration_assistant.model.request.RequestCheckTable;
import ru.inversion.migration_assistant.model.response.ResponseCheckTable;

import java.sql.ResultSet;
import java.util.Objects;

public class CheckTableExecutor extends DBExecutor<ResponseCheckTable> {
    final RequestCheckTable params;
    enum CheckType {EXISTS, NOT_EMPTY}
    String queryExists;
    String queryNotEmpty;

    public CheckTableExecutor(ExecutorParams<RequestCheckTable> params) {
        super(params);
        this.params = params.getWrappedParams();
    }

    @Override
    public ResponseCheckTable exec() throws Exception {
        ResponseCheckTable responseCheckTable = new ResponseCheckTable();
        defineQueries();
        executeQuery (queryExists, createConsumer (responseCheckTable, CheckType.EXISTS));
        if (responseCheckTable.getTableExists()) {
            executeQuery (queryNotEmpty, createConsumer (responseCheckTable, CheckType.NOT_EMPTY));
        }
        return responseCheckTable;
    }

    ExecutorConsumer<ResultSet> createConsumer (ResponseCheckTable responseCheckTable, CheckType checkType) {
        return resultSet -> {
            resultSet.next();
            boolean res;
            if (dbType == DBType.ORACLE) {
                res = resultSet.getInt(1) == 1;
            } else {
                res = resultSet.getBoolean(1);
            }
            switch (checkType) {
                case EXISTS:
                    responseCheckTable.setTableExists(res);
                    break;
                case NOT_EMPTY:
                    responseCheckTable.setTableNotEmpty(res);
                    break;
            }
        };
    }

    void defineQueries () {
        String table;
        if (params.isChangeTableCase()){
            if (Objects.equals(params.getTable(), params.getTable().toUpperCase())){
                table = params.getTable().toLowerCase();
            } else {
                table = params.getTable().toUpperCase();
            }
        } else {
            table = params.getTable();
        }

        if (dbType == DBType.ORACLE) {
            queryExists = "SELECT LEAST(COUNT(*),1)\n" +
                          "  FROM all_tables\n" +
                          " WHERE owner = '" + params.getSchema() + "'\n" +
                          "   AND table_name = '" + table + "'";

            queryNotEmpty = "SELECT LEAST(COUNT(*),1)\n" +
                            "  FROM DUAL\n" +
                            " WHERE EXISTS (SELECT NULL FROM " + params.getSchema() + "." + table + ")";
        } else if (dbType == DBType.POSTGRES) {
            queryExists = "SELECT EXISTS (SELECT 1 FROM pg_tables WHERE schemaname = '" + params.getSchema() + "' AND tablename = '" + table + "') AS table_exists";
            queryNotEmpty = "SELECT EXISTS (SELECT 1 FROM " + params.getSchema() + "." + table + " LIMIT 1) AS table_not_empty";
        } else {
            throw new RuntimeException("For DBType = " + dbType.name() + " query is not defined!");
        }
    }

}
