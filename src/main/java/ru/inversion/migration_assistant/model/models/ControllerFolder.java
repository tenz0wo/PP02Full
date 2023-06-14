package ru.inversion.migration_assistant.model.models;

import java.util.List;

public class ControllerFolder {
    String folderPath;
    List<Controller> controllers;

    public String getFolderPath(){
        return folderPath;
    }

    public void setFolderPath(String folderPath) {
        this.folderPath = folderPath;
    }

    public List<Controller> getControllers() {
        return controllers;
    }

    public void setControllers(List<Controller> controllers) {
        this.controllers = controllers;
    }
}
