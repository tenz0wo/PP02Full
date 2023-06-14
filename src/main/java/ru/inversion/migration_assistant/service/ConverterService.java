package ru.inversion.migration_assistant.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.inversion.migration_assistant.model.models.ControllerFolder;
import ru.inversion.migration_assistant.model.models.Controllers;
import ru.inversion.migration_assistant.repo.SourceDBRepository;
import ru.inversion.migration_assistant.repo.TargetDBRepository;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;


@Service
public class ConverterService {

    protected final SourceDBRepository sourceDBRepository;
    protected final TargetDBRepository targetDBRepository;

    @Autowired
    public ConverterService(SourceDBRepository sourceDBRepository, TargetDBRepository targetDBRepository) {
        this.sourceDBRepository = sourceDBRepository;
        this.targetDBRepository = targetDBRepository;
    }


//    public ResponseObj<ResponseExplainPlan> explainPlan(RequestExplainPlan params) {
//        return targetDBRepository.explainPlan(params);
//    }
//
//    public ResponseObj<ResponseDependencies> getDependencies(RequestDependencies params) {
//        return sourceDBRepository.getDependencies(params);
//    }

    public Controllers getControllers(String params) throws IOException, SQLException {
        ControllerDBLinks controllerDBLinks = new ControllerDBLinks();
        return controllerDBLinks.controllers;
    }
}