package ru.inversion.migration_assistant.exec.repo;

import ru.inversion.migration_assistant.db.DBType;
import ru.inversion.migration_assistant.exec.DBExecutor;
import ru.inversion.migration_assistant.exec.ExecutorConsumer;
import ru.inversion.migration_assistant.exec.ExecutorParams;
import ru.inversion.migration_assistant.model.request.RequestExplainTable;
import ru.inversion.migration_assistant.model.response.ResponseExplainTable;
import springfox.documentation.spring.web.json.Json;

import java.sql.ResultSet;

public class ExplainTableExecutor extends DBExecutor<ResponseExplainTable> {
    final RequestExplainTable params;
    String query;

    public ExplainTableExecutor(ExecutorParams<RequestExplainTable> params) {
        super(params);
        this.params = params.getWrappedParams();
    }
    @Override
    public ResponseExplainTable exec() throws Exception {
        ResponseExplainTable responseExplainTable = new ResponseExplainTable();
        defineQueries();
        executeQuery (query, createConsumer (responseExplainTable));
        return responseExplainTable;
    }

    ExecutorConsumer<ResultSet> createConsumer (ResponseExplainTable responseExplainTable) {
        return resultSet -> {
            while (resultSet.next()) {
                String response = resultSet.getString(1);
                response = response.substring(1, response.length() - 1);
                Json responseJson = new Json(response);
                responseExplainTable.setResponse(responseJson);
            }
        };
    }

    void defineQueries () {
        if (dbType == DBType.ORACLE) {
            throw new RuntimeException("For DBType = " + dbType.name() + " query is not defined!");
        } else if (dbType == DBType.POSTGRES) {
            query = params.getQuery();
        } else {
            throw new RuntimeException("For DBType = " + dbType.name() + " query is not defined!");
        }
    }
}
