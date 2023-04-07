package ru.inversion.migration_assistant.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.inversion.migration_assistant.model.RequestParams;
import ru.inversion.migration_assistant.model.ResponseObj;
import ru.inversion.migration_assistant.model.TableDto;
import ru.inversion.migration_assistant.model.TablesDto;
import ru.inversion.priv.tools.dcont.DCont;
import ru.inversion.priv.tools.mdom.MDom;
import ru.inversion.migration_assistant.repo.ConverterRepository;

import java.sql.SQLException;
import java.util.List;


@Service
public class ConverterService {

    protected ConverterRepository converterRepository;

    @Autowired
    public ConverterService(ConverterRepository converterRepository) {
        this.converterRepository = converterRepository;
    }

    public ResponseObj<Integer> getConvert(RequestParams params) throws SQLException{
        return converterRepository.getConvert(params);
    }

    public ResponseObj<TablesDto> getConvertUi(RequestParams params) throws SQLException{
//        try {
            String ret = converterRepository.getConvertUi(params).getResult();
//        } catch (Exception e){
//            throw new ResourceNotFoundException("Product with id " + id + " not found"));
//        }

        System.out.println("xml");
        System.out.println(ret);

        DCont dc = new MDom();
        dc.loadXml(ret);
        TablesDto tables =  mapTables(dc);

        return new ResponseObj<>(tables);
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



}