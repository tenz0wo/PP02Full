package ru.inversion.migration_assistant.exec.repo;

        import ru.inversion.migration_assistant.db.DBType;
        import ru.inversion.migration_assistant.exec.DBExecutor;
        import ru.inversion.migration_assistant.exec.ExecutorConsumer;
        import ru.inversion.migration_assistant.exec.ExecutorParams;
        import ru.inversion.migration_assistant.model.common.DbConnectionParams;
        import ru.inversion.migration_assistant.model.response.ColumnHints;
        import ru.inversion.migration_assistant.model.response.ResponseColumnsHints;

        import java.sql.ResultSet;
        import java.util.LinkedList;
        import java.util.List;

public class ColumnHintsExecutor extends DBExecutor<ResponseColumnsHints> {
    final DbConnectionParams params;
    String query;

    public ColumnHintsExecutor(ExecutorParams<DbConnectionParams> params) {
        super(params);
        this.params = params.getWrappedParams();
    }

    @Override
    public ResponseColumnsHints exec() throws Exception {
        ResponseColumnsHints responseColumnsHints = new ResponseColumnsHints();
        defineQueries();
        executeQuery (query, createConsumer (responseColumnsHints));
        return responseColumnsHints;
    }

    ExecutorConsumer<ResultSet> createConsumer (ResponseColumnsHints responseColumnsHints) {
        List<ColumnHints> list = new LinkedList<>();
        responseColumnsHints.setColumnsHints(list);
        return resultSet -> {
            while (resultSet.next()) {
                ColumnHints tableColumn = new ColumnHints();
                tableColumn.setTableName(resultSet.getString(1));
                tableColumn.setTableOwner(resultSet.getString(2));
                tableColumn.setColumnName(resultSet.getString(3));
                tableColumn.setColumnDataType(resultSet.getString(4));
                tableColumn.setOriginalDataType(resultSet.getString(5));

                responseColumnsHints.getColumnsHints().add(tableColumn);
            }
        };
    }

    void defineQueries () {
        if (dbType == DBType.ORACLE) {
            query = "SELECT h.TABLE_NAME, h.TABLE_OWNER, h.COLUMN_NAME, h.COLUMN_DATA_TYPE, \n" +
                    "       CASE \n" +
                    "         WHEN c.DATA_TYPE = 'NUMBER' AND c.DATA_PRECISION IS NULL AND c.DATA_SCALE = 0 THEN\n" +
                    "           'INTEGER'\n" +
                    "         ELSE\n" +
                    "           c.DATA_TYPE\n" +
                    "       END\n" +
                    "       || \n" +
                    "       CASE\n" +
                    "         WHEN c.DATA_PRECISION IS NOT NULL AND COALESCE(c.DATA_SCALE, 0) > 0 THEN\n" +
                    "          '(' || c.DATA_PRECISION || ',' || c.DATA_SCALE || ')'\n" +
                    "         WHEN c.DATA_PRECISION IS NOT NULL AND COALESCE(c.DATA_SCALE, 0) = 0 THEN\n" +
                    "          '(' || c.DATA_PRECISION || ')'\n" +
                    "         WHEN c.DATA_PRECISION IS NULL AND c.DATA_SCALE IS NOT NULL AND c.DATA_SCALE != 0 THEN\n" +
                    "          '(*,' || c.DATA_SCALE || ')'\n" +
                    "         WHEN c.CHAR_LENGTH > 0 THEN\n" +
                    "          '(' || c.CHAR_LENGTH || ')'\n" +
                    "       END ORIGINAL_DATA_TYPE\n" +
                    "  FROM o2p_column_hint h LEFT JOIN DBA_TAB_COLUMNS c\n" +
                    "    ON h.TABLE_OWNER = c.OWNER\n" +
                    "   AND h.TABLE_NAME = c.TABLE_NAME" +
                    "   AND h.COLUMN_NAME = c.COLUMN_NAME";
        } else if (dbType == DBType.POSTGRES) {
            throw new RuntimeException("For DBType = " + dbType.name() + " query is not defined!");
        } else {
            throw new RuntimeException("For DBType = " + dbType.name() + " query is not defined!");
        }
    }
}
