package ru.inversion.migration_assistant.db;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import ru.inversion.migration_assistant.model.PGScript;
import ru.inversion.migration_assistant.model.RequestPGScripts;
import ru.inversion.migration_assistant.model.ResponseCheckTable;
import ru.inversion.migration_assistant.model.ResponsePSQL;

import java.sql.*;
import java.util.LinkedList;
import java.util.List;

public class Postgres {
    Connection conn;

    public Postgres(String url, String user, String password) throws SQLException {
        this.conn = DriverManager.getConnection(url, user, password);
    }

    public List<ResponsePSQL> executeSqlScript(RequestPGScripts params) throws SQLException {
        Statement stmt = conn.createStatement();
        PGScript pgScriptLast = null;
        List<ResponsePSQL> resp = new LinkedList<>();

        try {
            for (PGScript pgScript: params.getPgScripts()){
                pgScriptLast = pgScript;
                ResponsePSQL responsePSQL = new ResponsePSQL();
                responsePSQL.setScriptName(pgScript.getScriptName());

                boolean response = stmt.execute(pgScript.getScript());

                if (response) {
                    responsePSQL.setResponse("Success!");
                } else {
                    responsePSQL.setResponse("Fail");
                }
                resp.add(responsePSQL);
            }
            stmt.execute("commit;");

        } catch (SQLException e) {
            stmt.execute("ROLLBACK;");
            ResponsePSQL responsePSQL = new ResponsePSQL();
            responsePSQL.setScriptName(pgScriptLast.getScriptName());

            responsePSQL.setResponse(e.toString());
            resp.add(responsePSQL);
        }
        return resp;
    }

    public ResponseCheckTable checkTable (String schema, String table) throws SQLException {
        Statement stmt = conn.createStatement();
        ResponseCheckTable responseCheckTable = new ResponseCheckTable();
        String query = "SELECT EXISTS (SELECT 1 FROM pg_tables WHERE schemaname = '" + schema + "' AND tablename = '" + table + "') AS table_exists\n" +
                "UNION ALL\n" +
                "SELECT EXISTS (SELECT 1 FROM " + schema + "." + table + " LIMIT 1) AS table_not_empty;";
        ResultSet response = stmt.executeQuery(query);

        response.next();
        responseCheckTable.setTable(response.getBoolean(1));
        response.next();
        responseCheckTable.setTableRow(response.getBoolean(1));

        return responseCheckTable;
    }
}
