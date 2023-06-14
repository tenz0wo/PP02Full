package ru.inversion.migration_assistant.model.models;

import java.util.ArrayList;
import java.util.List;

public class Controllers {
    List <ControllerFolder> Folders;

    public List <ControllerFolder> getControllerList(){
        return Folders;
    }

    public void setControllerList(List <ControllerFolder> controllerList){
        this.Folders = controllerList;
    }
}
