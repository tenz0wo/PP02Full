package ru.inversion.migration_assistant.model.models;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Controller {
    String path;
    ArrayList<ControllerTable> tables;
    List<LovTable> lovTables;
}


