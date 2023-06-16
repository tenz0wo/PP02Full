package ru.inversion.migration_assistant.exec;

import ru.inversion.migration_assistant.model.common.ResponseObj;
import ru.inversion.migration_assistant.util.ResultCode;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class DBExecHandler<T> {

    DBExecutor<T> executor;
    final Class<? extends DBExecutor<T>> execClass;
    final ExecutorParams<?> params;


    public DBExecHandler(Class<? extends DBExecutor<T>> execClass, ExecutorParams<?> params) {
        this.execClass = execClass;
        this.params = params;
    }

    public ResponseObj<T> exec() {
        ResponseObj<T> response = new ResponseObj<>();
        try {
            generateExecutor ();
            T t = executor.exec();
            response.setResult(t);
            executor.closeConnection();
        } catch (Exception ex) {
            response.getError().setCode(ResultCode.COMMON_ERROR.value());
            StringWriter sw = new StringWriter();
            ex.printStackTrace(new PrintWriter(sw));
            response.getError().setMessage(sw.toString());
        }
        return response;
    }

    private void generateExecutor () throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        Constructor<?> constructor = execClass.getConstructor(params.getClass());
        this.executor = (DBExecutor<T>) constructor.newInstance(params);
    }
}
