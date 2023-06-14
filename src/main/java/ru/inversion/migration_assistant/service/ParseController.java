package ru.inversion.migration_assistant.service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ParseController {
    String pathToMain = "C:\\Users\\Koryshev.INVERSION\\Desktop\\gvboishnbv'\\controller-db-links-back\\src\\main";
    List<String> listTable = new ArrayList<>();

    public void findTables(String pathController) {
        listTable = new ArrayList<>();
        String targetPattern = "private";
        String tableRegex = "private JInvTable\\<.+\\>";
        try (BufferedReader br = new BufferedReader(new FileReader(pathToMain+"\\result\\FXKu\\" + pathController))) {
            findForeignTable(br, targetPattern, tableRegex);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void findForeignTable(BufferedReader br, String targetPattern, String regex) throws IOException {
        String line;
        while ((line = br.readLine()) != null) {
            if (line.contains(targetPattern)) {
                Pattern pattern = Pattern.compile(regex);
                Matcher matcher = pattern.matcher(line);
                if (matcher.find()) {
                    String content = matcher.group(0);
                    List<String> retLink = cutArray(Collections.singletonList((content)));
                    listTable.add(retLink.toString());
                }
            }
        }
    }

    private List<String> cutArray(List<String> rtLink) {
        List<String> linksArray = new ArrayList<>();
        for (String link : rtLink) {
            int startIndex = link.indexOf("<") + 1;
            int endIndex = link.indexOf(">");
            String trimmedLink = link.substring(startIndex, endIndex);
            linksArray.add(trimmedLink);
        }
        return linksArray;
    }
}
