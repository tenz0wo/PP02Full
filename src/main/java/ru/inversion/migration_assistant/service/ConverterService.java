package ru.inversion.migration_assistant.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.inversion.migration_assistant.model.common.ResponseObj;
import ru.inversion.migration_assistant.model.models.ResponseControllers;
import ru.inversion.migration_assistant.repo.DBRepository;

import java.io.IOException;
import java.sql.SQLException;


@Service
public class ConverterService {

    protected final DBRepository DBRepository;

    @Autowired
    public ConverterService(DBRepository DBRepository) {
        this.DBRepository = DBRepository;
    }


//    public ResponseObj<ResponseExplainPlan> explainPlan(RequestExplainPlan params) {
//        return targetDBRepository.explainPlan(params);
//    }
//
//    public ResponseObj<ResponseDependencies> getDependencies(RequestDependencies params) {
//        return sourceDBRepository.getDependencies(params);
//    }

    public ResponseControllers getControllers(String params) throws IOException, SQLException {
        ControllerDBLinks controllerDBLinks = new ControllerDBLinks();
        return InsertOra2Pg(controllerDBLinks.responseControllers);
    }

    public ResponseControllers InsertOra2Pg(ResponseControllers controllers){
        return DBRepository.insertOra2Pg(controllers).getResult();
    }
}