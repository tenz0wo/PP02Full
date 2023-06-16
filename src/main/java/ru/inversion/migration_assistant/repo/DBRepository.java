package ru.inversion.migration_assistant.repo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.inversion.migration_assistant.exec.DBExecHandler;
import ru.inversion.migration_assistant.exec.ExecutorParams;
import ru.inversion.migration_assistant.exec.repo.InsertOra2Pg;
import ru.inversion.migration_assistant.model.common.ResponseObj;
import ru.inversion.migration_assistant.model.models.ResponseControllers;

@Service
@Slf4j
public class DBRepository {

//    public ResponseObj<TablesDto> getConvertUi(RequestParams params){
//        return new DBExecHandler<>(ConverterExecutor.class, new ExecutorParams<>(params)).exec();
//    }
//
//    public ResponseObj<ResponseDependencies> getDependencies(RequestDependencies params){
//        return new DBExecHandler<>(DependenciesExecutor.class, new ExecutorParams<>(params)).exec();
//    }

    public ResponseObj<ResponseControllers> insertOra2Pg(ResponseControllers params){
        return new DBExecHandler<>(InsertOra2Pg.class, new ExecutorParams<>(params)).exec();
    }
}
