/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package optimization;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import javax.xml.stream.XMLStreamException;
import parsing.InvalidOptimizationProblemException;

/**
 *
 * @author toshiba
 */
public class CalculateSetCoverageMetric {

    private static String dataFileName1 = "NSGA2-zdt6-02-10-G350-P048-run002-data.dat";
    private static String dataFileName2 = "NSGA3-zdt6-02-10-G350-P048-run042-data.dat";
    private static String rootDir = "D:/Dropbox/Work/NSGA/results/";

    public static void main(String[] args) throws IOException, XMLStreamException, InvalidOptimizationProblemException {
        String algorithm1 = dataFileName1.split("-")[0];
        String algorithm2 = dataFileName2.split("-")[0];
        BufferedReader bReader1 = null;
        BufferedReader bReader2 = null;
        PrintWriter printer = null;
        List<double[]> list1 = new ArrayList<double[]>();
        List<double[]> list2 = new ArrayList<double[]>();
        try {
            String line;
            // Read the contents of the first file
            bReader1 = new BufferedReader(new FileReader(rootDir + dataFileName1));
            while ((line = bReader1.readLine()) != null) {
                if (!line.startsWith("#") && !line.trim().equals("")) {
                    line = convertBlanksToSingleSpaces(line);
                    double[] tempArr = new double[2];
                    tempArr[0] = Double.parseDouble(line.split(" ")[0]);
                    tempArr[1] = Double.parseDouble(line.split(" ")[1]);
                    list1.add(tempArr);
                }
            }
            // Read the contents of the second file
            bReader2 = new BufferedReader(new FileReader(rootDir + dataFileName2));
            while ((line = bReader2.readLine()) != null) {
                if (!line.startsWith("#") && !line.trim().equals("")) {
                    line = convertBlanksToSingleSpaces(line);
                    double[] tempArr = new double[2];
                    tempArr[0] = Double.parseDouble(line.split(" ")[0]);
                    tempArr[1] = Double.parseDouble(line.split(" ")[1]);
                    list2.add(tempArr);
                }
            }
            double setCoverageMetricAB = getSetCoverageMetric(list1, list2);
            double setCoverageMetricBA = getSetCoverageMetric(list2, list1);
            // Initialize Printer
            printer = new PrintWriter(rootDir + "SetCoverageMetric.dat");
            printer.format("C(%s,%s) = %5.3f%n", algorithm1, algorithm2, setCoverageMetricAB);
            printer.format("C(%s,%s) = %5.3f%n", algorithm2, algorithm1, setCoverageMetricBA);
        } finally {
            if (bReader1 != null) {
                bReader1.close();
            }
            if (bReader2 != null) {
                bReader2.close();
            }
            if (printer != null) {
                printer.close();
            }
        }
    }

    private static String convertBlanksToSingleSpaces(String line) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < line.length(); i++) {
            if (i > 0) {
                if (Character.isWhitespace(line.charAt(i - 1)) && Character.isWhitespace(line.charAt(i))) {
                    continue;
                }
            }
            sb.append(line.charAt(i));
        }
        return sb.toString();
    }

    private static boolean weaklyDominates(double[] aIndividual, double[] bIndividual) {
        for (int i = 0; i < aIndividual.length; i++) {
            if (aIndividual[i] > bIndividual[i]) {
                return false;
            }
        }
        return true;
    }

    private static double getSetCoverageMetric(List<double[]> list1, List<double[]> list2) {
        // Calculate the Set Coverage Metric
        double cAB = 0;
        for (double[] bIndividual : list2) {
            for (double[] aIndividual : list1) {
                if (weaklyDominates(aIndividual, bIndividual)) {
                    cAB++;
                    break;
                }
            }
        }
        double setCoverageMetric = cAB / list2.size();
        return setCoverageMetric;
    }
}
