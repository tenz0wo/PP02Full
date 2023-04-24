package ru.inversion.migration_assistant.exec.repo;

import ru.inversion.migration_assistant.db.DBType;
import ru.inversion.migration_assistant.exec.DBExecutor;
import ru.inversion.migration_assistant.exec.ExecutorConsumer;
import ru.inversion.migration_assistant.exec.ExecutorParams;
import ru.inversion.migration_assistant.model.request.RequestTableColumn;
import ru.inversion.migration_assistant.model.response.DoubleParam;
import ru.inversion.migration_assistant.model.response.ResponseTableColumn;
import ru.inversion.migration_assistant.model.response.ResponseTableColumns;

import java.sql.ResultSet;
import java.util.LinkedList;
import java.util.List;

public class TableColumnExecutor extends DBExecutor<ResponseTableColumns> {
    final RequestTableColumn params;
    String query;

    public TableColumnExecutor(ExecutorParams<RequestTableColumn> params) {
        super(params);
        this.params = params.getWrappedParams();
    }

    @Override
    public ResponseTableColumns exec() throws Exception {
        ResponseTableColumns responseTableColumns = new ResponseTableColumns();
        defineQueries();
        executeQuery (query, createConsumer (responseTableColumns));
        return responseTableColumns;
    }

    ExecutorConsumer<ResultSet> createConsumer (ResponseTableColumns responseTableColumns) {
        List<DoubleParam> list = new LinkedList<>();
        responseTableColumns.setTableColumns(list);
        return resultSet -> {
            while (resultSet.next()) {
                DoubleParam tableColumn = new DoubleParam(resultSet.getString(1), resultSet.getString(2));

                responseTableColumns.getTableColumns().add(tableColumn);
            }
        };
    }

    void defineQueries () {
        String table = params.getTableName();
        String schema = params.getTableSchema();
        String colPrefix = params.getColPrefix().toUpperCase();

        if (dbType == DBType.ORACLE) {
            query = "SELECT COLUMN_NAME,\n" +
                    "       CASE\n" +
                    "         WHEN DATA_TYPE = 'NUMBER' AND DATA_PRECISION IS NULL AND\n" +
                    "              DATA_SCALE = 0 THEN\n" +
                    "          'INTEGER'\n" +
                    "         ELSE\n" +
                    "          DATA_TYPE\n" +
                    "       END || CASE\n" +
                    "         WHEN DATA_PRECISION IS NOT NULL AND COALESCE(DATA_SCALE, 0) > 0 THEN\n" +
                    "          '(' || DATA_PRECISION || ',' || DATA_SCALE || ')'\n" +
                    "         WHEN DATA_PRECISION IS NOT NULL AND COALESCE(DATA_SCALE, 0) = 0 THEN\n" +
                    "          '(' || DATA_PRECISION || ')'\n" +
                    "         WHEN DATA_PRECISION IS NULL AND DATA_SCALE IS NOT NULL AND\n" +
                    "              DATA_SCALE != 0 THEN\n" +
                    "          '(*,' || DATA_SCALE || ')'\n" +
                    "         WHEN CHAR_LENGTH > 0 THEN\n" +
                    "          '(' || CHAR_LENGTH || ')'\n" +
                    "       END COLUMN_TYPE\n" +
                    "  FROM DBA_TAB_COLUMNS c\n" +
                    " WHERE OWNER = '" + schema + "'\n" +
                    "   AND TABLE_NAME = '" + table +"'\n" +
                    "   AND COLUMN_NAME LIKE '%" + colPrefix + "%'" +
                    " ORDER BY COLUMN_ID";

            System.out.println("query");
            System.out.println(query);
        } else if (dbType == DBType.POSTGRES) {
            throw new RuntimeException("For DBType = " + dbType.name() + " query is not defined!");
        } else {
            throw new RuntimeException("For DBType = " + dbType.name() + " query is not defined!");
        }
    }
}
