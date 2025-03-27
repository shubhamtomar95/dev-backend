package com.flxpoint.csv;

import java.util.*;

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
        // TODO: Implement logic to read file
        return Collections.emptyList();
    }

    private static List<String> removeDuplicates(List<String> lines) {
        // TODO: Implement logic to remove duplicates while keeping the header
        return lines;
    }

    private static void writeCsvFile(String filePath, List<String> lines) {
        // TODO: Implement logic to write file
    }
}