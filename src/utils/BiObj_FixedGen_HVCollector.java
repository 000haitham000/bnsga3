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
public class BiObj_FixedGen_HVCollector {

    static String dir = "d:/results/_bi_objective";
    static JFileChooser fileChooser;

    public static void main(String[] args) throws IOException {
        fileChooser = new JFileChooser(new File(dir));
        fileChooser.setMultiSelectionEnabled(true);
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            File[] dirs = fileChooser.getSelectedFiles();
            System.out.format("%nCollecting median HV values%n%n");
            collectHvStats(dirs, "median", "hv_median_stats.dat");
            System.out.format("%nCollecting best HV values%n%n");
            collectHvStats(dirs, "best", "hv_best_stats.dat");
            System.out.format("%nCollecting worst HV values%n%n");
            collectHvStats(dirs, "worst", "hv_worst_stats.dat");
        }
    }

    public static void collectHvStats(File[] dirs, String tag, String fileName) throws IOException, FileNotFoundException {
        List<Double> nsga2HvValues = new ArrayList<Double>();
        List<Double> nsga3HvValues = new ArrayList<Double>();
        List<Double> uniNsga3HvValues = new ArrayList<Double>();
        for (int i = 0; i < dirs.length; i++) {
            System.out.format("\tDir(%02d) >> \"%s\"%n", i, dirs[i].getPath());
            System.out.format("\t\t%-12s>>", "NSGA-II");
            double nsga2HV = getHV(dirs[i], "nsga2", tag);
            System.out.format("\t\t%-12s>>", "NSGA-III");
            double nsga3HV = getHV(dirs[i], "nsga3", tag);
            System.out.format("\t\t%-12s>>", "U-NSGA-III");
            double uniNsga3HV = getHV(dirs[i], "unified_nsga3", tag);
            nsga2HvValues.add(nsga2HV);
            nsga3HvValues.add(nsga3HV);
            uniNsga3HvValues.add(uniNsga3HV);
        }
        dumpHVResults(dir, nsga2HvValues, nsga3HvValues, uniNsga3HvValues, fileName);
    }

    public static double getHV(File dir, String subDirName, String tag) throws FileNotFoundException, IOException {
        File subDir = new File(dir.getPath() + "/" + subDirName);
        File[] allFiles = subDir.listFiles();
        File metricsFile = null;
        for (File file : allFiles) {
            if (file.getName().contains("metrics")) {
                metricsFile = file;
                System.out.format("\t%s%n", metricsFile.getPath());
                break;
            }
        }
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(metricsFile));
            boolean medianTagEncountered = false;
            String line;
            while ((line = reader.readLine()) != null) {
                if (medianTagEncountered && line.toLowerCase().contains("hv")) {
                    String[] splits = line.trim().split(" ");
                    return Double.parseDouble(splits[splits.length - 1]);
                }
                if (line.toLowerCase().contains(tag)) {
                    medianTagEncountered = true;
                }
            }
            throw new IllegalArgumentException("Invalid File Format");
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
    }

    private static void dumpHVResults(
            String dir,
            List<Double> nsga2HvValues,
            List<Double> nsga3HvValues,
            List<Double> uniNsga3HvValues,
            String fileName) throws FileNotFoundException {
        File hvStatsFile = new File(dir + "/" + fileName);
        PrintWriter printer = null;
        try {
            printer = new PrintWriter(hvStatsFile);
            //printer.format("%-8s %-8s %-15s%n", "NSGA-II", "NSGA-III", "Uni-NSGA-III");
            for (int i = 0; i < nsga2HvValues.size(); i++) {
                printer.format("%08.7f %08.7f %08.7f%n", nsga2HvValues.get(i), nsga3HvValues.get(i), uniNsga3HvValues.get(i));
            }
        } finally {
            if (printer != null) {
                printer.close();
            }
        }
    }
}
