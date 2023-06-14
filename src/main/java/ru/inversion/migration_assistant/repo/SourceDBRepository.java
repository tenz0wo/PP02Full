package ru.inversion.migration_assistant.repo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.inversion.migration_assistant.model.common.DbConnectionParams;
import ru.inversion.migration_assistant.model.common.ResponseObj;
import ru.inversion.migration_assistant.model.request.*;
import ru.inversion.migration_assistant.model.response.*;

import java.sql.*;
import java.util.List;

@Service
@Slf4j
public class SourceDBRepository {

//    public ResponseObj<TablesDto> getConvertUi(RequestParams params){
//        return new DBExecHandler<>(ConverterExecutor.class, new ExecutorParams<>(params)).exec();
//    }
//
//    public ResponseObj<ResponseDependencies> getDependencies(RequestDependencies params){
//        return new DBExecHandler<>(DependenciesExecutor.class, new ExecutorParams<>(params)).exec();
//    }
}
