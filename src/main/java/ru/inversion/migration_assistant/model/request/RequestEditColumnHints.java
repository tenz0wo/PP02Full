package ru.inversion.migration_assistant.model.request;

import lombok.Data;
import ru.inversion.migration_assistant.model.common.DbConnectionParamsImpl;

@Data
public class RequestEditColumnHints extends DbConnectionParamsImpl{
    String i_table_owner;
    String i_table_name;
    String i_column_name;
    String i_column_data_type;

}
