package ru.inversion.migration_assistant.exec.repo;

import ru.inversion.migration_assistant.db.DBType;
import ru.inversion.migration_assistant.exec.DBExecutor;
import ru.inversion.migration_assistant.exec.ExecutorConsumer;
import ru.inversion.migration_assistant.exec.ExecutorParams;
import ru.inversion.migration_assistant.model.request.RequestDependencies;
import ru.inversion.migration_assistant.model.response.ResponseDependencies;
import springfox.documentation.spring.web.json.Json;

import java.sql.ResultSet;

public class DependenciesExecutor extends DBExecutor<ResponseDependencies> {

    final RequestDependencies params;
    String query;

    public DependenciesExecutor(ExecutorParams<RequestDependencies> params) {
        super(params);
        this.params = params.getWrappedParams();
    }

    @Override
    public ResponseDependencies exec() throws Exception {
        ResponseDependencies responseDependencies = new ResponseDependencies();
        defineQueries();
        executeQuery (query, createConsumer (responseDependencies));
        return responseDependencies;
    }

    ExecutorConsumer<ResultSet> createConsumer (ResponseDependencies responseDependencies) {
        return resultSet -> {
            String data = "";
            while (resultSet.next()) {
                data += resultSet.getString(1);
            }
            Json responseData = new Json(data);
            responseDependencies.setData(responseData);

        };
    }

    void defineQueries () {
        String owner = params.getOwner();
        String referencedOwner = params.getReferencedOwner();
        String name = params.getName();
        String type = params.getType();

        if (dbType == DBType.ORACLE) {
            query = "WITH connect_by_query as (\n" +
                    "  select ROWNUM as rnum,\n" +
                    "         d.name as parent_name,\n" +
                    "         d.referenced_name,\n" +
                    "         d.referenced_type,\n" +
                    "         d.referenced_owner,\n" +
                    "         LEVEL as Lvl\n" +
                    "    from (select *\n" +
                    "            from dba_dependencies\n" +
                    "           where owner = '" + owner + "' and referenced_owner = '" + referencedOwner + "') d\n" +
                    "start with d.name = '" + name + "' and d.type = '" + type + "'\n" +
                    " connect by nocycle prior d.referenced_name = d.name \n" +
                    "                and prior d.referenced_type = d.type\n" +
                    "                and prior d.referenced_owner = d.owner\n" +
                    ")\n" +
                    "select json_body\n" +
                    "  from (\n" +
                    "        select 0 as global_num, \n" +
                    "               0 as rnum, \n" +
                    "               '{\"dependencies\": [' json_body \n" +
                    "          from dual\n" +
                    "        union all\n" +
                    "        select 1 as global_num, rnum,\n" +
                    "               CASE \n" +
                    "                 WHEN Lvl = 1 and rnum = 1 THEN '{'\n" +
                    "                 WHEN Lvl = 1 and rnum > 1 THEN ',{'\n" +
                    "                 WHEN Lvl - LAG(Lvl) OVER (order by rnum) = 1 THEN ',\"children\" : [{' \n" +
                    "                 ELSE ',{' \n" +
                    "               END \n" +
                    "               || '\"name\" : \"' || referenced_name || '\", '\n" +
                    "               || '\"type\" : \"' || referenced_type || '\", '\n" +
                    "               || '\"owner\" : \"' || referenced_owner || '\"'\n" +
                    "               || CASE WHEN LEAD(Lvl, 1, 1) OVER (order by rnum) - Lvl <= 0 \n" +
                    "                  THEN '}' || rpad( ' ', 1+ (-2 * (LEAD(Lvl, 1, 1) OVER (order by rnum) - Lvl)), ']}' )\n" +
                    "                  ELSE NULL \n" +
                    "               END as json_body\n" +
                    "          from connect_by_query\n" +
                    "        union all\n" +
                    "        select 9 as global_num, \n" +
                    "               9 as rnum, \n" +
                    "               ']}' as json_body \n" +
                    "          from dual)\n" +
                    " order by global_num, rnum";

        } else if (dbType == DBType.POSTGRES) {
            throw new RuntimeException("For DBType = " + dbType.name() + " query is not defined!");
        } else {
            throw new RuntimeException("For DBType = " + dbType.name() + " query is not defined!");
        }
    }
}
