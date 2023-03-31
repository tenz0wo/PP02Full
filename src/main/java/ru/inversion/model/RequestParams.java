package ru.inversion.model;

import lombok.Data;

@Data
public class RequestParams {
    String i_prefix;
    String i_schema_name;
    String i_table_tablespace;
    String i_index_tablespace;
    String i_dbms;
    String i_seq_option;
}
