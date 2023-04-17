package ru.inversion.migration_assistant.exec.repo;

import ru.inversion.migration_assistant.db.DBType;
import ru.inversion.migration_assistant.exec.ExecutorConsumer;
import ru.inversion.migration_assistant.model.request.RequestParams;
import ru.inversion.migration_assistant.model.response.DoubleParam;
import ru.inversion.migration_assistant.model.sql.ObjectPart;
import ru.inversion.migration_assistant.model.sql.ObjectType;

import java.sql.ResultSet;
import java.util.LinkedList;
import java.util.List;

public class DoubleColumnListExecutor  extends RequestParamsExecutor<List<DoubleParam>> {
    String query = "";
    String column = "";
    String owners = "";
    final List<DoubleParam> responseDoubleColumnList = new LinkedList<>();
    final ObjectPart objectPart;
    final ObjectType objectType;

    public DoubleColumnListExecutor (ColumnListParams<RequestParams> params) {
        super(params);
        this.objectPart = params.getObjectPart();
        this.objectType = params.getObjectType();
    }

    @Override
    public List<DoubleParam> exec() throws Exception {
        defineQuery();
        defineColumn();
        executeQuery (query, createConsumer ());
        return responseDoubleColumnList;
    }

    ExecutorConsumer<ResultSet> createConsumer () {
        return resultSet -> {
            while (resultSet.next()) {
                responseDoubleColumnList.add(new DoubleParam(resultSet.getString(column), resultSet.getString(owners)));
            }
        };
    }

    void defineColumn() {
        switch (objectPart) {
            case OBJECT:
                column = "OBJECT_NAME";
                owners = "OWNERS";
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
        if (objectPart == ObjectPart.OBJECT) {
            query = "SELECT OBJECT_NAME, '(' || LISTAGG (OWNER, ', ') within group (order by OWNER) || ')' OWNERS \n" +
                    "  FROM DBA_OBJECTS \n" +
                    " WHERE OBJECT_TYPE = '" + objectType.name() + "' \n" +
                    "   AND ('" + params.getI_prefix() + "' IS NULL OR UPPER (object_name) LIKE '" + params.getI_prefix().toUpperCase() + "%') \n" +
                    "   AND ('" + params.getI_schema_name() + "' IS NULL OR UPPER (owner) LIKE '" + params.getI_schema_name().toUpperCase() + "%') \n" +
                    " GROUP BY OBJECT_NAME \n" +
                    " ORDER BY UPPER (OBJECT_NAME), LENGTH (UPPER (OBJECT_NAME)), OBJECT_NAME \n" +
                    " FETCH FIRST 50 ROWS ONLY";
        } else {
            throw new RuntimeException("For ObjectPart = " + objectPart.name() + " query is not defined!");
        }
    }
}
