/*
 * This file is used to compare the DIV metric and the GD metric of the two
 * algorithms NSGA-III and Diversity-NSGA-III. The two algorithms are compared
 * at equal function evaluations (x-axis) instead of equal number of
 * generations, because by the same specific generation, Diversity-NSGA-III
 * would have consumed more function evaluations. The code can be used to 
 * compare any two algorithms but first the names of the corresponding output
 * directories must be changed.
 */
package utils;

import emo.VirtualIndividual;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import javax.swing.JFileChooser;

/**
 *
 * @author Haitham
 */
public class Diversity2_Evaluations_Collector {

    final static JFileChooser fileChooser = new JFileChooser("E:\\temp");
    static int objCount = 2;

    public static void main(String[] args) throws IOException {
        // Open a directory search dialog
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fileChooser.setApproveButtonText("Open result directory");
        if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            File rootDir = fileChooser.getSelectedFile();
            // Check for the existence of the two subdirs: localSubDir and noLocalSubDir
            File subDirDiv = new File(rootDir.getPath() + File.separator + "nsga3_diversity_21JAN");
            File subDirNoDiv = new File(rootDir.getPath() + File.separator + "nsga3");
            if (!subDirDiv.exists() || !subDirNoDiv.exists()) {
                throw new IllegalArgumentException("The selected directory must"
                        + "contain two sub-directories, one representing "
                        + "NSGA-III with emphasis on diversity, and another "
                        + "representing ordinary NSGA-III with "
                        + "the following names respectively: "
                        + "\"nsga3_diversity_2\" and \"nsga3\"");
            }
            // Count the number of runs by counting the number of directories named
            // "generation_wise_runXXX". The numbers of runs in each subdirectory
            // need not match, it is recommended however to have the same number
            // of runs in each experiment for proper comparisons.
            int runCountDiv = countRuns(subDirDiv);
            int runCountNoDiv = countRuns(subDirNoDiv);
            // Count the number of dirs used for each experiment. Although, the
            // number of generations used in the two experiments might differ
            // (which is not recommended), the number of generations in each
            // experiment (in all the runs of the same experiment) MUST be the same.
            int genCountDiv = countGenerations(subDirDiv);
            int genCountNoDiv = countGenerations(subDirNoDiv);
            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            fileChooser.setApproveButtonText("Open Pareto front file");
            // Collect and dump metrics
            if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                // Collect and Dump both Div metric, GD and IGD metrics.
                collectAndDumpDiv(rootDir, subDirDiv, runCountDiv, genCountDiv, fileChooser.getSelectedFile(), "DIV");
                collectAndDumpDiv(rootDir, subDirNoDiv, runCountNoDiv, genCountNoDiv, fileChooser.getSelectedFile(), "NSGA3");
            } else {
                // Collect and Dump Div metric only
                collectAndDumpDiv(rootDir, subDirDiv, runCountDiv, genCountDiv, null, "DIV");
                collectAndDumpDiv(rootDir, subDirNoDiv, runCountNoDiv, genCountNoDiv, null, "NSGA3");
            }
        }
    }

    private static int countRuns(File subDir) {
        int runCount = 0;
        while (new File(subDir.getPath()
                + File.separator
                + String.format("generation_wise_run%03d",
                        runCount)).exists()) {
            runCount++;
        }
        return runCount;
    }

    private static int countGenerations(File subDir) {
        int genCount = 0;
        File sampleRunDirLocal = new File(subDir.getPath()
                + File.separator + "generation_wise_run000");
        while (new File(sampleRunDirLocal.getPath()
                + File.separator
                + String.format("obj_gen_%04d.dat",
                        genCount)).exists()) {
            genCount++;
        }
        return genCount;
    }

    private static int funEvalCount;
    private static double utopianEpsilon;

    private static void collectAndDumpDiv(
            File rootDir,
            File subDir,
            int runCount,
            int genCount,
            File paretoFile,
            String preMessage) throws IOException {
        
        double[][] divValues = new double[genCount][runCount];
        double[][] gdValues = null;
        double[][] igdValues = null;
        
        if (paretoFile != null) {
            gdValues = new double[genCount][runCount];
            igdValues = new double[genCount][runCount];
        }
        double[][] funEvals = new double[genCount][runCount];
        for (int i = 0; i < runCount; i++) {
            for (int j = 0; j < genCount; j++) {
                System.out.format("%s: Run(%02d) - Gen(%04d)%n", preMessage, i, j);
                // Load reference directions
                VirtualIndividual[] refDirs = InputOutput.loadIndividualsFromFile(new File(subDir.getAbsolutePath() + String.format("/generation_wise_run%03d/refdirs_gen_%04d.dat", i, j)));
                // Load population from file
                VirtualIndividual[] pop = InputOutput.loadIndividualsFromFile(new File(subDir.getAbsolutePath() + String.format("/generation_wise_run%03d/obj_gen_%04d.dat", i, j)));
                // Load meta-data: # function evaluations, ideal point and intercepts
                loadMetaData(new File(subDir.getAbsolutePath() + String.format("/generation_wise_run%03d/meta_data_gen_%04d.dat", i, j)));
                // Calculate the Div metric
                double diversityMetric = PerformanceMetrics.calculateDiversityMetric(pop, refDirs, utopianEpsilon);
                // Fill in corressponding positions
                divValues[j][i] = diversityMetric;
                funEvals[j][i] = funEvalCount;
                if (paretoFile != null) {
                    VirtualIndividual[] paretoFront = InputOutput.loadIndividualsFromFile(paretoFile);
                    double gd = PerformanceMetrics.calculateGenerationalDistance(objCount, pop, paretoFront, 2);
                    gdValues[j][i] = gd;
                    double igd = PerformanceMetrics.calculateInvertedGenerationalDistance(objCount, pop, paretoFront, 2);
                    igdValues[j][i] = igd;
                }
            }
        }
        // Get the median of each function evaluations row.
        double[] medianFunEvals = Mathematics.medianEachRow(funEvals);
        //double[] meanFunEvals = Mathematics.meanEachRow(funEvals);
        // Get the min, med and max corresponding to each row of function evaluations.
        double[][] divStats = new double[divValues.length][3];
        for (int i = 0; i < divValues.length; i++) {
            divStats[i][0] = Mathematics.getMin(divValues[i]);
            divStats[i][1] = Mathematics.getMedian(divValues[i]);
            //divStats[i][1] = Mathematics.getAverage(divValues[i]);
            divStats[i][2] = Mathematics.getMax(divValues[i]);
        }
        // Dump stats to file
        dumpStats(medianFunEvals, divStats, new File(rootDir.getPath() + "/div_" + subDir.getName()+ ".txt"));
        //dumpStats(meanFunEvals, divStats, new File(rootDir.getPath() + "/div_" + subDir.getName()+ ".txt"));
        if (paretoFile != null) {
            // GD
            // Get the min, med and max corresponding to each row of function evaluations.
            double[][] gdStats = new double[gdValues.length][3];
            for (int i = 0; i < gdValues.length; i++) {
                gdStats[i][0] = Mathematics.getMin(gdValues[i]);
                gdStats[i][1] = Mathematics.getMedian(gdValues[i]);
                //gdStats[i][1] = Mathematics.getAverage(gdValues[i]);
                gdStats[i][2] = Mathematics.getMax(gdValues[i]);
            }
            // Dump stats to file
            dumpStats(medianFunEvals, gdStats, new File(rootDir.getPath() + "/gd_" + subDir.getName()+ ".txt"));
            //dumpStats(meanFunEvals, gdStats, new File(rootDir.getPath() + "/gd_" + subDir.getName()+ ".txt"));
            // IGD
            // Get the min, med and max corresponding to each row of function evaluations.
            double[][] igdStats = new double[igdValues.length][3];
            for (int i = 0; i < igdValues.length; i++) {
                igdStats[i][0] = Mathematics.getMin(igdValues[i]);
                igdStats[i][1] = Mathematics.getMedian(igdValues[i]);
                //gdStats[i][1] = Mathematics.getAverage(gdValues[i]);
                igdStats[i][2] = Mathematics.getMax(igdValues[i]);
            }
            // Dump stats to file
            dumpStats(medianFunEvals, igdStats, new File(rootDir.getPath() + "/igd_" + subDir.getName()+ ".txt"));
        }
    }

    private static void loadMetaData(File file) throws IOException, NumberFormatException {
        BufferedReader metaReader = null;
        try {
            metaReader = new BufferedReader(new FileReader(file));
            String line;
            while ((line = metaReader.readLine()) != null) {
                String[] splits = line.split("=");
                if (splits[0].trim().equals("fun_eval")) {
                    funEvalCount = Integer.parseInt(splits[1].trim());
                } else if (splits[0].trim().equals("utopian_epsilon")) {
                    utopianEpsilon = Double.parseDouble(splits[1].trim());
                }/* else if (splits[0].trim().equals("ideal")) {
                 String rightHandSide = splits[1].trim();
                 rightHandSide = rightHandSide.substring(1, rightHandSide.length() - 1);
                 String[] subSplits = rightHandSide.split(",");
                 ideal = new double[subSplits.length];
                 for (int k = 0; k < subSplits.length; k++) {
                 ideal[k] = Double.parseDouble(subSplits[k]);
                 }
                 } else if (splits[0].trim().equals("intercepts")) {
                 String rightHandSide = splits[1].trim();
                 rightHandSide = rightHandSide.substring(1, rightHandSide.length() - 1);
                 String[] subSplits = rightHandSide.split(",");
                 intercepts = new double[subSplits.length];
                 for (int k = 0; k < subSplits.length; k++) {
                 intercepts[k] = Double.parseDouble(subSplits[k]);
                 }
                 }*/

            }
        } finally {
            if (metaReader != null) {
                metaReader.close();
            }
        }
    }

    private static void dumpStats(double[] medianFunEvals, double[][] metricStats, File file) throws FileNotFoundException {
        PrintWriter printer = null;
        try {
            printer = new PrintWriter(file);
            for (int i = 0; i < medianFunEvals.length; i++) {
                printer.format("%04d ", i);
                printer.format("%07d ", (int) medianFunEvals[i]);
                printer.format("%010.7f ", metricStats[i][0]);
                printer.format("%010.7f ", metricStats[i][1]);
                printer.format("%010.7f%n", metricStats[i][2]);
            }
        } finally {
            if (printer != null) {
                printer.close();
            }
        }
    }
}
