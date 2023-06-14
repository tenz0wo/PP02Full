package ru.inversion.migration_assistant.service;

import ru.inversion.migration_assistant.model.models.*;

import java.io.IOException;
import java.util.*;
import java.sql.*;

public class ControllerDBLinks {
    String pathToMain = "C:\\Users\\Koryshev.INVERSION\\Desktop\\gvboishnbv'\\controller-db-links-back\\src\\main";

    Controllers controllers = new Controllers();
    ParsePath parsePath = new ParsePath();
    ParseController parseController = new ParseController();
    ParseQuery parseQuery = new ParseQuery();
    ParseLovs parseLovs = new ParseLovs();
    ArrayList<ControllerTable> listControllerTable = new ArrayList<>();
    List<LovTable> listControllerLovs = new ArrayList<>();
    Connection conn;

    ControllerDBLinks() throws IOException, SQLException {
        Controller controller = null;
        List<Controller> listControllers;
        ControllerFolder folder = null;
        List<ControllerFolder> folders = new ArrayList<>();
        try {
            connect();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        parsePath.findControllerPathFolders();

        for (String path : parsePath.controllerFolders.keySet()) {
            listControllers = new ArrayList<>();
            for (String pathFile : findPathFile(path)){
                fillHashTables(pathFile);
                if (listControllerTable.size() != 0 || listControllerLovs.size() != 0) {
                    controller = new Controller();
                    controller.setTables(listControllerTable);
                    controller.setLovTables(listControllerLovs);
                    controller.setPath(pathFile);
                    listControllers.add(controller);
                }
            }

            folder = new ControllerFolder();
            folder.setFolderPath(path);
            folder.setControllers(listControllers);
            folders.add(folder);
//            System.out.println(selectFolderId(folder));
            insertControllers(folder);
//            insertControllers(controller, folder);

//            insertFolder(folder);
        }

        controllers.setControllerList(folders);

        System.out.println("+");

        conn.close();

    }

    private void connect() throws SQLException {
        String url = "jdbc:postgresql://pgpro.corp.inversion.ru:5432/pgdev";
        conn = DriverManager.getConnection(url, "xxi", "casper");

        System.out.println(conn);
    }

    private void insertFolder(ControllerFolder folder) throws SQLException {
        String folderName = folder.getFolderPath();
        PreparedStatement st = conn.prepareStatement("INSERT INTO ora2pg.ku_folders (folder_name) VALUES (?)");
        st.setObject(1, folderName);
        st.executeUpdate();
    }

    private void insertControllers(ControllerFolder folder) throws SQLException {
        for (Controller controller : folder.getControllers()) {
            String conPath = controller.getPath();
            PreparedStatement stCons = conn.prepareStatement("INSERT INTO ora2pg.ku_controllers (folder_id, path) VALUES (?,?)");
            stCons.setObject(1, selectFolderId(folder));
            stCons.setString(2, conPath);
            stCons.executeUpdate();
            insertControllerTable(controller);
            insertLovTable(controller);
        }
    }

    private Integer selectFolderId(ControllerFolder folder) throws SQLException {
        Integer trueId = null;
        String folderName = folder.getFolderPath();
        PreparedStatement stFolder = conn.prepareStatement("select ora2pg.ku_folders.folder_id from ora2pg.ku_folders where folder_name=?");
        stFolder.setString(1, String.valueOf(folderName));
        ResultSet rs = stFolder.executeQuery();
        while (rs.next()) {
            trueId = rs.getInt(1);
        }
        rs.close();
        return trueId;
    }

    private void insertControllerTable(Controller controller) throws SQLException {
        for (ControllerTable controllerTable : controller.getTables()) {
            String ttable = controllerTable.getTable();
            String tquery = String.valueOf(controllerTable.getQuery());

            PreparedStatement stConsTbl = conn.prepareStatement("INSERT INTO ora2pg.ku_controller_table\n" +
                                                                "(\"name\", query, controller_id)\n" +
                                                                "VALUES(?,?,?);\n");
            stConsTbl.setString(1, ttable);
            stConsTbl.setString(2, tquery);
            stConsTbl.setObject(3, selectControllerId(controller));
            stConsTbl.executeUpdate();
        }
    }

    private void insertLovTable(Controller controller) throws SQLException {
        for (LovTable lovTable : controller.getLovTables()) {
            String ltable = lovTable.getTable();
            String lquery = String.valueOf(lovTable.getQuery());

            PreparedStatement stConsTbl = conn.prepareStatement("INSERT INTO ora2pg.ku_lov_table\n" +
                    "(\"name\", query, controller_id)\n" +
                    "VALUES(?,?,?);\n");
            stConsTbl.setString(1, ltable);
            stConsTbl.setString(2, lquery);
            stConsTbl.setObject(3, selectControllerId(controller));
            stConsTbl.executeUpdate();
        }
    }

    private Integer selectControllerId(Controller controller) throws SQLException {
        Integer trueId = null;
        String controllerName = controller.getPath();
        PreparedStatement stFolder = conn.prepareStatement("select ora2pg.ku_controllers.controller_id from ora2pg.ku_controllers where path=?");
        stFolder.setString(1, String.valueOf(controllerName));
        ResultSet rs = stFolder.executeQuery();
        while (rs.next()) {
            trueId = rs.getInt(1);
        }
        rs.close();
        return trueId;
    }

    private ArrayList<String> findPathFile(String pathFolder) throws IOException {
        return parsePath.findControllerPathFiles(pathFolder);
    }

    private void fillHashTables(String pathController) throws IOException {
        HashMap<String, String> tableQuery = new HashMap<>();
        listControllerTable = new ArrayList<>();
        listControllerLovs = new ArrayList<>();
        if (pathController.substring(pathController.lastIndexOf("/")).contains("View")){
            parseController.findTables(pathController);

            for (String table : parseController.listTable) {
                String pathTable = createPathTable(pathController, table);
                String query = executeQuery(pathTable);

                if (query != null) {
                    addQueryToList(tableQuery, table, query);
                    addControllerTable(table, tableQuery);
                } else {
                    addNoneQueryToList(tableQuery, table);
                    addNoneControllerTable(pathController, table);
                }
                tableQuery = new HashMap<>();
            }
        } else {
            parseLovs.findLovs(pathController);
            listControllerLovs = parseLovs.lovTables;
        }


    }

    private String createPathTable(String pathController, String content) {
        return pathController.substring(0, pathController.lastIndexOf("/")) + "/" + content.replaceAll("\\[", "").replaceAll("\\]", "") + ".java";
    }

    private String executeQuery(String path) {
        return parseQuery.executeQuery(pathToMain+"\\result\\FXKu\\" + path);
    }

    private void addQueryToList(HashMap<String, String> tableQuery, String tableName, String query) {
        tableQuery.put(tableName, parseQuery.cutQuery(query));
    }

    private void addControllerTable(String content, HashMap<String, String> tableQuery) {
        ControllerTable controllerTable = new ControllerTable();
        controllerTable.setTable(content);
        controllerTable.setQuery(new ArrayList<>(tableQuery.values()));
//        showInfo(controllerTable);

        listControllerTable.add(controllerTable);
    }

    private void addNoneQueryToList(HashMap<String, String> tableQuery, String tableName) {
        tableQuery.put(tableName, null);
    }


    private void addNoneControllerTable(String pathController, String content) {
        ControllerTable controllerTable = new ControllerTable();
        controllerTable.setTable(content);
        controllerTable.setQuery(null);
//        showInfo(controllerTable);

        listControllerTable.add(controllerTable);
    }

    //    private void addNoneLovQueryToList(HashMap<String, String> lovQuery, String tableName) {
//        lovQuery.put(tableName, null);
//    }

//    private void addLovQueryToList(HashMap<String, String> lovQuery, String tableName, String query) {
//        lovQuery.put(tableName, parseQuery.cutQuery(query));
//    }

//    private void addLovTable(String content, HashMap<String, String> lovQuery) {
//        LovTable lovTable = new LovTable();
//        lovTable.setLov(content);
//        lovTable.setQuery(new ArrayList<>(lovQuery.values()));
//        showLovs(lovTable);
//    }



//    private void addControllersToControllerList(ArrayList<ControllerFolder> listController) {
//        controllers.setControllerList(listController);
//    }
//
//    private void showInfo(ControllerTable controllerTable){
//        System.out.println(controllerTable.getTable());
//        System.out.println(controllerTable.getQuery());
//    }
//
//    private void showLovs(LovTable lovTable){
//        System.out.println(lovTable.getPathLov());
//        System.out.println(lovTable.getTable());
//        System.out.println(lovTable.getQuery());
//    }
}
