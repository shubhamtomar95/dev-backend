package com.flxpoint.csv;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.nio.file.*;

public class CSVHelper {

    public static void main(String[] args) {
        String inputFile = "src/main/resources/input.csv";
        String outputFile = "src/main/resources/output.csv";

        List<String> lines = readCsvFile(inputFile);
        List<String> uniqueLines = removeDuplicates(lines);

        writeCsvFile(outputFile, uniqueLines);
        System.out.println("Deduplicated CSV saved to " + outputFile);
    }

    private static List<String> readCsvFile(String filePath) {

        try {
            return Files.readAllLines(Paths.get(filePath));
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        }
        return Collections.emptyList();
    }

    private static List<String> removeDuplicates(List<String> lines) {
        if (lines.isEmpty()) return lines;
        String header = lines.get(0);
        Set<String> hashSet = new LinkedHashSet<>(); //hashSet to hash all rows
        List<String> result = new ArrayList<>();
        result.add(header);
        //Iterate rows, split each row and generate 'hash-key' with name and email column
        for (int i = 1; i < lines.size(); i++) {
            String line = lines.get(i);
            String[] tokens = line.split(",");
            if (tokens.length != 3) continue;
            String name = tokens[1].trim();
            String email = tokens[2].trim();
            String key = name + "|" + email;
            //add row visited for first time, avoiding duplicates
            if (hashSet.add(key)) {
                result.add(line);
            }
        }
        return result;
    }

    private static void writeCsvFile(String filePath, List<String> lines) {
        try {
            Files.write(Paths.get(filePath), lines);
        } catch (IOException e) {
            System.err.println("Error writing file: " + e.getMessage());
        }
    }
}
