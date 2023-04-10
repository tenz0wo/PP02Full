package ru.inversion.migration_assistant.repo;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import ru.inversion.migration_assistant.model.DbObjectWithSchema;
import ru.inversion.migration_assistant.model.RequestParams;
import ru.inversion.migration_assistant.model.ResponseObj;

import java.sql.*;
import java.util.LinkedList;
import java.util.List;

@Service
@Slf4j
public class ConverterRepository{

    public Integer getConvert(RequestParams params) throws SQLException{
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
        connection.close();

        Integer res = callableStatement.getInt(1);
        connection.close();

        return res;
    }

    public String getConvertUi(RequestParams params) throws SQLException{
        Connection connection = prepareConnection(params);

        //передача параметров в оракл для получения id
        CallableStatement callableStatement = connection.prepareCall("{? = call ora2pg_pkg.convert_ui(?, ?, ?)}");
        callableStatement.registerOutParameter(1, Types.CLOB);
        callableStatement.setString(2, params.getI_prefix());
        callableStatement.setString(3, params.getI_schema_name());
        callableStatement.setString(4, params.getI_schema_name());
//        callableStatement.setString(4, params.getI_table_tablespace());
//        callableStatement.setString(5, params.getI_index_tablespace());
//        callableStatement.setString(6, params.getI_dbms());
        callableStatement.execute();
        String res = callableStatement.getString(1);
        connection.close();

        return res;
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
        String column = "OBJECT_NAME";
        return getResponseSimpleList (params, query, column);
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
        String column = "OWNER";
        return getResponseSimpleList (params, query, column);
    }

    private ResponseObj<List<String>> getResponseSimpleList (RequestParams params, String query, String column) throws SQLException {
        List<String> resp = new LinkedList<>();
        if (!params.getUrl().contains("oracle")) {
            return new ResponseObj<>(resp);
        }

        Connection connection = prepareConnection(params);
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(query);

        while (resultSet.next()) {
            resp.add(resultSet.getString(column));
        }

        connection.close();
        return new ResponseObj<>(resp);
    }

    private ResponseObj<List<DbObjectWithSchema>> getResponseOfObjWithSchema (RequestParams params, String query, DbObjectWithSchema columns) throws SQLException {
        List<DbObjectWithSchema> resp = new LinkedList<>();
        if (!params.getUrl().contains("oracle")) {
            return new ResponseObj<>(resp);
        }

        Connection connection = prepareConnection(params);
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(query);

        while (resultSet.next()) {
            DbObjectWithSchema obj = new DbObjectWithSchema ();
            obj.setObj(resultSet.getString(columns.getObj()));
            obj.setSchema(resultSet.getString(columns.getSchema()));
            resp.add(obj);
        }

        connection.close();
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

//---
//
//

    public ResponseObj<List<DbObjectWithSchema>> getTablesByPackage(RequestParams params) throws SQLException {
        prePopulateParams(params);
        String query =
                "with mytabs as (\n" +
                "                select tname, owner \n" +
                "                  from (\n" +
                "                       --прямая зависимость кода от таблиц\n" +
                "                       select d.referenced_name tname, d.owner\n" +
                "                         from dba_dependencies d\n" +
                "                        where d.owner = 'INVOPRO'\n" +
                "                          and d.referenced_owner = d.owner\n" +
                "                          and d.name in ('" + params.getI_prefix() + "') --пакеты для конвертации !!!\n" +
                "                          and d.referenced_type='TABLE'\n" +
                "                       union\n" +
                "                       --зависимость кода от таблиц через представления\n" +
                "                       select d.referenced_name tname, d.owner\n" +
                "                         from dba_dependencies d\n" +
                "                     where d.owner= '" + params.getI_schema_name() + "'\n" +
                "                       and d.referenced_owner = d.owner\n" +
                "                       and d.type='VIEW'\n" +
                "                       and (d.name, d.owner) in (select distinct d.referenced_name tname, d.owner\n" +
                "                                                   from dba_dependencies d\n" +
                "                                                  where d.owner = '" + params.getI_schema_name() + "'\n" +
                "                                                    and d.referenced_owner = d.owner\n" +
                "                                                    and d.name in ('" + params.getI_prefix() + "') --пакеты для конвертации !!!\n" +
                "                                                    and d.referenced_type='VIEW') --view\n" +
                "                       and d.referenced_type='TABLE'\n" +
                "                    ) --where tname not in ('USR','smr','cus','acc','ACC_DST') --ранее перенесенные таблицы\n" +
                "                  --where tname not in (SELECT cname FROM ORA2PG_EXP_TABLES WHERE CSHEMA='XXI' /*AND dconv<to_date('31.03.2023 00:00:00','DD.MM.RRRR HH24:MI:SS')*/)  --ранее перенесенные таблицы\n" +
                "               ),\n" +
                "      myrel as (\n" +
                "                select * " +
                "                  from (select distinct c.table_name t_child, c.owner, \n" +
                "                               (select c2.TABLE_NAME from dba_constraints c2 \n" +
                "                                 where c2.owner = '" + params.getI_schema_name() + "'\n" +
                "                                   and c2.CONSTRAINT_TYPE IN ('P','U')\n" +
                "                                   and c2.CONSTRAINT_NAME=c.R_CONSTRAINT_NAME) t_main\n" +
                "                          from dba_constraints c\n" +
                "                         where c.owner = '\" + params.getI_schema_name() + \"INVOPRO'\n" +
                "                           and c.R_owner = c.owner\n" +
                "                           and c.CONSTRAINT_TYPE = 'R')\n" +
                "                 where t_child!=t_main \n" +
                "                   and t_main in (select tname from mytabs)\n" +
                "               )\n" +
                "select TNAME, OWNER, \n" +
                "       (select NVL(max(level),0)\n" +
                "          from myrel\n" +
                "         start with myrel.t_child=tname\n" +
                "       connect by nocycle prior myrel.t_main = myrel.t_child\n" +
                "       ) IREL --\"глубина\" зависимостей в рамках выгружаемых для правильного порядка применения\n" +
                "  from mytabs\n" +
                " order by 2,1";
        DbObjectWithSchema columns = new DbObjectWithSchema("TNAME", "OWNER");
        return getResponseOfObjWithSchema (params, query, columns);
    }

}
