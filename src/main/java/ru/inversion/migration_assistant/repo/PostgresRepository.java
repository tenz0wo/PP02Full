package ru.inversion.migration_assistant.repo;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import ru.inversion.migration_assistant.db.Postgres;
import ru.inversion.migration_assistant.model.*;

import java.sql.*;
import java.util.LinkedList;
import java.util.List;

@Service
@Slf4j
public class PostgresRepository {
    public ResponseObj<List<ResponsePSQL>> executeSqlScript(RequestPGScripts params) throws SQLException {
        Postgres pg = new Postgres(params.getUrl(), params.getUser(), params.getPassword());
        return new ResponseObj<>(pg.executeSqlScript(params));
    }

    public ResponseObj<ResponseCheckTable> checkTable(RequestCheckTable params) throws SQLException {
        Postgres pg = new Postgres(params.getUrl(), params.getUser(), params.getPassword());
        return new ResponseObj<>(pg.checkTable(params.getSchema(), params.getTable()));
    }
}
