package ru.inversion.migration_assistant.model.common;

import lombok.Data;

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
}
