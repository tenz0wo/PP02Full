package ru.inversion.migration_assistant.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.inversion.migration_assistant.model.common.ResponseObj;
import ru.inversion.migration_assistant.model.request.*;
import ru.inversion.migration_assistant.model.response.*;
import ru.inversion.migration_assistant.util.ResultCode;
import ru.inversion.migration_assistant.repo.SourceDBRepository;
import ru.inversion.migration_assistant.repo.TargetDBRepository;

import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;


@Service
public class ConverterService {

    protected SourceDBRepository sourceDBRepository;
    protected TargetDBRepository targetDBRepository;

    @Autowired
    public ConverterService(SourceDBRepository sourceDBRepository, TargetDBRepository targetDBRepository) {
        this.sourceDBRepository = sourceDBRepository;
        this.targetDBRepository = targetDBRepository;
    }

    public ResponseObj<List<TablesDto>> getConvertUi(RequestParams[] paramRows) throws SQLException {
        List<TablesDto> tableRows = new LinkedList<>();
        List.of(paramRows).forEach(singleTableParams -> {
            ResponseObj<TablesDto> tableRowResponse = sourceDBRepository.getConvertUi(singleTableParams);
            if (!tableRowResponse.getError().getCode().equals(ResultCode.OK.value())) {
                ResponseObj<List<TablesDto>> response = new ResponseObj<>(tableRows);
                TablesDto problemTableRow = new TablesDto();
                TableDto problemTableRowWrapped = new TableDto();
                problemTableRowWrapped.setTableName(singleTableParams.getI_prefix());
                problemTableRowWrapped.setSchemaName(singleTableParams.getI_schema_name());
                problemTableRow.setTable(problemTableRowWrapped);
                response.getResult().add(problemTableRow);
                response.getError().setCode(tableRowResponse.getError().getCode());
                response.getError().setMessage(tableRowResponse.getError().getMessage());
            }
            tableRows.add(tableRowResponse.getResult());
        });
        return new ResponseObj<>(tableRows);
    }


    public ResponseObj<List<String>> getTableList(RequestParams params) throws SQLException{
        return sourceDBRepository.getTableList(params);
    }

    public ResponseObj<List<String>> getTableSchemaList(RequestParams params) throws SQLException{
        return sourceDBRepository.getTableSchemaList(params);
    }

    public ResponseObj<List<String>> getPackageList(RequestParams params) throws SQLException{
        return sourceDBRepository.getPackageList(params);
    }

    public ResponseObj<List<String>> getPackageSchemaList(RequestParams params) throws SQLException{
        return sourceDBRepository.getPackageSchemaList(params);
    }

    public ResponseObj<List<DbObjectWithSchema>> getTablesByPackage(RequestParams params) throws SQLException {
        return sourceDBRepository.getTablesByPackage(params);
    }

    public ResponseObj<List<ResponseExecutableScripts>> executeSqlScript(RequestExecutableScripts params) throws SQLException {
        return targetDBRepository.executeSqlScript(params);
    }

    public ResponseObj<ResponseCheckTable> checkTable(RequestCheckTable params) throws SQLException {
        return targetDBRepository.checkTable(params);
    }
}