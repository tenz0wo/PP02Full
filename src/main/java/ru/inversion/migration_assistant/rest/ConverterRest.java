package ru.inversion.migration_assistant.rest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.inversion.migration_assistant.model.common.ResponseObj;
import ru.inversion.migration_assistant.model.models.Controllers;
import ru.inversion.migration_assistant.service.ConverterService;


@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
@RestController
@Slf4j
public class ConverterRest{
    protected final ConverterService converterService;

    @Autowired
    public ConverterRest(ConverterService converterService) {
        this.converterService = converterService;
    }



//    @PostMapping(path = "migration/explain-plan")
//    @ResponseBody
//    public ResponseEntity<?> explainPlan(@RequestBody RequestExplainPlan request) throws Exception {
//        ResponseObj<?> response = converterService.explainPlan(request);
//        return ResponseEntity.ok(response);
//    }
//
//    @PostMapping(path = "migration/get-dependencies")
//    @ResponseBody
//    public ResponseEntity<?> get_dependencies(@RequestBody RequestDependencies request) throws Exception {
//        ResponseObj<?> response = converterService.getDependencies(request);
//        return ResponseEntity.ok(response);
//    }

    @PostMapping(path = "controller-db/get-controller")
    @ResponseBody
    public ResponseEntity<?> getControllers(@RequestBody String request) throws Exception {
        Controllers response = converterService.getControllers(request);
        return ResponseEntity.ok(response);
    }

}