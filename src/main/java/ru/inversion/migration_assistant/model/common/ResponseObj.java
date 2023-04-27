package ru.inversion.migration_assistant.model.common;

import lombok.Data;
import ru.inversion.migration_assistant.model.request.RequestTableColumn;

import java.util.function.Consumer;
import java.util.function.Supplier;

@Data
public class ResponseObj<T> {
    T result;
    ResponseError error = new ResponseError();

    public ResponseObj (T object) {
        this.result = object;
    }

    public ResponseObj () {
        this.result = null;
    }

    public ResponseObj (ResponseObj<T> obj) {
        try {
            this.result = obj.getResult();
            this.error = obj.getError();
        } catch (Exception ex) {
            this.error.setMessage(ex.getMessage());
            this.error.setCode(-1L);
        }
    }
}
