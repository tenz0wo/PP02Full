package ru.inversion.migration_assistant.model.models;

import java.util.ArrayList;

public class ControllerTable {
    String table;
    ArrayList <String> query;

    public String getTable(){
        return table;
    }

    public void setTable(String table){
        this.table = table;
    }

    public ArrayList <String> getQuery(){
        return query;
    }

    public void setQuery(ArrayList <String> query){
        this.query = query;
    }
}
