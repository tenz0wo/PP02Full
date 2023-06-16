package ru.inversion.migration_assistant.exec.repo;

import ru.inversion.migration_assistant.exec.DBExecutor;
import ru.inversion.migration_assistant.exec.ExecutorConsumer;
import ru.inversion.migration_assistant.exec.ExecutorParams;
import ru.inversion.migration_assistant.model.models.*;

import java.sql.ResultSet;
import java.sql.SQLException;

public class InsertOra2Pg extends DBExecutor<ResponseControllers> {

    final ResponseControllers params;

    String query;

    String queryDelFolders = "delete from ora2pg.ku_folders where folder_id > 0";
    String queryDelControllers = "delete from ora2pg.ku_controllers where controller_id > 0";
    String queryDelControllerTable = "delete from ora2pg.ku_controller_table where id_table > 0";
    String queryDelLOV = "delete from ora2pg.ku_lov_table where lov_id > 0";

    public InsertOra2Pg(ExecutorParams<ResponseControllers> params) {
        super(params);
        this.params = params.getWrappedParams();
    }

    @Override
    public ResponseControllers exec() throws Exception {
        ResponseControllers responseControllers = new ResponseControllers();

        deleteTables();
//        clearInc();

        for (ControllerFolder folder : params.getFolders()){
            defineQueriesInsertFolder(folder);
            executeUpdate(query);
            System.out.println("[!] folder: "+ query);
            for (Controller controller: folder.getControllers()){
                defineQueriesSelectFolderId(folder);
                ID idFolder = new ID();
                executeQuery(query, createConsumer(idFolder));
                System.out.println("[!] folder id: "+ query);

                defineQueriesInsertControllers(idFolder.getId(), controller.getPath());
                executeUpdate(query);
                for (ControllerTable controllerTable : controller.getTables()){
                    defineQueriesSelectControllerId(controller);
                    ID idController = new ID();
                    executeQuery(query, createConsumer(idController));
                    System.out.println("[!] controller id: "+ query);

                    defineQueriesInsertControllerTable(controllerTable.getTable(), String.valueOf(controllerTable.getQuery()), idController.getId());
                    executeUpdate(query);
                }
                for (LovTable lovTable : controller.getLovTables()){
                    defineQueriesSelectControllerId(controller);
                    ID idLov = new ID();
                    System.out.println("[!] lov id: "+ query);
                    executeQuery(query, createConsumer(idLov));

                    defineQueriesInsertLovTable(lovTable.getTable(), lovTable.getQuery(), idLov.getId());
                    executeUpdate(query);
                }
            }
        }

        return responseControllers;
    }


    ExecutorConsumer<ResultSet> createConsumer(ID id) {
        return resultSet -> {
            resultSet.next();
            id.setId(resultSet.getString(1));

//            boolean res;
//            if (dbType == DBType.ORACLE) {
//                res = resultSet.getInt(1) == 1;
//            } else {
//                res = resultSet.getBoolean(1);
//            }
//            switch (checkType) {
//                case EXISTS:
//                    responseCheckTable.setTableExists(res);
//                    break;
//                case NOT_EMPTY:
//                    responseCheckTable.setTableNotEmpty(res);
//                    break;
//            }
        };
    }

    void defineQueries() {
//        TableValues tableValues = new TableValues();
//        tableValues.setTableName (params.getTable(), params.isSwitchTableCase());
//        tableValues.setSchemaName (params.getSchema(), params.isSwitchSchemaCase());
//        tableValues.setTableNameForQuery (params.getTable(), params.isSwitchTableCase());
//        if (dbType == DBType.ORACLE) {
//            queryExists = "SELECT LEAST(COUNT(*),1)\n" +
//                          "  FROM all_tables\n" +
//                          " WHERE owner = '" + tableValues.getSchemaName() + "'\n" +
//                          "   AND table_name = '" + tableValues.getTableName() + "'";
//
//            queryNotEmpty = "SELECT LEAST(COUNT(*),1)\n" +
//                            "  FROM DUAL\n" +
//                            " WHERE EXISTS (SELECT NULL FROM " + tableValues.getSchemaName() + "." + tableValues.getTableNameForQuery() + ")";
//        } else if (dbType == DBType.POSTGRES) {
//            queryExists = "SELECT EXISTS (SELECT 1 FROM pg_tables WHERE schemaname = '" + tableValues.getSchemaName() + "' AND tablename = '" + tableValues.getTableName() + "') AS table_exists";
//            queryNotEmpty = "SELECT EXISTS (SELECT 1 FROM " + tableValues.getSchemaName() + "." + tableValues.getTableNameForQuery() + " LIMIT 1) AS table_not_empty";
//        } else {
//            throw new RuntimeException("For DBType = " + dbType.name() + " query is not defined!");
//        }
    }


    private void defineQueriesInsertFolder(ControllerFolder folder) throws SQLException {
        query = "INSERT INTO ora2pg.ku_folders (folder_name) VALUES ('"+folder.getFolderPath()+"')";
    }

    private void defineQueriesInsertControllers(String id, String path) throws SQLException {
        query = "INSERT INTO ora2pg.ku_controllers (folder_id, path) VALUES ("+id+",'"+path+"')";
    }

    private void defineQueriesSelectFolderId(ControllerFolder folder) throws SQLException {
        query = "select ora2pg.ku_folders.folder_id from ora2pg.ku_folders where folder_name='"+folder.getFolderPath()+"'";
    }

    private void defineQueriesInsertControllerTable(String name, String queryTable, String id) throws SQLException {
        if (queryTable != null){
            queryTable = queryTable.replaceAll("\\[", "").replaceAll("]", "").replaceAll("'", "");
        }
        query = "INSERT INTO ora2pg.ku_controller_table\n" +
                "(\"name\", query, controller_id)\n" +
                "VALUES('"+name+"','"+queryTable+"',"+id+");\n";
    }

    private void defineQueriesInsertLovTable(String name, String queryTable, String id) throws SQLException {
        if (queryTable != null){
            queryTable = queryTable.replaceAll("\\[", "").replaceAll("]", "").replaceAll("'", "");
        }
        query = "INSERT INTO ora2pg.ku_lov_table\n" +
                "(\"name\", query, controller_id)\n" +
                "VALUES('"+name+"','"+queryTable+"',"+id+");\n";
    }

    private void defineQueriesSelectControllerId(Controller controller) throws SQLException {
        query = "select ora2pg.ku_controllers.controller_id from ora2pg.ku_controllers where path='"+controller.getPath()+"'";
    }


    private void deleteTables() throws Exception {
        executeUpdate(queryDelLOV);
        executeUpdate(queryDelControllerTable);
        executeUpdate(queryDelControllers);
        executeUpdate(queryDelFolders);
    }
}
