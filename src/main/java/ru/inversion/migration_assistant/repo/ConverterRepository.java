package ru.inversion.migration_assistant.repo;

import lombok.extern.slf4j.Slf4j;
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

//        Connection connectionOracle = DriverManager.getConnection("jdbc:oracle:thin:@alcor:1521:dev8i","ora2pg", "ora2pg");
//        Connection connectionPostgr = DriverManager.getConnection("jdbc:postgresql://192.168.32.11:5432/pgdev", "xxi", "casper");

        Connection connection = prepareConnection(params);

        //передача параметров в оракл для получения id
        CallableStatement callableStatement = connection.prepareCall("{? = call ora2pg_pkg.convert(?)}");
//        CallableStatement callableStatement = connectionOracle.prepareCall("{? = call ora2pg_pkg.convert(?, ?, ?, ?, ?)}");
        callableStatement.registerOutParameter(1, Types.INTEGER);
        callableStatement.setString(2, params.getI_prefix());
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
//        Connection connectionPostgr = DriverManager.getConnection("jdbc:postgresql://192.168.32.11:5432/pgdev", "xxi", "casper");

        //передача параметров в оракл для получения id
        CallableStatement callableStatement = connection.prepareCall("{? = call ora2pg_pkg.convert_ui(?)}");
        callableStatement.registerOutParameter(1, Types.CLOB);
        callableStatement.setString(2, params.getI_prefix());
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
        List<String> resp = new LinkedList<>();
        if (!params.getUrl().contains("oracle")) {
            return new ResponseObj<>(resp);
        }
        Connection connection = prepareConnection(params);
        Statement statement = connection.createStatement();
        String query = "SELECT OBJECT_NAME FROM DBA_OBJECTS WHERE OWNER = 'XXI' AND OBJECT_TYPE = 'TABLE' AND UPPER(OBJECT_NAME) LIKE '" + params.getI_prefix().toUpperCase() + "%' order by UPPER(OBJECT_NAME), LENGTH(UPPER(OBJECT_NAME)) FETCH FIRST 50 ROWS ONLY";
        ResultSet resultSet = statement.executeQuery(query);
        while (resultSet.next()) {
            resp.add(resultSet.getString("OBJECT_NAME"));
        }

        return new ResponseObj<>(resp);
    }
}
