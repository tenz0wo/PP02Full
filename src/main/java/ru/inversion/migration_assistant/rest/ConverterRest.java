package ru.inversion.migration_assistant.rest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.inversion.migration_assistant.model.DbObjectWithSchema;
import ru.inversion.migration_assistant.model.TablesDto;
import ru.inversion.migration_assistant.service.ConverterService;
import ru.inversion.migration_assistant.model.ResponseObj;
import ru.inversion.migration_assistant.model.RequestParams;

import java.sql.SQLException;
import java.util.List;

@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
@RestController
@Slf4j
public class ConverterRest{
    protected ConverterService converterService;

    @Autowired
    public ConverterRest(ConverterService converterService) {
        this.converterService = converterService;
    }

    @PostMapping(path = "migration/convert")
    @ResponseBody
    public ResponseEntity<?> getConvert(@RequestBody RequestParams request) throws SQLException{
        ResponseObj<Integer> response = converterService.getConvert(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping(path = "migration/convert/ui")
    @ResponseBody
    public ResponseEntity<?> getConvertUi(@RequestBody RequestParams request) throws SQLException{
        ResponseObj<TablesDto> response = converterService.getConvertUi(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping(path = "migration/table-list")
    @ResponseBody
    public ResponseEntity<?> getTableList(@RequestBody RequestParams request) throws SQLException{
        List<String> response = converterService.getTableList(request).getResult();
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
        List<String> response = converterService.getPackageList(request).getResult();
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

}