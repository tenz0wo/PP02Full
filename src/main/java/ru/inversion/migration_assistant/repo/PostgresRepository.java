package ru.inversion.migration_assistant.repo;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import ru.inversion.migration_assistant.model.*;

import java.sql.*;
import java.util.LinkedList;
import java.util.List;

@Service
@Slf4j
public class PostgresRepository {
    public ResponseObj<List<ResponsePSQL>> executeSqlScript(RequestPGScripts params) throws SQLException {
        System.out.println("host = " + params.getHost());
        System.out.println("getPgScripts = " + params.getPgScripts());
        List<ResponsePSQL> resp = new LinkedList<>();


        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        PGScript pgScriptLast = null;

        try {
            String url = "jdbc:postgresql://" + params.getHost() + ":" + params.getPort() + "/" + params.getDbName();
            String user = params.getUser();
            String pass = params.getPassword();
            conn = DriverManager.getConnection(url, user, pass);

            for (PGScript pgScript: params.getPgScripts()){
                pgScriptLast = pgScript;
                ResponsePSQL responsePSQL = new ResponsePSQL();
                responsePSQL.setScriptName(pgScript.getScriptName());
                
                stmt = conn.createStatement();
                boolean response = stmt.execute(pgScript.getScript());
                stmt.execute("commit;");

                if (response == true) {
                    responsePSQL.setResponse("Success!");
                } else {
                    responsePSQL.setResponse("Fail");
                }
                resp.add(responsePSQL);
            }
            
        } catch (SQLException e) {
            stmt.execute("ROLLBACK;");
            ResponsePSQL responsePSQL = new ResponsePSQL();
            responsePSQL.setScriptName(pgScriptLast.getScriptName());

            responsePSQL.setResponse(e.toString());
            resp.add(responsePSQL);
        } finally {
            // Закрытие ресурсов
            try {
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }

        return new ResponseObj<>(resp);

    }
}
