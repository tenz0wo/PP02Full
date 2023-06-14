package ru.inversion.migration_assistant.service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ParsePath {

    HashMap<String, String> controllerFolders = new HashMap<>();

    String pathToMain = "C:\\Users\\Koryshev.INVERSION\\Desktop\\gvboishnbv'\\controller-db-links-back\\src\\main";
    String jarFilePath = pathToMain+"\\result\\FXKu.jar";

    public ParsePath() throws IOException {
    }

    public JarFile openJarFile(String path) throws IOException {
        return new JarFile(path);
    }
    public boolean checkExistTable(String path, String regex) {
        try (BufferedReader reader = new BufferedReader(new FileReader(pathToMain+"\\result\\FXKu\\"+path))) {
            String line;
            while ((line = reader.readLine()) != null) {
                Pattern pattern = Pattern.compile(regex);
                Matcher matcher = pattern.matcher(line);
                if (matcher.find()) {
                    return true;
                }
            }
            return false;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public ArrayList<String> findControllerPathFiles(String pathFolder) throws IOException {
        ArrayList<String> controllerPaths = new ArrayList<>();

        JarFile jarFile = openJarFile(jarFilePath);
        Enumeration<JarEntry> entries = jarFile.entries();

        while (entries.hasMoreElements()) {
            JarEntry entry = (JarEntry) entries.nextElement();
            String name = entry.getName();
            if (name.endsWith("Controller.java") && (extractPathFolder(name).equals(pathFolder))) {
                    controllerPaths.add(name);
                }
        }
        jarFile.close();
        return controllerPaths;
    }

    public void findControllerPathFolders() throws IOException {
        JarFile jarFile = openJarFile(jarFilePath);
        Enumeration<JarEntry> entries = jarFile.entries();
        controllerFolders = new HashMap<>();

        while (entries.hasMoreElements()) {
            JarEntry entry = (JarEntry) entries.nextElement();
            String pathFile = entry.getName();
            if (pathFile.endsWith("Controller.java")) {
                controllerFolders.put(extractPathFolder(pathFile), extractPathFolder(pathFile));
            }
        }
        jarFile.close();
    }

    private String extractPathFolder(String path){
        return path.substring(0, path.lastIndexOf('/'));
    }

}