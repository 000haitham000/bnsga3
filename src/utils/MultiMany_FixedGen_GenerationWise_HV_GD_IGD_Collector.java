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
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/**
 *
 * @author Haitham
 */
public class MultiMany_FixedGen_GenerationWise_HV_GD_IGD_Collector {

    public static void main(String[] args) throws IOException {
        try {
            // Adjust look & feel
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(MultiMany_FixedGen_GenerationWise_HV_GD_IGD_Collector.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            Logger.getLogger(MultiMany_FixedGen_GenerationWise_HV_GD_IGD_Collector.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(MultiMany_FixedGen_GenerationWise_HV_GD_IGD_Collector.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedLookAndFeelException ex) {
            Logger.getLogger(MultiMany_FixedGen_GenerationWise_HV_GD_IGD_Collector.class.getName()).log(Level.SEVERE, null, ex);
        }
        // Browse the hypervolumes file
        JFileChooser fileChooser = new JFileChooser("D:\\results\\CEC2015 results");
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            File hypervolumesFile = new File(fileChooser.getSelectedFile().getPath() + "/hypervolumes_report.txt");
            File gdFile = new File(fileChooser.getSelectedFile().getPath() + "/gd_report.txt");
            File igdFile = new File(fileChooser.getSelectedFile().getPath() + "/igd_report.txt");
            // Find how many lines are there in that file
            int linesCount = countLines(gdFile.getPath());
            // Read the number of generations
            System.out.println("Please, enter number of generations: ");
            int genCount = new Scanner(System.in).nextInt();
            // Calculate the number of runs
            int runsCount = (int) (linesCount / genCount);
            if (Mathematics.compare(runsCount, linesCount / genCount) != 0) {
                System.out.println("Illegal file format: "
                        + "number of lines in the file indicates that "
                        + "not all runs have the same number of generations, "
                        + "or that some lines are missing.");
                System.exit(-1);
            }
            // Create an array to store the averages
            double[] hvAverages = new double[genCount];
            double[] gdAverages = new double[genCount];
            double[] igdAverages = new double[genCount];
            // Parse your selected file and take the average of HV values for all runs
            BufferedReader hvReader = null;
            PrintWriter hvPrinter = null;
            BufferedReader gdReader = null;
            PrintWriter gdPrinter = null;
            BufferedReader igdReader = null;
            PrintWriter igdPrinter = null;
            try {
                hvReader = new BufferedReader(new FileReader(hypervolumesFile));
                hvPrinter = new PrintWriter(hypervolumesFile.getParentFile().getPath() + "/hypervolumes_avg.txt");
                gdReader = new BufferedReader(new FileReader(gdFile));
                gdPrinter = new PrintWriter(gdFile.getParentFile().getPath() + "/gd_avg.txt");
                igdReader = new BufferedReader(new FileReader(igdFile));
                igdPrinter = new PrintWriter(igdFile.getParentFile().getPath() + "/igd_avg.txt");
                // Store these averages
                for (int i = 0; i < runsCount; i++) {
                    for (int j = 0; j < genCount; j++) {
                        // HV
                        String hvLine = hvReader.readLine();
                        String hvFinalPart = processLine(hvLine);
                        hvAverages[j] += Double.parseDouble(hvFinalPart) / runsCount;
                        // GD
                        String gdLine = gdReader.readLine();
                        String gdFinalPart = processLine(gdLine);
                        gdAverages[j] += Double.parseDouble(gdFinalPart) / runsCount;
                        // IGD
                        String igdLine = igdReader.readLine();
                        String igdFinalPart = processLine(igdLine);
                        igdAverages[j] += Double.parseDouble(igdFinalPart) / runsCount;
                    }
                }
                // Dump the stored averages to a file
                // HV
                for (int i = 0; i < hvAverages.length; i++) {
                    hvPrinter.format("%03d %010.8f%n", i, hvAverages[i]);
                }
                // GD
                for (int i = 0; i < gdAverages.length; i++) {
                    gdPrinter.format("%03d %010.8f%n", i, gdAverages[i]);
                }
                // IGD
                for (int i = 0; i < igdAverages.length; i++) {
                    igdPrinter.format("%03d %010.8f%n", i, igdAverages[i]);
                }
                System.out.println("Averages files successfully created.");
            } finally {
                if (hvReader != null) {
                    hvReader.close();
                }
                if (hvPrinter != null) {
                    hvPrinter.close();
                }
                if (gdReader != null) {
                    gdReader.close();
                }
                if (gdPrinter != null) {
                    gdPrinter.close();
                }
                if (igdReader != null) {
                    igdReader.close();
                }
                if (igdPrinter != null) {
                    igdPrinter.close();
                }
            }
        }
    }

    private static String processLine(String line) {
        String[] parts = line.split(" ");
        String finalPart = parts[parts.length - 1];
        if (finalPart.endsWith(")")) {
            finalPart = finalPart.substring(0, finalPart.length() - 2);
        } else {
            System.out.println("Invalid file format");
            System.exit(-1);
        }
        if (finalPart.startsWith("(")) {
            finalPart = finalPart.substring(1);
        }
        return finalPart;
    }

    public static int countLines(String filename) throws IOException {
        InputStream is = new BufferedInputStream(new FileInputStream(filename));
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
