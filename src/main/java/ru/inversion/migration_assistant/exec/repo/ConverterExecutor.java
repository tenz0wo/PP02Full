package ru.inversion.migration_assistant.exec.repo;

import ru.inversion.migration_assistant.exec.ExecutorParams;
import ru.inversion.migration_assistant.model.request.RequestParams;
import ru.inversion.migration_assistant.model.response.TableDto;
import ru.inversion.migration_assistant.model.response.TablesDto;
import ru.inversion.priv.tools.dcont.DCont;
import ru.inversion.priv.tools.mdom.MDom;

import java.sql.Types;
import java.util.concurrent.atomic.AtomicReference;

public class ConverterExecutor extends RequestParamsExecutor<TablesDto> {

    public ConverterExecutor(ExecutorParams<RequestParams> executorParams) {
        super(executorParams);
    }

    @Override
    public TablesDto exec() throws Exception {
        return convertTable();
    }

    TablesDto convertTable () throws Exception {
        String result = convert4UI();
        return mapResponseOnTables(result);
    }

    String convert4UI() throws Exception {
        AtomicReference<String> convertResult = new AtomicReference<>("");
        String callable = "{? = call ora2pg_pkg.convert_ui(?, ?, ?)}";
        executeCallable(
                callable,
                callableStatement -> {
                    callableStatement.registerOutParameter(1, Types.CLOB);
                    callableStatement.setString(2, params.getI_prefix());
                    callableStatement.setString(3, params.getI_schema_name());
                    callableStatement.setString(4, params.getI_target_schema_name());
                },
                callableStatement -> convertResult.set(callableStatement.getString(1)));
        return convertResult.get();
    }

    TablesDto mapResponseOnTables(String tableConvertResult) {
        TablesDto tables = new TablesDto();
        DCont dc = new MDom();
        dc.loadXml(tableConvertResult);
        tables.setVersion(dc.e("version").asStr());
        tables.setPrefix(dc.e("prefix").asStr());
        String commonSchema = dc.e("source_shema_name").asStr();
        if (commonSchema.isBlank()) {
            commonSchema = params.getI_schema_name();
        }
        tables.setSchema(commonSchema);
        DCont dcTable = dc.e("table");
        TableDto table = new TableDto();
        table.setSchemaName(dcTable.e("source_shema_name").asStr());
        table.setTableName(dcTable.e("table_name").asStr());
        table.setDdlTabPg(dcTable.e("ddl_tab_pg").asStr());
        table.setDdlConPg(dcTable.e("ddl_con_pg").asStr());
        table.setDdlTabFdw(dcTable.e("ddl_tab_fdw").asStr());
        table.setDdlIndPg(dcTable.e("ddl_ind_pg").asStr());
        table.setRowidStat(dcTable.e("rowid_stat").asStr());
        table.setScript(dcTable.e("script").asStr());
        table.setLog(dcTable.e("log").asStr());
        tables.setTable(table);

//        Ежели всё-таки будет массив
//        Напоминалка, как работать с DCont
//        for (DCont dcTable : dc.list("table")) {
//            TableDto table = new TableDto();
//            table.setSchemaName(dcTable.e("shema_name").asStr());
//            table.setTableName(dcTable.e("table_name").asStr());
//            table.setDdlTabPg(dcTable.e("ddl_tab_pg").asStr());
//            table.setDdlConPg(dcTable.e("ddl_con_pg").asStr());
//            table.setDdlTabFdw(dcTable.e("ddl_tab_fdw").asStr());
//            table.setScript(dcTable.e("script").asStr());
//            tables.getTables().add(table);
//        }

        return tables;
    }
}
