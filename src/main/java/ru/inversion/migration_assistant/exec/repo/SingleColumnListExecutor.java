package ru.inversion.migration_assistant.exec.repo;

import ru.inversion.migration_assistant.db.DBType;
import ru.inversion.migration_assistant.exec.ExecutorConsumer;
import ru.inversion.migration_assistant.model.request.RequestParams;
import ru.inversion.migration_assistant.model.sql.ObjectPart;
import ru.inversion.migration_assistant.model.sql.ObjectType;

import java.sql.ResultSet;
import java.util.LinkedList;
import java.util.List;

public class SingleColumnListExecutor extends RequestParamsExecutor<List<String>> {
    String query = "";
    String column = "";
    final List<String> responseSingleColumnList = new LinkedList<>();
    final ObjectPart objectPart;
    final ObjectType objectType;

    public SingleColumnListExecutor (ColumnListParams<RequestParams> params) {
        super(params);
        this.objectPart = params.getObjectPart();
        this.objectType = params.getObjectType();
    }

    @Override
    public List<String> exec() throws Exception {
        defineQuery();
        defineColumn();
        executeQuery (query, createConsumer ());
        return responseSingleColumnList;
    }

    ExecutorConsumer<ResultSet> createConsumer () {
        return resultSet -> {
            while (resultSet.next()) {
                responseSingleColumnList.add(resultSet.getString(column));
            }
        };
    }

    void defineColumn() {
        switch (objectPart) {
            case OBJECT:
                column = "OBJECT_NAME";
                break;
            case SCHEMA:
                column = "OWNER";
                break;
        }
    }

    void defineQuery() {
        if (dbType != DBType.ORACLE) {
            throw new RuntimeException("For DBType = " + dbType.name() + " query is not defined!");
        }
        switch (objectPart) {
            case OBJECT:
                query = "SELECT DISTINCT OBJECT_NAME \n" +
                        "  FROM DBA_OBJECTS \n" +
                        " WHERE OBJECT_TYPE = '" + objectType.name() + "' \n" +
                        "   AND ('" + params.getI_prefix() +"' IS NULL OR UPPER (object_name) LIKE '" + params.getI_prefix().toUpperCase() + "%') \n" +
                        "   AND ('" + params.getI_schema_name() + "' IS NULL OR UPPER (owner) LIKE '" + params.getI_schema_name().toUpperCase() + "%') \n" +
                        " ORDER BY UPPER (OBJECT_NAME), LENGTH (UPPER (OBJECT_NAME)) \n" +
                        " FETCH FIRST 50 ROWS ONLY";
                break;
            case SCHEMA:
                query = "SELECT OWNER\n" +
                        "  FROM (SELECT OWNER, count(*) cnt\n" +
                        "          FROM dba_objects\n" +
                        "         WHERE object_type = '" + objectType.name() + "'\n" +
                        "           AND ('" + params.getI_prefix() +"' IS NULL OR UPPER (object_name) LIKE '" + params.getI_prefix().toUpperCase() + "%') \n" +
                        "           AND ('" + params.getI_schema_name() + "' IS NULL OR UPPER (owner) LIKE '" + params.getI_schema_name().toUpperCase() + "%') \n" +
                        "         GROUP BY owner)\n" +
                        " ORDER BY cnt DESC \n" +
                        " FETCH FIRST 50 ROWS ONLY";
                break;
            default:
                throw new RuntimeException("For ObjectPart = " + objectPart.name() + " query is not defined!");
        }
    }
}
