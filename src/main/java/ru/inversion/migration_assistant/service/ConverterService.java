package ru.inversion.migration_assistant.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.inversion.migration_assistant.exec.ExecutorConsumer;
import ru.inversion.migration_assistant.model.common.DbConnectionParams;
import ru.inversion.migration_assistant.model.common.ResponseObj;
import ru.inversion.migration_assistant.model.request.*;
import ru.inversion.migration_assistant.model.response.*;
import ru.inversion.migration_assistant.util.ResultCode;
import ru.inversion.migration_assistant.repo.SourceDBRepository;
import ru.inversion.migration_assistant.repo.TargetDBRepository;

import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;


@Service
public class ConverterService {

    protected final SourceDBRepository sourceDBRepository;
    protected final TargetDBRepository targetDBRepository;

    @Autowired
    public ConverterService(SourceDBRepository sourceDBRepository, TargetDBRepository targetDBRepository) {
        this.sourceDBRepository = sourceDBRepository;
        this.targetDBRepository = targetDBRepository;
    }

    public static <T, R, E extends Enum<E>> ResponseObj<List<R>> execute(List<T> list,
                                                                         Function<T, ResponseObj<R>> function,
                                                                         Supplier<R> onFailResultSupplier,
                                                                         ExecutorConsumer<ResponseObj<List<R>>> onFailResultConsumer) throws Exception {
        List<R> resultList = new LinkedList<>();
        ResponseObj<List<R>> response = new ResponseObj<>(resultList);

        for (T item: list) {
            ResponseObj<R> itemResult = function.apply(item);

            if (!itemResult.getError().getCode().equals(ResultCode.OK.value())) {
                R failedItem = onFailResultSupplier.get();
                resultList.add(failedItem);
                response.getError().setCode(itemResult.getError().getCode());
                response.getError().setMessage(itemResult.getError().getMessage());
                onFailResultConsumer.accept(response);
                return response;
            }

            resultList.add(itemResult.getResult());
        }

        return response;
    }

    public ResponseObj<List<TablesDto>> getConvertUi(RequestParams[] paramRows) throws Exception {
        return execute(
                List.of(paramRows),
                sourceDBRepository::getConvertUi,
                TablesDto::new,
                listResponseObj -> {}
        );
    }

    public ResponseObj<List<ResponseExecutableScript>> executeSqlScript(RequestExecutableScripts params) throws Exception {

        return execute(
                params.getScripts(),
                targetDBRepository::executeSqlScript,
                ResponseExecutableScript::new,
                listResponseObj -> {
                    if (!listResponseObj.getError().getCode().equals(ResultCode.OK.value())) {
                        listResponseObj.getResult().get(listResponseObj.getResult().size()-1).setResponse("Error");
                        String name = params.getScripts().get(listResponseObj.getResult().size()-1).getExecutableScript().getScriptName();
                        listResponseObj.getResult().get(listResponseObj.getResult().size()-1).setScriptName(name);
                    }
                }
        );
    }

    public ResponseObj<List<DoubleParam>> getTableList(RequestParams params) {
        return sourceDBRepository.getTableList(params);
    }

    public ResponseObj<List<String>> getTableSchemaList(RequestParams params) {
        return sourceDBRepository.getTableSchemaList(params);
    }

    public ResponseObj<List<String>> getPackageList(RequestParams params) {
        return sourceDBRepository.getPackageList(params);
    }

    public ResponseObj<List<String>> getPackageSchemaList(RequestParams params) {
        return sourceDBRepository.getPackageSchemaList(params);
    }

    public ResponseObj<List<DbObjectWithSchema>> getTablesByPackage(RequestParams params) throws SQLException {
        return sourceDBRepository.getTablesByPackage(params);
    }

    public ResponseObj<ResponseCheckTable> checkTable(RequestCheckTable params) {
        return targetDBRepository.checkTable(params);
    }

    public ResponseObj<String> checkConnection(DbConnectionParams params) {
        return targetDBRepository.checkConnection(params);
    }
}