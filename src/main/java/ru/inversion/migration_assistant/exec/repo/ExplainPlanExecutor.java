package ru.inversion.migration_assistant.exec.repo;

import ru.inversion.migration_assistant.db.DBType;
import ru.inversion.migration_assistant.exec.DBExecutor;
import ru.inversion.migration_assistant.exec.ExecutorConsumer;
import ru.inversion.migration_assistant.exec.ExecutorParams;
import ru.inversion.migration_assistant.model.request.RequestExplainPlan;
import ru.inversion.migration_assistant.model.response.ResponseExplainPlan;
import springfox.documentation.spring.web.json.Json;

import java.sql.ResultSet;

public class ExplainPlanExecutor extends DBExecutor<ResponseExplainPlan> {
    final RequestExplainPlan params;
    String queryJson;
    String queryText;
    String explainSubQuery;
    boolean processingJson = false;
    boolean processingText = false;

    public ExplainPlanExecutor(ExecutorParams<RequestExplainPlan> params) {
        super(params);
        this.params = params.getWrappedParams();
    }
    @Override
    public ResponseExplainPlan exec() throws Exception {
        ResponseExplainPlan responseExplainPlan = new ResponseExplainPlan();
        defineQueries();
        if (params.getCallback().isJson()){
            processingJson = true;
            executeQuery (queryJson, createConsumer (responseExplainPlan));
            processingJson = false;
        }
        if (params.getCallback().isPlain_text()){
            processingText = true;
            executeQuery (queryText, createConsumer (responseExplainPlan));
            processingJson = false;
        }
        return responseExplainPlan;
    }

    ExecutorConsumer<ResultSet> createConsumer (ResponseExplainPlan responseExplainPlan) {
        return resultSet -> {
            String response = "";
            while (resultSet.next()) {
                if (processingJson){
                    response = resultSet.getString(1);
                    response = response.substring(1, response.length() - 1);
                    Json responseJson = new Json(response);
                    responseExplainPlan.setJson(responseJson);
                }
                if (processingText) {
                    response += resultSet.getString(1);
                    responseExplainPlan.setPlain_text(response);
                }
            }
        };
    }

    void defineQueries () {
        if (dbType == DBType.ORACLE) {
            throw new RuntimeException("For DBType = " + dbType.name() + " query is not defined!");
        } else if (dbType == DBType.POSTGRES) {
            if (params.getCallback().isJson()){
                queryJson = createSubQuery("JSON") + params.getQuery();
            }
            if (params.getCallback().isPlain_text()){
                queryText = createSubQuery("TEXT") + params.getQuery();
            }
        } else {
            throw new RuntimeException("For DBType = " + dbType.name() + " query is not defined!");
        }
    }

    String createSubQuery(String formatEXPLAIN){
        String paramsExplainSubQuery = "";
        if (params.isAnalyze()){
            paramsExplainSubQuery += ",ANALYZE";
        }
        if (params.isVerbose()){
            paramsExplainSubQuery += ",VERBOSE";
        }
        if (params.isBuffers()){
            paramsExplainSubQuery += ",BUFFERS";
        }
        if (params.isSettings()){
            paramsExplainSubQuery += ",SETTINGS";
        }
        if (params.isWal()){
            paramsExplainSubQuery += ",WAL";
        }
        return "EXPLAIN (FORMAT " + formatEXPLAIN + paramsExplainSubQuery + ") ";
    }
}
