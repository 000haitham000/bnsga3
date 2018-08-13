/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Haitham
 */
public class QSurvivalAcrossRunsStatsCollector {

    private static File dir = new File("D:\\results\\15obj_dtlz1-030-020-P0136-G2000\\nsga3");

    public static void main(String[] args) throws FileNotFoundException, IOException {
        File[] files = collectSetCoverageStatsFilesOfAllRuns(dir);
        // Make sure that all files have the same number of generations
        int genCount = getGenCount(files);
        // Create the structure that will hold the averages
        double[] qSurvivalStats = new double[genCount];
        // Update the structure using the set coverage stats of each run
        for (File file : files) {
            updateAverageStats(file, files.length, qSurvivalStats);
        }
        // Print the final set coverage stats averages to a file
        InputOutput.dumpQSurvivingStatistics(qSurvivalStats, -1, dir.getPath()+"/");
    }

    private static File[] collectSetCoverageStatsFilesOfAllRuns(File dir) {
        if (!dir.isDirectory()) {
            throw new UnsupportedOperationException("This parameter must be a directory.");
        }
        File[] allFiles = dir.listFiles();
        List<File> designatedFiles = new ArrayList<File>();
        for (File file : allFiles) {
            if (file.isFile() && file.getName().startsWith("q_survival_percent")) {
                designatedFiles.add(file);
            }
        }
        File[] designatedFilesArr = new File[designatedFiles.size()];
        designatedFiles.toArray(designatedFilesArr);
        return designatedFilesArr;
    }

    private static int getGenCount(File[] files) throws IOException {
        int linesCount = countLines(files[0]);
        for (int i = 1; i < files.length; i++) {
            if (countLines(files[i]) != linesCount) {
                throw new UnsupportedOperationException("All files must have the same number of generations");
            }
        }
        return linesCount;
    }

    private static void updateAverageStats(File file, int runsCount, double[] qSurvivalStats) throws IOException {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            String line;
            while((line = reader.readLine()) != null) {
                String[] splits = line.split(" ");
                int genIndex = Integer.parseInt(splits[0]);
                double qSurvivalPercent = Double.parseDouble(splits[1]);
                qSurvivalStats[genIndex] += qSurvivalPercent/runsCount;
            }
        } finally {
            if(reader != null) {
                reader.close();
            }
        }
    }

    public static int countLines(File file) throws IOException {
        InputStream is = new BufferedInputStream(new FileInputStream(file));
        try {
            byte[] c = new byte[1024];
            int count = 0;
            int readChars = 0;
            boolean empty = true;
            while ((readChars = is.read(c)) != -1) {
                empty = false;
                for (int i = 0; i < readChars; ++i) {
                    if (c[i] == '\n') {
                        ++count;
                    }
                }
            }
            return (count == 0 && !empty) ? 1 : count;
        } finally {
            is.close();
        }
    }
}
