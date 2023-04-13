package ru.inversion.migration_assistant.model.common;

import lombok.Data;
import ru.inversion.migration_assistant.util.ResultCode;

@Data
public class ResponseError {
    Long code = ResultCode.OK.value();
    String message;
}
