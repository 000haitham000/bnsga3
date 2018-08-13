/*
 * This file is created to collect the results for the local search paper sent
 * the Journal of Global Optimization (JOGO).
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
import java.util.Scanner;
import javax.swing.JFileChooser;

/**
 *
 * @author Haitham
 */
public class KKT_BiObj_GenerationWise_StatisticsCollector {

    final static JFileChooser fileChooser = new JFileChooser("d:\\results\\local_search");

    public static void main(String[] args) throws FileNotFoundException, IOException {
        // Open a directory search dialog
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            File rootDir = fileChooser.getSelectedFile();
            // Check for the existence of the two subdirs: localSubDir and noLocalSubDir
            File subDirLocal = new File(rootDir.getPath() + File.separator + "nsga3_local_search");
            File subDirNoLocal = new File(rootDir.getPath() + File.separator + "nsga3");
            if (!subDirLocal.exists() || !subDirNoLocal.exists()) {
                throw new IllegalArgumentException("The selected directory must"
                        + "contain two sub-directories, one represents "
                        + "NSGA-III with local search, while the other "
                        + "represents NSGA-III without local search with "
                        + "the following names respectively: "
                        + "\"nsga3_local_search\" and \"nsga3\"");
            }
            // Try to figure out the population size from the name of the directory
            // otherwise, prompt the user to insert it.
            Scanner scanner = new Scanner(System.in);
            int popSize = -1;
            String[] splits = rootDir.getName().split("-");
            for (String split : splits) {
                if (split.matches("P\\d+")) {
                    popSize = Integer.parseInt(split.substring(1));
                }
            }
            if (popSize == -1) {
                System.out.print("Please, insert experiment-1 population size: ");
                popSize = scanner.nextInt();
            }
            // Insert the interval of local search i.e. every-how-many generations
            System.out.print("Please, insert the local search interval (per generation): ");
            int localSearchInterval = scanner.nextInt();
//            System.out.print("Please, insert the local search max func eval: ");
//            int maxLocalSearchFunEval = scanner.nextInt();
            // Insert the actual average number of function evaluations
            // required by a single local-search run.
            System.out.print("Please, insert the actual average fun. evals. of a SINGLE local search run: ");
            int actualLocalTotalEvals = scanner.nextInt();
            // Count the number of runs by counting the number of directories named
            // "generation_wise_runXXX". The numbers of runs in each subdirectory
            // need not match, it is recommended however to have the same number
            // of runs in each experiment for proper comparisons.
            int runCountLocal = countRuns(subDirLocal);
            int runCountNoLocal = countRuns(subDirLocal);
            // Count the number of dirs used for each experiment. Although, the
            // number of generations used in the two experiments might differ
            // (which is not recommended), the number of generations in each
            // experiment (in all the runs of the same experiment) MUST be the same.
            int genCountLocal = countGenerations(subDirLocal);
            int actualLocalEvalsPerGeneration
                    = ((actualLocalTotalEvals - genCountLocal * popSize - popSize)
                    /* * popSize */
                    / (int) Math.ceil(genCountLocal / localSearchInterval))
                    /* - 1 */;
            int genCountNoLocal = countGenerations(subDirNoLocal);
            // Collect and dump statistics of the first subdir
            collectAndDumpStats(genCountLocal, runCountLocal, subDirLocal,
                    popSize, localSearchInterval, actualLocalEvalsPerGeneration);
            // Collect and dump statistics of the second subdir
            collectAndDumpStats(genCountNoLocal, runCountNoLocal, subDirNoLocal,
                    popSize, localSearchInterval, 0);
        }
    }

    private static void collectAndDumpStats(int genCount, int runCount, File subDir, int popSize, int localSearchInterval, int actualLocalEvalsPerGeneration) throws IOException, FileNotFoundException {
        // Create a 2D array of size (genCount, exp1RunsCount) to hold the
        // minimum KKT value of each generation at each run. (minAllRunsKkt)
        double[][] minAllRunsKkt = new double[genCount][runCount];
        // Create a 2D array of size (genCount, exp1RunsCount) to hold the
        // median KKT value of each generation at each run. (medianAllRunsKkt)
        double[][] medianAllRunsKkt = new double[genCount][runCount];
        // Create a 2D array of size (genCount, exp1RunsCount) to hold the
        // mean KKT value of each generation at each run. (meanAllRunsKkt)
        double[][] meanAllRunsKkt = new double[genCount][runCount];
        // Create a 2D array of size (genCount, exp1RunsCount) to hold the
        // max KKT value of each generation at each run. (maxAllRunsKkt)
        double[][] maxAllRunsKkt = new double[genCount][runCount];
        // Fill all the previous arrays
        for (int i = 0; i < genCount; i++) {
            for (int j = 0; j < runCount; j++) {
                System.out.println("Run = " + j + " - Gen = " + i);
                File generationFile = new File(subDir.getPath()
                        + File.separator
                        + String.format("generation_wise_run%03d", j)
                        + File.separator
                        + String.format("gen_%04d_kkt.dat", i));
                if (!generationFile.exists()) {
                    throw new FileNotFoundException("Missing generation file");
                }
                // Calculate statistics and Fill in the four 2D arrays
                double[] kktValues = readDoublesArr(generationFile);
                double min = Mathematics.getMin(kktValues);
                minAllRunsKkt[i][j] = min;
                double median = Mathematics.getMedian(kktValues);
                medianAllRunsKkt[i][j] = median;
                double mean = Mathematics.getAverage(kktValues);
                meanAllRunsKkt[i][j] = mean;
                double max = Mathematics.getMax(kktValues);
                maxAllRunsKkt[i][j] = max;
            }
        }
        // From each of the four 2D arrays created above, create another 1D
        // array with size (genCount) holding the median of the corresponding
        // statistic in each generation. (minKkt, medianKkt, meanKkt and maxKkt)
        double[] minKktValues = Mathematics.medianEachRow(minAllRunsKkt);
        double[] medianKktValues = Mathematics.medianEachRow(medianAllRunsKkt);
        double[] meanKktValues = Mathematics.medianEachRow(meanAllRunsKkt);
        double[] maxKktValues = Mathematics.medianEachRow(maxAllRunsKkt);
        // Create a 1D array with size (genCount) containing the number of
        // function evaluations at each generation. This number is equal to:
        // FE(G_index) = (P+G_index*P) in the no-local search case, and
        // FE(G_index) = (P+(G_index+1)*P+(G_index/INTERVAL+1)*MAX_LOCAL_EVAL) in the local search case
        int[] funEvalValues = new int[genCount];
        for (int i = 0; i < funEvalValues.length; i++) {
            if (actualLocalEvalsPerGeneration == 0) {
                // No local search
                funEvalValues[i]
                        = popSize
                        + (i + 1) * popSize;
            } else {
                // Local search
                funEvalValues[i]
                        = popSize
                        + (i + 1) * popSize
                        + ((int) Math.ceil((i + 1) / localSearchInterval)
                        * (actualLocalEvalsPerGeneration /* + 1 */) /* / popSize */);
            }
            //funEvalValues[i] = popSize + (i + 1) * popSize + (i / localSearchInterval + 1) * actualLocalEvalsPerGeneration;
        }
        // Write a file to the first subdir with the following format:
        //  COL1: Function evaluations per generation
        //  COL2: minKkt array
        //  COL3: medianKkt array
        //  COL4: meanKkt array
        //  COL5: maxKkt array
        File outFileLocal = new File(subDir.getPath()
                + File.separator + "kkt_all_runs_stats.dat");
        dumpStats(funEvalValues, minKktValues, medianKktValues,
                meanKktValues, maxKktValues, outFileLocal);
    }

    private static int countGenerations(File subDir) {
        int genCount = 0;
        File sampleRunDirLocal = new File(subDir.getPath()
                + File.separator + "generation_wise_run000");
        while (new File(sampleRunDirLocal.getPath()
                + File.separator
                + String.format("gen_%04d_kkt.dat",
                        genCount)).exists()) {
            genCount++;
        }
        return genCount;
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

    private static double[] readDoublesArr(File file) throws IOException {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            List<Double> valuesList = new ArrayList<Double>();
            String line;
            while ((line = reader.readLine()) != null) {
                valuesList.add(Double.parseDouble(line.trim()));
            }
            double[] valuesArr = new double[valuesList.size()];
            for (int i = 0; i < valuesList.size(); i++) {
                valuesArr[i] = valuesList.get(i);
            }
            return valuesArr;
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
    }

    private static void dumpStats(
            int[] funEvalValues,
            double[] minKktValues,
            double[] medianKktValues,
            double[] meanKktValues,
            double[] maxKktValues,
            File outFile) throws FileNotFoundException {
        PrintWriter printer = null;
        try {
            printer = new PrintWriter(outFile);
            for (int i = 0; i < funEvalValues.length; i++) {
                printer.format("%04d %015.7f %015.7f %015.7f %015.7f%n",
                        funEvalValues[i],
                        minKktValues[i],
                        medianKktValues[i],
                        meanKktValues[i],
                        maxKktValues[i]);
            }
        } finally {
            if (printer != null) {
                printer.close();
            }
        }
    }
}
