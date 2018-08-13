/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JFileChooser;

/**
 *
 * @author Haitham
 */
public class SingleObj_GenerationWise_StatisticsCollector {

    final static File currentDirectory = new File("D:\\results");
    static JFileChooser fileChooser = new JFileChooser(currentDirectory);

    public static void main(String[] args) throws IOException {
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            File dir = fileChooser.getSelectedFile();
            if (dir.isDirectory()) {
                File[] subDirs = dir.listFiles();
                List<File> runsDirs = new ArrayList<File>();
                for (File f : subDirs) {
                    if (f.isDirectory() && f.getName().startsWith("generation_wise_run")) {
                        runsDirs.add(f);
                    }
                }
                if (!runsDirs.isEmpty()) {
                    int generationsCount = runsDirs.get(0).listFiles().length;
                    int populationSize = getPopulationSize(runsDirs.get(0).listFiles()[0]);
                    double[][][] data = new double[runsDirs.size()][generationsCount][populationSize];
                    for (int i = 0; i < runsDirs.size(); i++) {
                        if (!runsDirs.get(i).getName().equals(String.format("generation_wise_run%03d", i))) {
                            throw new IllegalArgumentException("Directory names  do not follow the convention");
                        }
                        for (int j = 0; j < generationsCount; j++) {
                            System.out.format("Parsing Run(%03d) Gen(%04d)...%n", i, j);
                            File genFile = getFileOfGeneration(j, runsDirs.get(i));
                            double[] objValues = getObjValuesFromFile(genFile);
                            for(int k = 0; k < populationSize; k++) {
                                data[i][j][k] = objValues[k];
                            }
                        }
                    }
                    // Dump the collected results into a file formatted as required
                    InputOutput.dumpSingleObjGenerationWiseAcrossRunsResults(data, dir.getPath() + "/" + dir.getName().toLowerCase() + "_across_runs_stats.dat");
                }
            }
        }
    }

    private static File getFileOfGeneration(int j, File dir) {
        File[] generationFiles = dir.listFiles();
        String shouldBeName = String.format("gen_%04d_obj.dat", j);
        if (generationFiles[j].isFile() && generationFiles[j].getName().equals(shouldBeName)) {
            return generationFiles[j];
        }
        for (File f : generationFiles) {
            if (f.getName().equals(shouldBeName)) {
                return f;
            }
        }
        throw new IllegalArgumentException("Data files do not follow the naming convention");
    }

    private static double[] getObjValuesFromFile(File file) throws IOException {
        List<Double> objValuesList = new ArrayList<Double>();
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            String line;
            while ((line = reader.readLine()) != null && !line.trim().equals("")) {
                objValuesList.add(Double.parseDouble(line.trim()));
            }
            double[] objValues = new double[objValuesList.size()];
            for (int i = 0; i < objValuesList.size(); i++) {
                objValues[i] = objValuesList.get(i);
            }
            return objValues;
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
    }

    private static int getPopulationSize(File file) throws IOException {
        int popSize = 0;
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            String line;
            while ((line = reader.readLine()) != null) {
                popSize++;
            }
            return popSize;
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
    }

}
