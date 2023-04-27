package ru.inversion.migration_assistant.exec.repo;

import ru.inversion.migration_assistant.db.DBType;
import ru.inversion.migration_assistant.exec.DBExecutor;
import ru.inversion.migration_assistant.exec.ExecutorConsumer;
import ru.inversion.migration_assistant.exec.ExecutorParams;
import ru.inversion.migration_assistant.model.operational.TableValues;
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
        TableValues tableValues = new TableValues();
        tableValues.setTableName (fixCase (params.getTable(), params.isSwitchTableCase()));
        tableValues.setSchemaName (fixCase (params.getSchema(), params.isSwitchSchemaCase()));
        tableValues.setTableNameForQuery (fixCaseWithDbType (params.getTable(), params.isSwitchTableCase()));
        if (dbType == DBType.ORACLE) {
            queryExists = "SELECT LEAST(COUNT(*),1)\n" +
                          "  FROM all_tables\n" +
                          " WHERE owner = '" + tableValues.getSchemaName() + "'\n" +
                          "   AND table_name = '" + tableValues.getTableName() + "'";

            queryNotEmpty = "SELECT LEAST(COUNT(*),1)\n" +
                            "  FROM DUAL\n" +
                            " WHERE EXISTS (SELECT NULL FROM " + tableValues.getSchemaName() + "." + tableValues.getTableNameForQuery() + ")";
        } else if (dbType == DBType.POSTGRES) {
            queryExists = "SELECT EXISTS (SELECT 1 FROM pg_tables WHERE schemaname = '" + tableValues.getSchemaName() + "' AND tablename = '" + tableValues.getTableName() + "') AS table_exists";
            queryNotEmpty = "SELECT EXISTS (SELECT 1 FROM " + tableValues.getSchemaName() + "." + tableValues.getTableNameForQuery() + " LIMIT 1) AS table_not_empty";
        } else {
            throw new RuntimeException("For DBType = " + dbType.name() + " query is not defined!");
        }
    }

    String fixCase (String str, boolean switchCase) {
        if (switchCase){
            if (Objects.equals(str, str.toUpperCase())){
                return str.toLowerCase();
            } else {
                return str.toUpperCase();
            }
        } else {
            return str;
        }
    }

    String fixCaseWithDbType(String str, boolean switchCase) {
        String retStr = fixCase(str, switchCase);
        if (dbType == DBType.ORACLE && Objects.equals(retStr, retStr.toLowerCase()) || dbType == DBType.POSTGRES && Objects.equals(retStr, retStr.toUpperCase())) {
            retStr = "\"" + retStr + "\"";
        }
        return retStr;
    }

}
