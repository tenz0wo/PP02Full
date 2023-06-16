package ru.inversion.migration_assistant.model.models;

import lombok.Data;

import java.util.ArrayList;

@Data
public class ControllerTable {
    String table;
    ArrayList <String> query;
}
