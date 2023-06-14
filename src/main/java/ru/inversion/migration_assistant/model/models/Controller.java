package ru.inversion.migration_assistant.model.models;

import java.util.ArrayList;
import java.util.List;

public class Controller {
    String path;
    ArrayList<ControllerTable> tables;
    List<LovTable> lovTables;

    public String getPath(){
        return path;
    }

    public void setPath(String path){
        this.path = path;
    }

    public ArrayList<ControllerTable> getTables(){
        return tables;
    }

    public void setTables(ArrayList<ControllerTable> tables){
        this.tables = tables;
    }

    public List<LovTable> getLovTables() {
        return lovTables;
    }

    public void setLovTables(List<LovTable> lovTables) {
        this.lovTables = lovTables;
    }
}


