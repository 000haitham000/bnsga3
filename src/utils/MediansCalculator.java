/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import static utils.Mathematics.getMax;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

/**
 *
 * @author Haitham
 */
public class MediansCalculator {

    private static String hv_reportFilePath
            = "D:\\results\\zdt1-03-30-030-020-P0048/hypervolumes_report.txt";
    private static String m_reportFilePath
            = "D:\\results\\zdt1-03-30-030-020-P0048/hv_medians_report.txt";

    public static void main(String[] args) throws IOException {
        generateMediansReport();
        generateComparisonsReport();
    }

    private static void generateMediansReport() throws NumberFormatException, IOException, IllegalArgumentException {
        System.out.print("Please, insert the number of runs: ");
        String runsCountString = new Scanner(System.in).next();
        int runsCount = Integer.parseInt(runsCountString);
        String mediansReport = parseHypervolumesReport(runsCount);
        File reportFile = new File(hv_reportFilePath);
        writeReport(mediansReport, reportFile.getParent() + "/hv_medians_report.txt");
    }

    private static void generateComparisonsReport() throws IOException {
        System.out.print("Please, insert number of algorithms you are comparing: ");
        int algCount = Integer.parseInt(new Scanner(System.in).next());
        String comparisonReport = parseMediansReport(algCount);
        File reportFile = new File(m_reportFilePath);
        writeReport(comparisonReport, reportFile.getParent() + "/hv_comparisons_report.txt");
    }

    private static String parseMediansReport(int algCount) throws IllegalArgumentException, IOException {
        double[] hvArr = new double[algCount];
        BufferedReader reader = null;
        try {
            StringBuilder comparisonReportBuilder = new StringBuilder();
            for (int i = 0; i < algCount; i++) {
                comparisonReportBuilder.append(
                        String.format(
                                "%30s",
                                String.format("%s%d", "Algorithm-", i)));
            }
            comparisonReportBuilder.append(String.format("%n"));
            comparisonReportBuilder.append(String.format("%30s%30s%30s%n",
                    "-----", "-----", "-----"));
            reader = new BufferedReader(new FileReader(m_reportFilePath));
            int lineCount = 0;
            String line;
            while ((line = reader.readLine()) != null) {
                hvArr[lineCount % algCount] = getHvValueFromLine(line, lineCount);
                if (lineCount % algCount == algCount - 1) {
                    int maxHvAlgIndex = Mathematics.getMaxIndex(hvArr);
                    int minHvAlgIndex = Mathematics.getMinIndex(hvArr);
                    comparisonReportBuilder.append(String.format("%n"));
                    for (int i = 0; i < hvArr.length; i++) {
                        if (i == maxHvAlgIndex) {
                            comparisonReportBuilder.append(String.format("%17s", "BEST "));
                        } else if (i == minHvAlgIndex) {
                            comparisonReportBuilder.append(String.format("%17s", "WORST"));
                        } else {
                            comparisonReportBuilder.append(String.format("%17s", "     "));
                        }
                        comparisonReportBuilder.append(String.format(" (%10.7f)", hvArr[i]));
                    }
                    comparisonReportBuilder.append(String.format("%n"));
                }
                lineCount++;
            }
            return comparisonReportBuilder.toString();
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
    }

    private static String parseHypervolumesReport(int runsCount) throws IllegalArgumentException, IOException {
        double[] hvArr = new double[runsCount];
        String[] linesArr = new String[runsCount];
        BufferedReader reader = null;
        try {
            StringBuilder mediansReportBuilder = new StringBuilder();
            reader = new BufferedReader(new FileReader(hv_reportFilePath));
            int lineCount = 0;
            String line;
            while ((line = reader.readLine()) != null) {
                hvArr[lineCount % runsCount] = getHvValueFromLine(line, lineCount);
                linesArr[lineCount % runsCount] = line;
                if (lineCount % runsCount == runsCount - 1) {
                    int medianIndex = Mathematics.getNonNegativesMedianIndex(hvArr);
                    if (medianIndex != -1) {
                        mediansReportBuilder.append(String.format("%s%n", linesArr[medianIndex]));
                    } else {
                        mediansReportBuilder.append(String.format("%s%n", linesArr[0]));
                    }
                }
                lineCount++;
            }
            return mediansReportBuilder.toString();
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
    }

    private static double getHvValueFromLine(String line, int lineCount) throws IllegalArgumentException {
        String[] splits = GeneralUtilities.replaceBlanksWithSingleSpace(line).split(" ");
        String lastSplit = splits[splits.length - 1];
        if (!lastSplit.startsWith("(")) {
            lastSplit = splits[splits.length - 2] + splits[splits.length - 1];
        }
        if (!lastSplit.startsWith("(") || !lastSplit.endsWith(")")) {
            throw new IllegalArgumentException(
                    String.format("Invalid file format: "
                            + "line %d does NOT end with the a number "
                            + "enclosed between parentheses.",
                            lineCount)
            );
        }
        String hvString = lastSplit.substring(
                1,
                lastSplit.length() - 1);
        return Double.parseDouble(hvString);
    }

    private static void writeReport(String text, String filePath) throws IOException {
        PrintWriter printer = null;
        try {
            printer = new PrintWriter(filePath);
            printer.print(text);
        } finally {
            if (printer != null) {
                printer.close();
            }
        }
    }

}
