package ru.inversion.migration_assistant.model.models;

public class LovTable {
    String pathLov;
    String table;
    String query;

    public String getPathLov() {
        return pathLov;
    }

    public void setPathLov(String pathLov) {
        this.pathLov = pathLov;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public boolean chekAllNull(){
        if (table.isBlank() && pathLov.isBlank() && query.isBlank()){
            return false;
        }
        return true;
    }
}
