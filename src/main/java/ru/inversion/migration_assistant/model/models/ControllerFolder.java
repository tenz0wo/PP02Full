package ru.inversion.migration_assistant.model.models;

import lombok.Data;

import java.util.List;

@Data
public class ControllerFolder {
    String folderPath;
    List<Controller> controllers;
}
