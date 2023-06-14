package ru.inversion.migration_assistant.service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ParseQuery {
    public String executeQuery(String pathFile) {
        String targetPattern = "@NamedNativeQuery";
        if (Files.exists(Paths.get(pathFile))) {
            try (BufferedReader br = new BufferedReader(new FileReader(pathFile))) {
                return findQueryDefinition(br, targetPattern);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }

    private String findQueryDefinition(BufferedReader reader, String targetPattern) throws IOException {
        Integer countOpenBracket = 0;
        boolean flagStartCountBracket = false;
        String resultLine = "";
        String line;
        while ((line = reader.readLine()) != null) {
            if (line.contains(targetPattern) || flagStartCountBracket) {
                countOpenBracket = countBracket(line, countOpenBracket);
                resultLine += line;
                flagStartCountBracket = true;
                if (countOpenBracket == 0) {
                    return resultLine.trim();
                }
            }
        }
        return null;
    }

    private Integer countBracket(String bracketLine, Integer countOpenBracket) {
        for (int i=0; i<bracketLine.length(); i++){
            if (bracketLine.charAt(i) == '('){
                countOpenBracket++;
            }
            if (bracketLine.charAt(i) == ')'){
                countOpenBracket--;
            }
        }
        return countOpenBracket;
    }

    public String cutQuery(String query) {
        String X = query.replaceAll("\\n", "").replaceAll("^.+query", "").replaceAll("^.+?\"", "").replaceAll("\".+?\"", "");
        return X;
    }
}
