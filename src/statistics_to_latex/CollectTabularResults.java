/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package statistics_to_latex;

import utils.Mathematics;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JFileChooser;

/**
 *
 * @author Haitham
 */
public class CollectTabularResults {

    private static String initialDir = "D:\\results\\_bi_objective";
    private static JFileChooser fileChooser = new JFileChooser(initialDir);

    public static void main(String[] args) throws IOException {
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fileChooser.setMultiSelectionEnabled(true);
        // Single Objective Code
//        List<EvaluationsResultBlock> singleObjectiveResultBlocksList = new ArrayList<EvaluationsResultBlock>();
//        if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
//            for (File dir : fileChooser.getSelectedFiles()) {
//                // Unconstrained (involving CMA-ES)
//                //EvaluationsResultBlock resultBlock = readSingleObjectiveResultsBlock_with_CMAES(dir);
//                // Constrained (no CMA-ES)
//                EvaluationsResultBlock resultBlock = readSingleObjectiveResultsBlock(dir);
//                singleObjectiveResultBlocksList.add(resultBlock);
//            }
//        }
//        dumpSingleObjectiveLatexTable(singleObjectiveResultBlocksList);
        // Bi-Objectives Code
        List<GenerationsResultBlock> biObjectiveResultBlocksList = new ArrayList<GenerationsResultBlock>();
        if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            for (File dir : fileChooser.getSelectedFiles()) {
                GenerationsResultBlock[] resultsBlocks = readBiObjectiveResultsBlocks(dir);
                for (GenerationsResultBlock block : resultsBlocks) {
                    biObjectiveResultBlocksList.add(block);
                }
            }
        }
        dumpBiObjectiveLatexTable(biObjectiveResultBlocksList);
        // Multi- & Many-Objectives Code
//        List<GenerationsResultsBlockForMultiobjective> multiObjectiveResultBlocksList = new ArrayList<GenerationsResultsBlockForMultiobjective>();
//        if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
//            for (File dir : fileChooser.getSelectedFiles()) {
//                GenerationsResultsBlockForMultiobjective resultBlock = readMultiObjectiveResultsBlock(dir, 3, 11);
//                multiObjectiveResultBlocksList.add(resultBlock);
//            }
//        }
//        dumpMultiObjectiveLatexTable(multiObjectiveResultBlocksList);
    }

    private static double[] readEliteRGAResultsAccrossRuns(File eliteRGARsultsFile) throws IOException {
        List<Double> fitnessValuesList = new ArrayList<Double>();
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(eliteRGARsultsFile));
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.startsWith("Best")) {
                    line = removeAdditionalSpaces(line);
                    String[] splits = line.split(" ");
                    fitnessValuesList.add(Double.parseDouble(splits[3]));
                }
            }
            double[] fitnessValuesArr = new double[fitnessValuesList.size()];
            for (int i = 0; i < fitnessValuesList.size(); i++) {
                fitnessValuesArr[i] = fitnessValuesList.get(i);
            }
            return fitnessValuesArr;
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
    }

    private static EvaluationsResultBlock readSingleObjectiveResultsBlock(File dir) throws IOException {
        double[] bestValues = new double[3];
        double[] medianValues = new double[3];
        double[] worstValues = new double[3];
        // The name of the problem is the name of the directory itself
        String problemName = getSingleProblemName(dir.getName());
        // Specify the file containing the Elite-RGA output
        File eliteRGARsultsFile = new File(dir.getPath() + "/elite_rga/result_elite_preserving.out");
        // Read the population size
        int popSize = readPopSize(eliteRGARsultsFile);
        // Read the generations count from that file (remember when comparing
        // with NSGA-III the generations count will be different but the whole
        // number of function evaluations should be the same. That's why we
        // are using function evaluations for comparison)
        int funcEvalCount = readGenCount(eliteRGARsultsFile) * popSize;
        // Read all the final fitness value of each run
        double[] fitnessValues = readEliteRGAResultsAccrossRuns(eliteRGARsultsFile);
        // Get the best, median and worst fitness values of Elite-RGA results
        double rgaBest = fitnessValues[Mathematics.getMinIndex(fitnessValues)];
        double rgaMedian = fitnessValues[Mathematics.getMedianIndex(fitnessValues)];
        double rgaWorst = fitnessValues[Mathematics.getMaxIndex(fitnessValues)];
        // Specify the file containing the NSGA-III output
        File nsga3RsultsFile = new File(dir.getPath() + "/nsga3/nsga3_across_runs_stats.dat");
        // Read the three values in the last line. These three values represent
        // the best, median and worst fitness values of all the runs.
        String[] splits = removeAdditionalSpaces(tail(nsga3RsultsFile).trim()).split(" ");
        // Remember that the Zero index is the number of function evaluations
        // and we do not need that now, we just needd the three following
        // values.
        double nsga3Best = Double.parseDouble(splits[1]);
        double nsga3Median = Double.parseDouble(splits[2]);
        double nsga3Worst = Double.parseDouble(splits[3]);
        // Specify the file containing the U-NSGA-III results.
        File unsga3RsultsFile = new File(dir.getPath() + "/unified_nsga3/unified_nsga3_across_runs_stats.dat");
        splits = removeAdditionalSpaces(tail(unsga3RsultsFile).trim()).split(" ");
        // Remember that the Zero index is the number of function evaluations
        // and we do not need that now, we just needd the three following
        // values.
        double unsga3Best = Double.parseDouble(splits[1]);
        double unsga3Median = Double.parseDouble(splits[2]);
        double unsga3Worst = Double.parseDouble(splits[3]);
        // Assign the collected statictics to the local arrays
        bestValues[0] = rgaBest;
        bestValues[1] = nsga3Best;
        bestValues[2] = unsga3Best;
        medianValues[0] = rgaMedian;
        medianValues[1] = nsga3Median;
        medianValues[2] = unsga3Median;
        worstValues[0] = rgaWorst;
        worstValues[1] = nsga3Worst;
        worstValues[2] = unsga3Worst;
        // Construct and return one block of results
        return new EvaluationsResultBlock(problemName, funcEvalCount, popSize, bestValues, medianValues, worstValues);
    }

    private static EvaluationsResultBlock readSingleObjectiveResultsBlock_with_CMAES(File dir) throws IOException {
        double[] bestValues = new double[4];
        double[] medianValues = new double[4];
        double[] worstValues = new double[4];
        // The name of the problem is the name of the directory itself
        String problemName = getSingleProblemName(dir.getName());
        // Specify the file containing the Elite-RGA output
        File eliteRGARsultsFile = new File(dir.getPath() + "/elite_rga/result_elite_preserving.out");
        // Read the population size
        int popSize = readPopSize(eliteRGARsultsFile);
        // Read the generations count from that file (remember when comparing
        // with NSGA-III the generations count will be different but the whole
        // number of function evaluations should be the same. That's why we
        // are using function evaluations for comparison)
        int funcEvalCount = readGenCount(eliteRGARsultsFile) * popSize;
        // Read all the final fitness value of each run
        double[] fitnessValues = readEliteRGAResultsAccrossRuns(eliteRGARsultsFile);
        // Get the best, median and worst fitness values of Elite-RGA results
        double rgaBest = fitnessValues[Mathematics.getMinIndex(fitnessValues)];
        double rgaMedian = fitnessValues[Mathematics.getMedianIndex(fitnessValues)];
        double rgaWorst = fitnessValues[Mathematics.getMaxIndex(fitnessValues)];
        // Specify the file containing the NSGA-III output
        File nsga3RsultsFile = new File(dir.getPath() + "/nsga3/nsga3_across_runs_stats.dat");
        // Read the three values in the last line. These three values represent
        // the best, median and worst fitness values of all the runs.
        String[] splits = removeAdditionalSpaces(tail(nsga3RsultsFile).trim()).split(" ");
        // Remember that the Zero index is the number of function evaluations
        // and we do not need that now, we just needd the three following
        // values.
        double nsga3Best = Double.parseDouble(splits[1]);
        double nsga3Median = Double.parseDouble(splits[2]);
        double nsga3Worst = Double.parseDouble(splits[3]);
        // Specify the file containing the U-NSGA-III results.
        File unsga3RsultsFile = new File(dir.getPath() + "/unified_nsga3/unified_nsga3_across_runs_stats.dat");
        splits = removeAdditionalSpaces(tail(unsga3RsultsFile).trim()).split(" ");
        // Remember that the Zero index is the number of function evaluations
        // and we do not need that now, we just needd the three following
        // values.
        double unsga3Best = Double.parseDouble(splits[1]);
        double unsga3Median = Double.parseDouble(splits[2]);
        double unsga3Worst = Double.parseDouble(splits[3]);

        // Specify the file containing the CMA-ES output
        File cmaesRsultsFile = new File(dir.getPath() + "/cmaes/cmaes_across_runs_stats.dat");
        // Read the three values in the last line. These three values represent
        // the best, median and worst fitness values of all the runs.
        splits = removeAdditionalSpaces(tail(cmaesRsultsFile).trim()).split(" ");
        // Remember that the Zero index is the number of function evaluations
        // and we do not need that now, we just needd the three following
        // values.
        double cmaesBest = Double.parseDouble(splits[1]);
        double cmaesMedian = Double.parseDouble(splits[2]);
        double cmaesWorst = Double.parseDouble(splits[3]);

        // Assign the collected statictics to the local arrays
        bestValues[0] = rgaBest;
        bestValues[1] = nsga3Best;
        bestValues[2] = unsga3Best;
        bestValues[3] = cmaesBest;
        medianValues[0] = rgaMedian;
        medianValues[1] = nsga3Median;
        medianValues[2] = unsga3Median;
        medianValues[3] = cmaesMedian;
        worstValues[0] = rgaWorst;
        worstValues[1] = nsga3Worst;
        worstValues[2] = unsga3Worst;
        worstValues[3] = cmaesWorst;
        // Construct and return one block of results
        return new EvaluationsResultBlock(problemName, funcEvalCount, popSize, bestValues, medianValues, worstValues);
    }

    private static GenerationsResultBlock[] readBiObjectiveResultsBlocks(File dir) throws IOException {
        File[] subFilesAndDirs = dir.listFiles();
        List<File> subDirsList = new ArrayList<File>();
        // Rule out sub-files (keep only sub-dirs)
        for (File file : subFilesAndDirs) {
            if (file.isDirectory()) {
                subDirsList.add(file);
            }
        }
        // Sort sub-dirs ascendingly based on their population sizes
        for (int i = 0; i < subDirsList.size() - 1; i++) {
            for (int j = i + 1; j < subDirsList.size(); j++) {
                int iPopSize = getPopSizeFromDirName(subDirsList.get(i).getName());
                int jPopSize = getPopSizeFromDirName(subDirsList.get(j).getName());
                if (jPopSize < iPopSize) {
                    // Swap the positions of the two sub-dirs in the list
                    File temp = subDirsList.get(i);
                    subDirsList.set(i, subDirsList.get(j));
                    subDirsList.set(j, temp);
                }
            }
        }
        // Open a stream to each of the three statistics files
        File bestsFile = new File(dir.getPath() + "/hv_best_stats.dat");
        File mediansFile = new File(dir.getPath() + "/hv_median_stats.dat");
        File worstsFile = new File(dir.getPath() + "/hv_worst_stats.dat");
        List<GenerationsResultBlock> resultsBlocksList = new ArrayList<GenerationsResultBlock>();
        BufferedReader bestsReader = null, mediansReader = null, worstsReader = null;
        try {
            bestsReader = new BufferedReader(new FileReader(bestsFile));
            mediansReader = new BufferedReader(new FileReader(mediansFile));
            worstsReader = new BufferedReader(new FileReader(worstsFile));
            String bestsLine, mediansLine, worstsLine;
            int counter = 0;
            while ((bestsLine = bestsReader.readLine()) != null) {
                mediansLine = mediansReader.readLine();
                worstsLine = worstsReader.readLine();
                bestsLine = removeAdditionalSpaces(bestsLine.trim());
                mediansLine = removeAdditionalSpaces(mediansLine.trim());
                worstsLine = removeAdditionalSpaces(worstsLine.trim());
                // Collect the best of the three algorithms
                double[] bestValues = new double[3];
                bestValues[0] = Double.parseDouble(bestsLine.split(" ")[0]);
                bestValues[1] = Double.parseDouble(bestsLine.split(" ")[1]);
                bestValues[2] = Double.parseDouble(bestsLine.split(" ")[2]);
                // Collect the median of each algorithm
                double[] medianValues = new double[3];
                medianValues[0] = Double.parseDouble(mediansLine.split(" ")[0]);
                medianValues[1] = Double.parseDouble(mediansLine.split(" ")[1]);
                medianValues[2] = Double.parseDouble(mediansLine.split(" ")[2]);
                // Collect the worst of each algorithm
                double[] worstValues = new double[3];
                worstValues[0] = Double.parseDouble(worstsLine.split(" ")[0]);
                worstValues[1] = Double.parseDouble(worstsLine.split(" ")[1]);
                worstValues[2] = Double.parseDouble(worstsLine.split(" ")[2]);
                // Read popSize and genCount of the corresponsing sub-dir
                int popSize = getPopSizeFromDirName(subDirsList.get(counter).getName());
                int genCount = getGenCountFromDirName(subDirsList.get(counter).getName());
                // Create results block
                GenerationsResultBlock resultBlock = new GenerationsResultBlock(getBiProblemName(dir.getName()), genCount, popSize, bestValues, medianValues, worstValues);
                resultsBlocksList.add(resultBlock);
                counter++;
            }
            GenerationsResultBlock[] resultBlocksArr = new GenerationsResultBlock[resultsBlocksList.size()];
            resultsBlocksList.toArray(resultBlocksArr);
            return resultBlocksArr;
        } finally {
            if (bestsReader != null) {
                bestsReader.close();
            }
        }
    }

    private static GenerationsResultsBlockForMultiobjective readMultiObjectiveResultsBlock(File dir, int algorithmsCount, int runsCount) throws IOException {
        File hypervolumesReportFile = new File(dir.getPath() + "/hypervolumes_report.txt");
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(hypervolumesReportFile));
            double[][] hypervolumes = new double[algorithmsCount][runsCount];
            int counter = 0;
            String line;
            while ((line = reader.readLine()) != null) {
                line = removeAllSpaces(line);
                String[] splits = line.split("\\(");
                String lastSplit = splits[1];
                hypervolumes[counter / runsCount][counter % runsCount] = Double.parseDouble(lastSplit.substring(0, lastSplit.length() - 1));
                counter++;
            }
            double[] bestValues = new double[3];
            bestValues[0] = hypervolumes[0][Mathematics.getMaxIndex(hypervolumes[0])];
            bestValues[1] = hypervolumes[1][Mathematics.getMaxIndex(hypervolumes[1])];
            bestValues[2] = hypervolumes[2][Mathematics.getMaxIndex(hypervolumes[2])];
            double[] medianValues = new double[3];
            int medianHvIndex;
            medianHvIndex = Mathematics.getNonNegativesMedianIndex(hypervolumes[0]);
            if (medianHvIndex == -1) {
                medianValues[0] = -1;
            } else {
                medianValues[0] = hypervolumes[0][medianHvIndex];
            }
            medianHvIndex = Mathematics.getNonNegativesMedianIndex(hypervolumes[1]);
            if (medianHvIndex == -1) {
                medianValues[1] = -1;
            } else {
                medianValues[1] = hypervolumes[1][medianHvIndex];
            }
            medianHvIndex = Mathematics.getNonNegativesMedianIndex(hypervolumes[2]);
            if (medianHvIndex == -1) {
                medianValues[2] = -1;
            } else {
                medianValues[2] = hypervolumes[2][medianHvIndex];
            }
            double[] worstValues = new double[3];
            worstValues[0] = hypervolumes[0][Mathematics.getMinIndex(hypervolumes[0])];
            worstValues[1] = hypervolumes[1][Mathematics.getMinIndex(hypervolumes[1])];
            worstValues[2] = hypervolumes[2][Mathematics.getMinIndex(hypervolumes[2])];
            return new GenerationsResultsBlockForMultiobjective(getMultiAndManyProblemName(dir.getName()), getObjCountFromDirName(dir.getName()), getGenCountFromDirName(dir.getName()), getPopSizeFromDirName(dir.getName()), bestValues, medianValues, worstValues);
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
    }

    private static void dumpSingleObjectiveLatexTable(List<EvaluationsResultBlock> resultBlocksList) {
        String accuracy = "5.2";
        String codeStart
                = "\\begin{table*}[!t]\n"
                + "% increase table row spacing, adjust to taste\n"
                + "\\renewcommand{\\arraystretch}{1.3}\n"
                + "\\extrarowheight 1pt\n"
                + "\\caption{Dummy Table Caption}\n"
                + "\\label{tab:dummy_label}\n"
                + "\\centering\n"
                + "\\begin{tabular}{|c|c|c||c|c|c||c|c|c||c|c|c||c|c|c|}\n"
                + "\\hline\n"
                + "% Columns headers\n"
                + "\\multirow{2}{*}{Problem} & \\multirow{2}{*}{Func. Eval.} & \\multirow{2}{*}{P} & \\multicolumn{3}{c||}{EliteRGA} & \\multicolumn{3}{c||}{NSGA-III} & \\multicolumn{3}{c||}{U-NSGA-III} & \\multicolumn{3}{c|}{CMA-ES}\\\\ \\cline{4-15}\n"
                + "              &    &    &  Best & Median & Worst & Best & Median & Worst & Best & Median & Worst & Best & Median & Worst \\\\\n"
                + "\\hline\n";
        StringBuilder resultsBlocksBuilder = new StringBuilder("% Problem results blocks (Problem Name, #Func. Eval., PopSize, best_rga, median_rga, worst_rga, best_nsga3, median_nsga3, worst_nsga3, best_unsga3, median_unsga3, worst_unsga3)\n");
        for (EvaluationsResultBlock resultBlock : resultBlocksList) {
            // Check best values to detrmine which one to boldify
            String[] bestValuesStr = new String[resultBlock.bestValues.length];
            int largestHvIndex = getIndexOfMinFitness(resultBlock.bestValues);
            for (int i = 0; i < resultBlock.bestValues.length; i++) {
                if (i == largestHvIndex) {
                    bestValuesStr[i] = String.format("\\textbf{%" + accuracy + "f}", resultBlock.bestValues[i]);
                } else {
                    bestValuesStr[i] = String.format("%" + accuracy + "f", resultBlock.bestValues[i]);
                }
            }
            // Check median values to detrmine which one to boldify
            String[] medianValuesStr = new String[resultBlock.medianValues.length];
            largestHvIndex = getIndexOfMinFitness(resultBlock.medianValues);
            for (int i = 0; i < resultBlock.medianValues.length; i++) {
                if (i == largestHvIndex) {
                    medianValuesStr[i] = String.format("\\textbf{%" + accuracy + "f}", resultBlock.medianValues[i]);
                } else {
                    medianValuesStr[i] = String.format("%" + accuracy + "f", resultBlock.medianValues[i]);
                }
            }
            // Check worst values to detrmine which one to boldify
            String[] worstValuesStr = new String[resultBlock.worstValues.length];
            largestHvIndex = getIndexOfMinFitness(resultBlock.worstValues);
            for (int i = 0; i < resultBlock.worstValues.length; i++) {
                if (i == largestHvIndex) {
                    worstValuesStr[i] = String.format("\\textbf{%" + accuracy + "f}", resultBlock.worstValues[i]);
                } else {
                    worstValuesStr[i] = String.format("%" + accuracy + "f", resultBlock.worstValues[i]);
                }
            }
            resultsBlocksBuilder.append(String.format("%s & %d & %d & %s & %s & %s & %s & %s & %s & %s & %s & %s & %s & %s & %s \\\\ \\hline %n",
                    resultBlock.getProblemName(),
                    resultBlock.getFuncEvalCount(),
                    resultBlock.getPopSize(),
                    bestValuesStr[0],
                    medianValuesStr[0],
                    worstValuesStr[0],
                    bestValuesStr[1],
                    medianValuesStr[1],
                    worstValuesStr[1],
                    bestValuesStr[2],
                    medianValuesStr[2],
                    worstValuesStr[2],
                    bestValuesStr[3],
                    medianValuesStr[3],
                    worstValuesStr[3]
            ));
        }
        String codeEnd
                = "\\end{tabular}\n"
                + "\\end{table*}";
        System.out.println(codeStart + resultsBlocksBuilder.toString() + codeEnd);
    }

    private static void dumpBiObjectiveLatexTable(List<GenerationsResultBlock> resultBlocksList) {
        String accuracy = "7.5";
        String codeStart
                = "\\begin{table*}[!t]\n"
                + "% increase table row spacing, adjust to taste\n"
                + "\\renewcommand{\\arraystretch}{1.3}\n"
                + "\\extrarowheight 1pt\n"
                + "\\caption{Dummy Table Caption}\n"
                + "\\label{tab:dummy_label}\n"
                + "\\centering\n"
                + "\\begin{tabular}{|c|c|c||c|c|c||c|c|c||c|c|c|}\n"
                + "\\hline\n"
                + "% Columns headers\n"
                + "\\multirow{2}{*}{Problem} & \\multirow{2}{*}{G} & \\multirow{2}{*}{P} & \\multicolumn{3}{c||}{NSGA-II} & \\multicolumn{3}{c||}{NSGA-III} & \\multicolumn{3}{c|}{U-NSGA-III} \\\\ \\cline{4-12}\n"
                + "              &    &    &  Best & Median & Worst & Best & Median & Worst & Best & Median & Worst \\\\\n"
                + "\\hline\n";
        StringBuilder resultsBlocksBuilder = new StringBuilder("% Problem results blocks (Problem Name, genCount, PopSize, best_rga, median_rga, worst_rga, best_nsga3, median_nsga3, worst_nsga3, best_unsga3, median_unsga3, worst_unsga3)\n");
        for (GenerationsResultBlock resultBlock : resultBlocksList) {
            // Check each three best values to detrmine which one to boldify
            String[] bestValuesStr = new String[resultBlock.bestValues.length];
            int largestHvIndex = getIndexOfMaxHv(resultBlock.bestValues);
            for (int i = 0; i < resultBlock.bestValues.length; i++) {
                if (i == largestHvIndex) {
                    bestValuesStr[i] = String.format("\\textbf{%" + accuracy + "f}", resultBlock.bestValues[i]);
                } else {
                    bestValuesStr[i] = String.format("%" + accuracy + "f", resultBlock.bestValues[i]);
                }
            }
            // Check each three median values to detrmine which one to boldify
            String[] medianValuesStr = new String[resultBlock.medianValues.length];
            largestHvIndex = getIndexOfMaxHv(resultBlock.medianValues);
            for (int i = 0; i < resultBlock.medianValues.length; i++) {
                if (i == largestHvIndex) {
                    medianValuesStr[i] = String.format("\\textbf{%" + accuracy + "f}", resultBlock.medianValues[i]);
                } else {
                    medianValuesStr[i] = String.format("%" + accuracy + "f", resultBlock.medianValues[i]);
                }
            }
            // Check each three best values to detrmine which one to boldify
            String[] worstValuesStr = new String[resultBlock.worstValues.length];
            largestHvIndex = getIndexOfMaxHv(resultBlock.worstValues);
            for (int i = 0; i < resultBlock.worstValues.length; i++) {
                if (i == largestHvIndex) {
                    worstValuesStr[i] = String.format("\\textbf{%" + accuracy + "f}", resultBlock.worstValues[i]);
                } else {
                    worstValuesStr[i] = String.format("%" + accuracy + "f", resultBlock.worstValues[i]);
                }
            }
            resultsBlocksBuilder.append(String.format("%s & %d & %d & %s & %s & %s & %s & %s & %s & %s & %s & %s \\\\ \\hline %n",
                    resultBlock.getProblemName(),
                    resultBlock.getGenCount(),
                    resultBlock.getPopSize(),
                    bestValuesStr[0],
                    medianValuesStr[0],
                    worstValuesStr[0],
                    bestValuesStr[1],
                    medianValuesStr[1],
                    worstValuesStr[1],
                    bestValuesStr[2],
                    medianValuesStr[2],
                    worstValuesStr[2]));
        }
        String codeEnd
                = "\\end{tabular}\n"
                + "\\end{table*}";
        System.out.println(codeStart + resultsBlocksBuilder.toString() + codeEnd);
    }

    private static void dumpMultiObjectiveLatexTable(List<GenerationsResultsBlockForMultiobjective> resultBlocksList) {
        boolean useDecimalFormatter = true;
        DecimalFormat formatter = new DecimalFormat("0.###E0");
        String accuracy = "7.5";
        String codeStart
                = "\\begin{table*}[!t]\n"
                + "% increase table row spacing, adjust to taste\n"
                + "\\renewcommand{\\arraystretch}{1.3}\n"
                + "\\extrarowheight 1pt\n"
                + "\\caption{Dummy Table Caption}\n"
                + "\\label{tab:dummy_label}\n"
                + "\\centering\n"
                + "\\begin{tabular}{|c|c|c|c||c|c|c||c|c|c||c|c|c|}\n"
                + "\\hline\n"
                + "% Columns headers\n"
                + "\\multirow{2}{*}{Problem} & \\multirow{2}{*}{Obj. }& \\multirow{2}{*}{G} & \\multirow{2}{*}{P} & \\multicolumn{3}{c||}{NSGA-II} & \\multicolumn{3}{c||}{NSGA-III} & \\multicolumn{3}{c|}{U-NSGA-III} \\\\ \\cline{5-13}\n"
                + "              &    &    &    &  Best & Median & Worst & Best & Median & Worst & Best & Median & Worst \\\\\n"
                + "\\hline\n";
        StringBuilder resultsBlocksBuilder = new StringBuilder("% Problem results blocks (Problem Name, objCount, PopSize, best_nsga2, median_nsga2 worst_nsga2, best_nsga3, median_nsga3, worst_nsga3, best_unsga3, median_unsga3, worst_unsga3)\n");
        for (GenerationsResultsBlockForMultiobjective resultBlock : resultBlocksList) {
            // Check each three best values to detrmine which one to boldify
            String[] bestValuesStr = new String[resultBlock.bestValues.length];
            int largestHvIndex = getIndexOfMaxHv(resultBlock.bestValues);
            for (int i = 0; i < resultBlock.bestValues.length; i++) {
                if (useDecimalFormatter) {
                    if (i == largestHvIndex) {
                        bestValuesStr[i] = String.format("\\textbf{%s}", formatter.format(resultBlock.bestValues[i]));
                    } else {
                        bestValuesStr[i] = String.format("%s", formatter.format(resultBlock.bestValues[i]));
                    }
                } else {
                    if (i == largestHvIndex) {
                        bestValuesStr[i] = String.format("\\textbf{%" + accuracy + "f}", resultBlock.bestValues[i]);
                    } else {
                        bestValuesStr[i] = String.format("%" + accuracy + "f", resultBlock.bestValues[i]);
                    }
                }
            }
            // Check each three median values to detrmine which one to boldify
            String[] medianValuesStr = new String[resultBlock.medianValues.length];
            largestHvIndex = getIndexOfMaxHv(resultBlock.medianValues);
            for (int i = 0; i < resultBlock.medianValues.length; i++) {
                if (useDecimalFormatter) {
                    if (i == largestHvIndex) {
                        medianValuesStr[i] = String.format("\\textbf{%s}", formatter.format(resultBlock.medianValues[i]));
                    } else {
                        medianValuesStr[i] = String.format("%s", formatter.format(resultBlock.medianValues[i]));
                    }
                } else {
                    if (i == largestHvIndex) {
                        medianValuesStr[i] = String.format("\\textbf{%" + accuracy + "f}", resultBlock.medianValues[i]);
                    } else {
                        medianValuesStr[i] = String.format("%" + accuracy + "f", resultBlock.medianValues[i]);
                    }
                }
            }
            // Check each three best values to detrmine which one to boldify
            String[] worstValuesStr = new String[resultBlock.worstValues.length];
            largestHvIndex = getIndexOfMaxHv(resultBlock.worstValues);
            for (int i = 0; i < resultBlock.worstValues.length; i++) {
                if (useDecimalFormatter) {
                    if (i == largestHvIndex) {
                        worstValuesStr[i] = String.format("\\textbf{%s}", formatter.format(resultBlock.worstValues[i]));
                    } else {
                        worstValuesStr[i] = String.format("%s", formatter.format(resultBlock.worstValues[i]));
                    }
                } else {
                    if (i == largestHvIndex) {
                        worstValuesStr[i] = String.format("\\textbf{%" + accuracy + "f}", resultBlock.worstValues[i]);
                    } else {
                        worstValuesStr[i] = String.format("%" + accuracy + "f", resultBlock.worstValues[i]);
                    }
                }
            }
            resultsBlocksBuilder.append(String.format("%s & %d & %d & %d & %s & %s & %s & %s & %s & %s & %s & %s & %s \\\\ \\hline %n",
                    resultBlock.getProblemName(),
                    resultBlock.getObjCount(),
                    resultBlock.getGenCount(),
                    resultBlock.getPopSize(),
                    bestValuesStr[0],
                    medianValuesStr[0],
                    worstValuesStr[0],
                    bestValuesStr[1],
                    medianValuesStr[1],
                    worstValuesStr[1],
                    bestValuesStr[2],
                    medianValuesStr[2],
                    worstValuesStr[2]));
        }
        String codeEnd
                = "\\end{tabular}\n"
                + "\\end{table*}";
        System.out.println(codeStart + resultsBlocksBuilder.toString() + codeEnd);
    }

    private static int getObjCountFromDirName(String dirName) {
        String firstSplit = dirName.split("_")[0];
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < firstSplit.length(); i++) {
            if (Character.isDigit(firstSplit.charAt(i))) {
                sb.append(firstSplit.charAt(i));
            } else {
                break;
            }
        }
        return Integer.parseInt(sb.toString());
    }

    private static int getGenCountFromDirName(String dirName) {
        String[] splits = dirName.split("-");
        for (String split : splits) {
            if (split.startsWith("G")) {
                return Integer.parseInt(split.substring(1));
            }
        }
        throw new IllegalArgumentException("Invalid File Format");
    }

    private static int getPopSizeFromDirName(String dirName) {
        String[] splits = dirName.split("-");
        for (String split : splits) {
            if (split.startsWith("P")) {
                return Integer.parseInt(split.substring(1));
            }
        }
        throw new IllegalArgumentException("Invalid File Format");
    }

    private static int getIndexOfMaxHv(double[] values) {
        return Mathematics.getMaxIndex(values);
    }

    private static int getIndexOfMinFitness(double[] values) {
        return Mathematics.getMinIndex(values);
    }

    private static String removeAllSpaces(String line) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < line.length(); i++) {
            if (!Character.isWhitespace(line.charAt(i))) {
                sb.append(line.charAt(i));
            }
        }
        return sb.toString();
    }

    public static String tail(File file) {
        RandomAccessFile fileHandler = null;
        try {
            fileHandler = new RandomAccessFile(file, "r");
            long fileLength = fileHandler.length() - 1;
            StringBuilder sb = new StringBuilder();

            for (long filePointer = fileLength; filePointer != -1; filePointer--) {
                fileHandler.seek(filePointer);
                int readByte = fileHandler.readByte();

                if (readByte == 0xA) {
                    if (filePointer == fileLength) {
                        continue;
                    }
                    break;

                } else if (readByte == 0xD) {
                    if (filePointer == fileLength - 1) {
                        continue;
                    }
                    break;
                }

                sb.append((char) readByte);
            }

            String lastLine = sb.reverse().toString();
            return lastLine;
        } catch (java.io.FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (java.io.IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            if (fileHandler != null) {
                try {
                    fileHandler.close();
                } catch (IOException e) {
                    /* ignore */
                }
            }
        }
    }

    private static String removeAdditionalSpaces(String line) {
        line = line.trim();
        StringBuilder sb = new StringBuilder();
        boolean spaceMet = false;
        for (int i = 0; i < line.length(); i++) {
            if (line.charAt(i) == ' ') {
                if (!spaceMet) {
                    sb.append(line.charAt(i));
                    spaceMet = true;
                }
            } else {
                spaceMet = false;
                sb.append(line.charAt(i));
            }
        }
        return sb.toString();
    }

    private static int readGenCount(File eliteRGARsultsFile) throws IOException {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(eliteRGARsultsFile));
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.startsWith("Total")) {
                    line = removeAdditionalSpaces(line);
                    String[] splits = line.split(" ");
                    return Integer.parseInt(splits[splits.length - 1]);
                }
            }
            // If execution reached this point this means that the format
            // of the file is invalid.
            throw new IllegalArgumentException("Invalid file format");
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
    }

    private static int readPopSize(File eliteRGARsultsFile) throws IOException {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(eliteRGARsultsFile));
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.startsWith("Population")) {
                    line = removeAdditionalSpaces(line);
                    String[] splits = line.split(" ");
                    return Integer.parseInt(splits[splits.length - 1]);
                }
            }
            // If execution reached this point this means that the format
            // of the file is invalid.
            throw new IllegalArgumentException("Invalid file format");
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
    }

    private static String getSingleProblemName(String name) {
        return name.split("-")[2].toUpperCase();
    }

    private static String getBiProblemName(String name) {
        return name.split("_")[0].toUpperCase();
    }

    private static String getMultiAndManyProblemName(String dirName) {
        String[] splits = dirName.split("-");
        StringBuilder problemNameBuilder = new StringBuilder();
        for (int i = 0; i < splits.length; i++) {
            try {
                Integer.parseInt(splits[i]);
                break;
            } catch (NumberFormatException ex) {
                if (!problemNameBuilder.toString().equals("")) {
                    problemNameBuilder.append(" ");
                }
                problemNameBuilder.append(splits[i].toUpperCase());
            }
        }
        StringBuilder finalProblemNameBuilder = new StringBuilder();
        splits = problemNameBuilder.toString().split("_");
        for (int i = 0; i < splits.length; i++) {
            if (!splits[i].toLowerCase().contains("obj")) {
                if (!finalProblemNameBuilder.toString().equals("")) {
                    finalProblemNameBuilder.append(" ");
                }
                finalProblemNameBuilder.append(splits[i]);
            }
        }
        return finalProblemNameBuilder.toString();
    }
}
