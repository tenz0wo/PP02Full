package ru.inversion.migration_assistant.rest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.inversion.migration_assistant.model.common.DbConnectionParamsImpl;
import ru.inversion.migration_assistant.model.common.ResponseObj;
import ru.inversion.migration_assistant.model.request.*;
import ru.inversion.migration_assistant.model.response.DbObjectWithSchema;
import ru.inversion.migration_assistant.model.response.DoubleParam;
import ru.inversion.migration_assistant.model.response.ResponseAppendColumnHints;
import ru.inversion.migration_assistant.model.response.TablesDto;
import ru.inversion.migration_assistant.service.ConverterService;

import java.sql.SQLException;
import java.util.List;

@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
@RestController
@Slf4j
public class ConverterRest{
    protected final ConverterService converterService;

    @Autowired
    public ConverterRest(ConverterService converterService) {
        this.converterService = converterService;
    }

    @PostMapping(path = "migration/convert/ui")
    @ResponseBody
    public ResponseEntity<?> getConvertUi(@RequestBody RequestParams[] request) throws Exception {
        ResponseObj<List<TablesDto>> response = converterService.getConvertUi(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping(path = "migration/table-list")
    @ResponseBody
    public ResponseEntity<?> getTableList(@RequestBody RequestParams request, @RequestHeader HttpHeaders headers) throws SQLException{
        List<DoubleParam> response = converterService.getTableList(request).getResult();
        return ResponseEntity.ok(response);
    }

    @PostMapping(path = "migration/table-schema-list")
    @ResponseBody
    public ResponseEntity<?> getTableSchemaList(@RequestBody RequestParams request) throws SQLException{
        List<String> response = converterService.getTableSchemaList(request).getResult();
        return ResponseEntity.ok(response);
    }

    @PostMapping(path = "migration/package-list")
    @ResponseBody
    public ResponseEntity<?> getPackageList(@RequestBody RequestParams request) throws SQLException{
        List<DoubleParam> response = converterService.getPackageList(request).getResult();
        return ResponseEntity.ok(response);
    }


    @PostMapping(path = "migration/package-schema-list")
    @ResponseBody
    public ResponseEntity<?> getPackageSchemaList(@RequestBody RequestParams request) throws SQLException{
        List<String> response = converterService.getPackageSchemaList(request).getResult();
        return ResponseEntity.ok(response);
    }

    @PostMapping(path = "migration/tables-by-package")
    @ResponseBody
    public ResponseEntity<List<DbObjectWithSchema>> getTablesByPackage(@RequestBody RequestParams request) throws SQLException {
        List<DbObjectWithSchema> response = converterService.getTablesByPackage(request).getResult();
        return ResponseEntity.ok(response);
    }

    @PostMapping(path = "migration/execute-sql-script")
    @ResponseBody
    public ResponseEntity<?> executeSqlScript(@RequestBody RequestExecutableScripts request) throws Exception {
        return ResponseEntity.ok(converterService.executeSqlScript(request));
    }

    @PostMapping(path = "migration/check-table")
    @ResponseBody
    public ResponseEntity<?> checkTable(@RequestBody RequestCheckTable request) throws SQLException {
        return ResponseEntity.ok(converterService.checkTable(request));
    }

    @PostMapping(path = "migration/check-connection")
    @ResponseBody
    public ResponseEntity<?> checkTable(@RequestBody DbConnectionParamsImpl request) throws SQLException {
        return ResponseEntity.ok(converterService.checkConnection(request));
    }

    @PostMapping(path = "migration/append-column-hints")
    @ResponseBody
    public ResponseEntity<?> appendColumnHints(@RequestBody RequestEditColumnHints request) throws Exception {
        ResponseObj<ResponseAppendColumnHints> response = converterService.appendColumnHints(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping(path = "migration/get-column-hints")
    @ResponseBody
    public ResponseEntity<?> getColumnHints(@RequestBody DbConnectionParamsImpl request) throws Exception {
        ResponseObj<?> response = converterService.getColumnHints(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping(path = "migration/get-table-columns")
    @ResponseBody
    public ResponseEntity<?> getTableColumns(@RequestBody RequestTableColumn request) throws Exception {
        ResponseObj<?> response = converterService.getTableColumns(request);
        return ResponseEntity.ok(response);
    }

}