package ru.inversion.migration_assistant.repo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.inversion.migration_assistant.exec.DBExecHandler;
import ru.inversion.migration_assistant.exec.ExecutorParams;
import ru.inversion.migration_assistant.exec.repo.*;
import ru.inversion.migration_assistant.model.common.ResponseObj;
import ru.inversion.migration_assistant.model.request.RequestParams;
import ru.inversion.migration_assistant.model.response.DbObjectWithSchema;
import ru.inversion.migration_assistant.model.response.DoubleParam;
import ru.inversion.migration_assistant.model.response.TablesDto;
import ru.inversion.migration_assistant.model.sql.ObjectPart;
import ru.inversion.migration_assistant.model.sql.ObjectType;

import java.sql.*;
import java.util.List;

@Service
@Slf4j
public class SourceDBRepository {

    public ResponseObj<TablesDto> getConvertUi(RequestParams params){
        return new DBExecHandler<>(ConverterExecutor.class, new ExecutorParams<>(params)).exec();
    }

    public ResponseObj<List<DoubleParam>> getTableList(RequestParams params) {
        ColumnListParams<RequestParams> columnListParams = new ColumnListParams<>(params, ObjectType.TABLE, ObjectPart.OBJECT);
        return new DBExecHandler<>(DoubleColumnListExecutor.class, columnListParams).exec();
    }

    public ResponseObj<List<String>> getTableSchemaList(RequestParams params) {
        ColumnListParams<RequestParams> columnListParams = new ColumnListParams<>(params, ObjectType.TABLE, ObjectPart.SCHEMA);
        return new DBExecHandler<>(SingleColumnListExecutor.class, columnListParams).exec();
    }

    public ResponseObj<List<DoubleParam>> getPackageList(RequestParams params) {
        ColumnListParams<RequestParams> columnListParams = new ColumnListParams<>(params, ObjectType.PACKAGE, ObjectPart.OBJECT);
        return new DBExecHandler<>(DoubleColumnListExecutor.class, columnListParams).exec();
    }

    public ResponseObj<List<String>> getPackageSchemaList(RequestParams params) {
        ColumnListParams<RequestParams> columnListParams = new ColumnListParams<>(params, ObjectType.PACKAGE, ObjectPart.SCHEMA);
        return new DBExecHandler<>(SingleColumnListExecutor.class, columnListParams).exec();
    }

    public ResponseObj<List<DbObjectWithSchema>> getTablesByPackage(RequestParams params) throws SQLException {
        return new DBExecHandler<>(TablesByPackageExecutor.class, new ExecutorParams<>(params)).exec();
    }

}
