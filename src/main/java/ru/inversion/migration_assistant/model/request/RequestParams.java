package ru.inversion.migration_assistant.model.request;

import lombok.Data;
import ru.inversion.migration_assistant.model.common.DbConnectionParamsImpl;

@Data
public class RequestParams extends DbConnectionParamsImpl {
    String i_prefix;
    String i_schema_name;
    String i_table_tablespace;
    String i_index_tablespace;
    String i_dbms;
    String i_seq_option;
}
