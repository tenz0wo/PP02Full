package ru.inversion.migration_assistant.exec.repo;

import ru.inversion.migration_assistant.db.DBType;
import ru.inversion.migration_assistant.exec.ExecutorConsumer;
import ru.inversion.migration_assistant.exec.ExecutorParams;
import ru.inversion.migration_assistant.model.request.RequestParams;
import ru.inversion.migration_assistant.model.response.DbObjectWithSchema;

import java.sql.ResultSet;
import java.util.LinkedList;
import java.util.List;

public class TablesByPackageExecutor extends RequestParamsExecutor<List<DbObjectWithSchema>> {

    String query;
    DbObjectWithSchema columns;
    final List<DbObjectWithSchema> tablesByPackageList = new LinkedList<>();


    public TablesByPackageExecutor(ExecutorParams<RequestParams> executorParams) {
        super(executorParams);
    }

    @Override
    public List<DbObjectWithSchema> exec() throws Exception {
        defineQuery();
        defineColumns();
        executeQuery(query, createConsumer());
        return tablesByPackageList;
    }

    ExecutorConsumer<ResultSet> createConsumer () {
        return resultSet -> {
            while (resultSet.next()) {
                DbObjectWithSchema obj = new DbObjectWithSchema ();
                obj.setObj(resultSet.getString(columns.getObj()));
                obj.setSchema(resultSet.getString(columns.getSchema()));
                tablesByPackageList.add(obj);
            }
        };
    }

    void defineQuery() {
        DBType dbType = DBType.defineByUrl(params.getUrl());
        if (dbType != DBType.ORACLE) {
            throw new RuntimeException("For DBType = " + dbType.name() + " query is not defined!");
        }
        query = "with mytabs as (\n" +
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
    }

    void defineColumns() {
        columns = new DbObjectWithSchema("TNAME", "OWNER");
    }
}
