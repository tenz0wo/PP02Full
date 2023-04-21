package ru.inversion.migration_assistant.exec.repo;

import ru.inversion.migration_assistant.exec.ExecutorParams;
import ru.inversion.migration_assistant.model.request.RequestEditColumnHints;
import ru.inversion.migration_assistant.model.response.ResponseAppendColumnHints;

import java.sql.Types;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class AppendColumnHintsExecutor extends RequestEditColumnHintsExecutor<ResponseAppendColumnHints> {
    ResponseAppendColumnHints response = new ResponseAppendColumnHints();
    Map<Integer, String> descriptions = new HashMap<>();

    public AppendColumnHintsExecutor(ExecutorParams<RequestEditColumnHints> executorParams) {
        super(executorParams);
        populateDescriptions();
    }

    @Override
    public ResponseAppendColumnHints exec() throws Exception {
        response.setCode(addColumnHint());
        describeResultCode();
        return response;
    }

    Integer addColumnHint() throws Exception {
        AtomicReference<Integer> convertResult = new AtomicReference<>();
        String callable = "{? = call ora2pg_pkg.add_column_hint(?, ?, ?, ?)}";
        executeCallable(
                callable,
                callableStatement -> {
                    callableStatement.registerOutParameter(1, Types.INTEGER);
                    callableStatement.setString(2, params.getI_table_owner());
                    callableStatement.setString(3, params.getI_table_name());
                    callableStatement.setString(4, params.getI_column_name());
                    callableStatement.setString(5, params.getI_column_data_type());
                },
                callableStatement -> convertResult.set(callableStatement.getInt(1)));
        return convertResult.get();
    }

    void describeResultCode(){
        try {
            response.setDescription(descriptions.get(response.getCode()));
            if (response.getDescription().isBlank()){
                throw new RuntimeException("Error: Unknown function's return code: " + response.getCode());
            }
        } catch (NullPointerException ex){
            throw new RuntimeException("Error: Unknown function's return code: " + response.getCode());
        }
        if (response.getCode() < 0) {
            throw new RuntimeException("Error: " + response.getDescription());
        }
    }

    void populateDescriptions(){
        descriptions.put(-1, "Переданы не все параметры");
        descriptions.put(0, "Добавлен новый хинт");
        descriptions.put(1, "Хинт обновлен");
    }
}
