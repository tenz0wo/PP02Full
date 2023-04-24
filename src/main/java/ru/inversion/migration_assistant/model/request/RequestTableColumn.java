package ru.inversion.migration_assistant.model.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import ru.inversion.migration_assistant.model.common.DbConnectionParamsImpl;

@Data
public class RequestTableColumn extends DbConnectionParamsImpl {
    @JsonProperty("table_schema")
    String tableSchema;
    @JsonProperty("table_name")
    String tableName;
}
