package ru.inversion.migration_assistant.repo;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import ru.inversion.migration_assistant.model.RequestParams;
import ru.inversion.migration_assistant.model.ResponseObj;

import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@Service
@Slf4j
public class ConverterRepository{

    public ResponseObj<Integer> getConvert(RequestParams params) throws SQLException{
        Connection connection = prepareConnection(params);

        //передача параметров в оракл для получения id
        CallableStatement callableStatement = connection.prepareCall("{? = call ora2pg_pkg.convert(?, ?)}");
//        CallableStatement callableStatement = connectionOracle.prepareCall("{? = call ora2pg_pkg.convert(?, ?, ?, ?, ?)}");
        callableStatement.registerOutParameter(1, Types.INTEGER);
        callableStatement.setString(2, params.getI_prefix());
        callableStatement.setString(3, params.getI_schema_name());
//        callableStatement.setString(3, params.getI_schema_name());
//        callableStatement.setString(4, params.getI_table_tablespace());
//        callableStatement.setString(5, params.getI_index_tablespace());
//        callableStatement.setString(6, params.getI_dbms());
        callableStatement.execute();
        Integer resp = callableStatement.getInt(1);

        return new ResponseObj<>(resp);
    }

    public ResponseObj<String> getConvertUi(RequestParams params) throws SQLException{
        Connection connection = prepareConnection(params);

        //передача параметров в оракл для получения id
        CallableStatement callableStatement = connection.prepareCall("{? = call ora2pg_pkg.convert_ui(?, ?)}");
        callableStatement.registerOutParameter(1, Types.CLOB);
        callableStatement.setString(2, params.getI_prefix());
        callableStatement.setString(3, params.getI_schema_name());
//        callableStatement.setString(3, params.getI_schema_name());
//        callableStatement.setString(4, params.getI_table_tablespace());
//        callableStatement.setString(5, params.getI_index_tablespace());
//        callableStatement.setString(6, params.getI_dbms());
        callableStatement.execute();
        String resp = callableStatement.getString(1);

        return new ResponseObj<>(resp);
    }

    Connection prepareConnection (RequestParams params) throws SQLException {
        return DriverManager.getConnection(params.getUrl(),params.getUser(), params.getPassword());
    }

    public ResponseObj<List<String>> getTableList(RequestParams params) throws SQLException{
        return getObjectList(params, "TABLE");
    }

    public ResponseObj<List<String>> getTableSchemaList(RequestParams params) throws SQLException{
        return getObjectSchemaList(params, "TABLE");
    }

    public ResponseObj<List<String>> getPackageList(RequestParams params) throws SQLException {
        return getObjectList(params, "PACKAGE");
    }

    public ResponseObj<List<String>> getPackageSchemaList(RequestParams params) throws SQLException {
        return getObjectSchemaList(params, "PACKAGE");
    }

    public ResponseObj<List<String>> getObjectList(RequestParams params, String objectType) throws SQLException {
        prePopulateParams(params);
        String query =  "SELECT DISTINCT OBJECT_NAME \n" +
                        "  FROM DBA_OBJECTS \n" +
                        " WHERE OBJECT_TYPE = '" + objectType + "' \n" +
                        "   AND ('" + params.getI_prefix() +"' IS NULL OR UPPER (object_name) LIKE '" + params.getI_prefix().toUpperCase() + "%') \n" +
                        "   AND ('" + params.getI_schema_name() + "' IS NULL OR UPPER (owner) LIKE '" + params.getI_schema_name().toUpperCase() + "%') \n" +
                        " ORDER BY UPPER (OBJECT_NAME), LENGTH (UPPER (OBJECT_NAME)) \n" +
                        " FETCH FIRST 50 ROWS ONLY";
        List<String> columns = List.of("OBJECT_NAME");
        return getResponseSimpleList (params, query, columns);
    }

    public ResponseObj<List<String>> getObjectSchemaList(RequestParams params, String objectType) throws SQLException {
        prePopulateParams(params);
        String query =  "SELECT OWNER\n" +
                        "  FROM (SELECT OWNER, count(*) cnt\n" +
                        "          FROM dba_objects\n" +
                        "         WHERE object_type = '" + objectType + "'\n" +
                        "           AND ('" + params.getI_prefix() +"' IS NULL OR UPPER (object_name) LIKE '" + params.getI_prefix().toUpperCase() + "%') \n" +
                        "           AND ('" + params.getI_schema_name() + "' IS NULL OR UPPER (owner) LIKE '" + params.getI_schema_name().toUpperCase() + "%') \n" +
                        "         GROUP BY owner)\n" +
                        " ORDER BY cnt DESC \n" +
                        " FETCH FIRST 50 ROWS ONLY";
        List<String> columns = List.of("OWNER");
        return getResponseSimpleList (params, query, columns);
    }

    private ResponseObj<List<String>> getResponseSimpleList (RequestParams params, String query, List<String> columns) throws SQLException {
        List<String> resp = new LinkedList<>();
        if (!params.getUrl().contains("oracle")) {
            return new ResponseObj<>(resp);
        }

        Connection connection = prepareConnection(params);
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(query);

        while (resultSet.next()) {
            for (String colLabel: columns) {
                resp.add(resultSet.getString(colLabel));
            }
        }

        return new ResponseObj<>(resp);
    }

    private void prePopulateParams (RequestParams params) {
        if (StringUtils.isBlank(params.getI_prefix())) {
            params.setI_prefix("");
        }
        if (StringUtils.isBlank(params.getI_schema_name())) {
            params.setI_schema_name("");
        }
    }

}
