package ru.inversion.migration_assistant.service;

import io.swagger.v3.oas.models.links.Link;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.inversion.migration_assistant.model.*;
import ru.inversion.priv.tools.dcont.DCont;
import ru.inversion.priv.tools.mdom.MDom;
import ru.inversion.migration_assistant.repo.ConverterRepository;
import ru.inversion.migration_assistant.repo.PostgresRepository;

import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;


@Service
public class ConverterService {

    protected ConverterRepository converterRepository;
    protected PostgresRepository postgresRepository;

    @Autowired
    public ConverterService(ConverterRepository converterRepository, PostgresRepository postgresRepository) {
        this.converterRepository = converterRepository;
        this.postgresRepository = postgresRepository;
    }

    public ResponseObj<Integer> getConvert(RequestParams params) throws SQLException{
        return new ResponseObj<>(converterRepository.getConvert(params));
    }

    public ResponseObj<List<TablesDto>> getConvertUi(RequestParams[] paramRows) throws SQLException {
        List<TablesDto> tableRows = new LinkedList<>();
        List.of(paramRows).forEach(requestParams -> {
            addTableRow (requestParams, tableRows);
        });
        return new ResponseObj<>(tableRows);
    }

    private void addTableRow (RequestParams params, List<TablesDto> tableRows) {
        TablesDto tableRow = new TablesDto();
        try {
            String ret = converterRepository.getConvertUi(params);
            System.out.println("ret");
            System.out.println(ret);
            DCont dc = new MDom();
            dc.loadXml(ret);
            tableRow = mapTables(dc);
        } catch (SQLException ex) {
            tableRow.getTable().setTableName(params.getI_prefix());
            tableRow.getTable().setSchemaName(params.getI_schema_name());
            tableRow.getTable().setDdlTabPg(ex.getMessage());
            tableRow.getTable().setDdlTabFdw(ex.getMessage());
            tableRow.getTable().setScript(ex.getMessage());
            tableRow.getTable().setDdlConPg(ex.getMessage());
        } finally {
            tableRows.add(tableRow);
        }
    }

    TablesDto mapTables(DCont dc) {
        TablesDto tables = new TablesDto();
        tables.setVersion(dc.e("version").asStr());
        tables.setPrefix(dc.e("prefix").asStr());

        DCont dcTable = dc.e("table");
        TableDto table = new TableDto();
        table.setSchemaName(dcTable.e("shema_name").asStr());
        table.setTableName(dcTable.e("table_name").asStr());
        table.setDdlTabPg(dcTable.e("ddl_tab_pg").asStr());
        table.setDdlConPg(dcTable.e("ddl_con_pg").asStr());
        table.setDdlTabFdw(dcTable.e("ddl_tab_fdw").asStr());
        table.setScript(dcTable.e("script").asStr());
        tables.setTable(table);

//        Ежели всё-таки будет массив
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


    public ResponseObj<List<String>> getTableList(RequestParams params) throws SQLException{
        return converterRepository.getTableList(params);
    }

    public ResponseObj<List<String>> getTableSchemaList(RequestParams params) throws SQLException{
        return converterRepository.getTableSchemaList(params);
    }

    public ResponseObj<List<String>> getPackageList(RequestParams params) throws SQLException{
        return converterRepository.getPackageList(params);
    }

    public ResponseObj<List<String>> getPackageSchemaList(RequestParams params) throws SQLException{
        return converterRepository.getPackageSchemaList(params);
    }

    public ResponseObj<List<DbObjectWithSchema>> getTablesByPackage(RequestParams params) throws SQLException {
        return converterRepository.getTablesByPackage(params);
    }

    public ResponseObj<List<ResponsePSQL>> executeSqlScript(RequestPGScripts params) throws SQLException {
        return postgresRepository.executeSqlScript(params);
    }
}