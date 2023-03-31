package ru.inversion.repo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.inversion.model.RequestParams;
import ru.inversion.model.ResponseObj;

import java.io.*;
import java.sql.*;

@Service
@Slf4j
public class ConverterRepository{

    public ResponseObj getConvert(RequestParams params) throws SQLException{
        ResponseObj response = new ResponseObj();
        Connection connectionOracle = DriverManager.getConnection("jdbc:oracle:thin:@alcor:1521:dev8i","ora2pg", "ora2pg");
//        Connection connectionPostgr = DriverManager.getConnection("jdbc:postgresql://192.168.32.11:5432/pgdev", "xxi", "casper");

        //передача параметров в оракл для получения id
        CallableStatement callableStatement = connectionOracle.prepareCall("{? = call ora2pg_pkg.convert(?)}");
//        CallableStatement callableStatement = connectionOracle.prepareCall("{? = call ora2pg_pkg.convert(?, ?, ?, ?, ?)}");
        callableStatement.registerOutParameter(1, Types.INTEGER);
        callableStatement.setString(2, params.getI_prefix());
//        callableStatement.setString(3, params.getI_schema_name());
//        callableStatement.setString(4, params.getI_table_tablespace());
//        callableStatement.setString(5, params.getI_index_tablespace());
//        callableStatement.setString(6, params.getI_dbms());
        callableStatement.execute();
        int i = callableStatement.getInt(1);

        response.setResult(i);
        return response;
    }

    public ResponseObj getConvertUi(RequestParams params) throws SQLException{
        ResponseObj response = new ResponseObj();
        Connection connectionOracle = DriverManager.getConnection("jdbc:oracle:thin:@alcor:1521:dev8i","ora2pg", "ora2pg");
//        Connection connectionPostgr = DriverManager.getConnection("jdbc:postgresql://192.168.32.11:5432/pgdev", "xxi", "casper");

        //передача параметров в оракл для получения id
        CallableStatement callableStatement = connectionOracle.prepareCall("{? = call ora2pg_pkg.convert_ui(?)}");
        callableStatement.registerOutParameter(1, Types.CLOB);
        callableStatement.setString(2, params.getI_prefix());
//        callableStatement.setString(3, params.getI_schema_name());
//        callableStatement.setString(4, params.getI_table_tablespace());
//        callableStatement.setString(5, params.getI_index_tablespace());
//        callableStatement.setString(6, params.getI_dbms());
        callableStatement.execute();
        String i = callableStatement.getString(1);

        response.setResult(i);
        return response;
    }
}
