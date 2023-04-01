package ru.inversion.migration_assistant.model;

import lombok.Data;

@Data
public class RequestParams {
    String url;
    String user;
    String password;

    String i_prefix;
    String i_schema_name;
    String i_table_tablespace;
    String i_index_tablespace;
    String i_dbms;
    String i_seq_option;
}
