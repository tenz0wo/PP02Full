package ru.inversion.migration_assistant.model.common;

import lombok.Data;

@Data
public class DbConnectionParamsImpl implements DbConnectionParams {
     String url;
     String user;
     String password;
}
