package ru.inversion.migration_assistant.service;

import ru.inversion.migration_assistant.model.models.LovTable;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ParseLovs {
    String pathToMain = "C:\\Users\\Koryshev.INVERSION\\Desktop\\gvboishnbv'\\controller-db-links-back\\src\\main";
    List<LovTable> lovTables = new ArrayList<>();
    String tableLovs;
    List<String> listLovs = new ArrayList<>();
    List<String> hardCheck = new ArrayList<>();
    ParseQuery parseQuery = new ParseQuery();

    public void findLovs(String pathController) {
        lovTables = new ArrayList<>();
        listLovs = new ArrayList<>();

        String lovTargetPattern = "JInvEntityLov<";
        String impTargetPattern = "import ru.";
        String lovImportRegex = "import ru.+|(\\(\\(|)JInvEntityLov<.+>";
        try (BufferedReader br = new BufferedReader(new FileReader(pathToMain+"\\result\\FXKu\\" + pathController))) {
            findForeignLovs(br, lovTargetPattern, impTargetPattern,lovImportRegex, pathController);
            LovTable lovTable = findTableLovs();
            if (lovTable != null) {
                lovTables.add(findTableLovs());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public LovTable findTableLovs(){
        tableLovs = "";
        LovTable lovTable = new LovTable();
        for (String link : listLovs) {
            String tablePattern = "@Table";
            String tableRegex = "@Table\\(name=\".+\"\\)";
            try (BufferedReader br = new BufferedReader(new FileReader(pathToMain+"\\result\\FXKu\\" + link))) {
                String pathFile = pathToMain+"\\result\\FXKu\\" + link;
                String query = parseQuery.executeQuery(pathFile);
                lovTable.setPathLov(link);
                lovTable.setTable(searchTable(br, tablePattern, tableRegex, pathFile));
                lovTable.setQuery(query);
                return lovTable;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }

    private void findForeignLovs(BufferedReader br, String targetPattern, String impTargetPattern, String regex, String pathController) throws IOException {
        String line;
        while ((line = br.readLine()) != null) {
            String foreignLov = "";
            String newLine = "";
            if (line.contains(targetPattern)) {
                newLine = line.replaceAll("\\s", "").replaceAll("\\(|\\)", "");
                Pattern pattern = Pattern.compile(regex);
                Matcher matcher = pattern.matcher(newLine);
                if (matcher.find()) {
                    foreignLov = cutContent(matcher.group(0));
                    if (checkLovPath(foreignLov)) {
                        listLovs.add(foreignLov);
                    } else {
                        String link = generateLovPath(foreignLov, pathController);
                        if (checkLovPath(link)) {
                            listLovs.add(link);
                        }
                    }
                }
            }
        }
    }

    private String searchTable(BufferedReader br, String tablePattern, String regex, String link) throws IOException {
        String line;
        while ((line = br.readLine()) != null) {
            if (line.contains(tablePattern)){
                String tableLine = "";
                tableLine = line.replaceAll("\\n", "")
                                .replaceAll("\\s", "")
                                .replaceAll("@Table\\(name=\\\"", "")
                                .replaceAll("\"\\)", "");
                return tableLine;
            }
        }
        return null;
    }

    private String cutContent(String content){
        String lov = content.replaceAll("\\s", "")
                .replaceAll("JInvEntityLov<", "")
                .replaceAll(",.+", "")
                .replaceAll("import", "")
                .replaceAll("\\.", "/")
                .replaceAll(";", "");
        return lov;
    }

    private String generateLovPath(String nameLov, String path){
        return path.substring(0, path.lastIndexOf("/")+1) + nameLov + ".java";
    }

    private boolean checkLovPath(String lovPath){
        try (BufferedReader br = new BufferedReader(new FileReader(pathToMain+"\\result\\FXKu\\" + lovPath))) {
            return true;
        } catch (IOException e) {
            return false;
        }
    }
}
