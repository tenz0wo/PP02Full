package ru.inversion.rest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.inversion.model.ResponseObj;
import ru.inversion.model.RequestParams;
import ru.inversion.service.ConverterService;

import java.io.IOException;
import java.sql.SQLException;

@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
@RestController
@Slf4j
public class ConverterRest{
    protected ConverterService converterService;

    @Autowired
    public ConverterRest(ConverterService converterService) {
        this.converterService = converterService;
    }

    @PostMapping(path = "oracle/convert")
    @ResponseBody
    public ResponseEntity<?> getConvert(@RequestBody RequestParams request) throws SQLException{
        ResponseObj response = converterService.getConvert(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping(path = "oracle/convert/ui")
    @ResponseBody
    public ResponseEntity<?> getConvertUi(@RequestBody RequestParams request) throws SQLException{
        ResponseObj response = converterService.getConvertUi(request);
        return ResponseEntity.ok(response);
    }
}