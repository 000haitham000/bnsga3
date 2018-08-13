/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author seadahai
 */
public class MatlabIO {

    /**
     * Read a 2D matrix from a file just like Matlab dlmread() function does.
     * @param filePath The input file to read from
     * @return A 2D double array representing the matrix read. 
     * @throws IOException If a problem occurred while reading from the file.
     */
    public static double[][] dlmread2D(String filePath) throws IOException {
        // Create a list to accumulate al the lines
        List<String> linesList;
        // Create input stream
        BufferedReader reader = null;
        try {
            // Read all lines and add them to the list
            reader = new BufferedReader(new FileReader(filePath));
            linesList = new ArrayList<>();
            String line;
            while ((line = reader.readLine()) != null) {
                linesList.add(line);
            }
            // Fill in the double array
            if (linesList.isEmpty()) {
                System.out.println("Input file is empty");
                return new double[0][0];
            } else {
                double[][] m = new double[linesList.size()][linesList.get(0).split("\\s").length];
                for (int i = 0; i < linesList.size(); i++) {
                    String[] splits = linesList.get(i).split("\\s");
                    for (int j = 0; j < splits.length; j++) {
                        m[i][j] = Double.parseDouble(splits[j].trim());
                    }
                }
                // Return the final double array
                return m;
            }
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
    }

    /**
     * Write a 2D matrix to a file just like Matlab dlmwrite() function does.
     * @param filePath Output file path.
     * @param m The matrix to write.
     */
    public static void dlmwrite2D(String filePath, double[][] m) throws IOException {
        PrintWriter printer = null;
        try {
            printer = new PrintWriter(new FileWriter(filePath));
            for (double[] mi : m) {
                for (int j = 0; j < mi.length; j++) {
                    printer.format("%8.6f", mi[j]);
                    if(j != mi.length - 1) {
                        printer.print("\t");
                    }
                }
                printer.println();
            }
        } finally {
            if(printer != null) {
                printer.close();
            }
        }
    }
}
