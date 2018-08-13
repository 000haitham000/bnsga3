/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package optimization;

import asf.ASF_Minimizer;
import asf.BNH_ASF_Minimizer;
import asf.DTLZ1_ASF_Minimizer;
import asf.DTLZ2_ASF_Minimizer;
import asf.OSY_ASF_Minimizer;
import asf.SRN_ASF_Minimizer;
import asf.TNK_ASF_Minimizer;
import asf.ZDT4_ASF_Minimizer;
import asf.ZDT6_ASF_Minimizer;
import com.mathworks.toolbox.javabuilder.MWException;
import emo.DoubleAssignmentException;
import emo.Individual;
import emo.OptimizationProblem;
import emo.VirtualIndividual;
import engines.AbstractGeneticEngine;
import engines.NSGA3_DC_EMO2017;
import engines.NSGA3Engine;
import evaluators.BNHEvaluator;
import evaluators.GeneralDTLZ1Evaluator;
import evaluators.GeneralDTLZ2Evaluator;
import evaluators.OSYEvaluator;
import evaluators.SRNEvaluator;
import evaluators.TNKEvaluator;
import evaluators.ZDT4Evaluator;
import evaluators.ZDT6Evaluator;
import extremels.BnhExtremeLocalSearch;
import extremels.Dtlz1ExtremeLocalSearch;
import extremels.Dtlz2ExtremeLocalSearch;
import extremels.ExtremeLocalSearch;
import extremels.OsyExtremeLocalSearch;
import extremels.SrnExtremeLocalSearch;
import extremels.TnkExtremeLocalSearch;
import extremels.Zdt4ExtremeLocalSearch;
import extremels.Zdt6ExtremeLocalSearch;
import haitham.Utilities;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.stream.XMLStreamException;
import jmetal.core.SolutionSet;
import jmetal.qualityIndicator.Hypervolume;
import jmetal.qualityIndicator.fastHypervolume.wfg.WFGHV;
import kkt.KKT_Calculator;
import kkt.xmlbased.BnhKKTPMCalculator;
import kkt.xmlbased.Dtlz1KKTPMCalculator_05obj;
import kkt.xmlbased.Dtlz2KKTPMCalculator_05obj;
import kkt.xmlbased.OsyKKTPMCalculator;
import kkt.xmlbased.SrnKKTPMCalculator;
import kkt.xmlbased.TnkKKTPMCalculator;
import kkt.xmlbased.Zdt4KKTPMCalculator;
import kkt.xmlbased.Zdt6KKTPMCalculator;
import moeaframework.moead.GenericProblem;
import moeaframework.moead.ReportingExecutor;
import org.moeaframework.Executor;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.Solution;
import org.moeaframework.core.variable.EncodingUtils;
import parsing.IndividualEvaluator;
import parsing.InvalidOptimizationProblemException;
import parsing.StaXParser;
import utils.Mathematics;
import utils.PerformanceMetrics;
import utils.RandomNumberGenerator;

/**
 *
 * @author Haitham
 */
public class TestScript_EMO2017 {

    public static PrintWriter printer = null;

    // Ideal points
    static final double[] BNH_IDEAL_POINT = new double[]{5.577718464709733E-5, 4.000134245207236};
    static final double[] OSY_IDEAL_POINT = new double[]{-273.9999885325946, 4.010756522628899};
    static final double[] SRN_IDEAL_POINT = new double[]{10.10117140168606, -217.73837054184486};
    static final double[] TNK_IDEAL_POINT = new double[]{0.041664126903726846, 0.04166412690372694};
    static final double[] ZDT4_IDEAL_POINT = new double[]{0.0, 0.0};
    static final double[] ZDT6_IDEAL_POINT = new double[]{0.0, 0.0};
    static final double[] DTLZ1_5OBJ_IDEAL_POINT = new double[]{0.0, 0.0, 0.0, 0.0, 0.0};
    static final double[] DTLZ2_5OBJ_IDEAL_POINT = new double[]{0.0, 0.0, 0.0, 0.0, 0.0};
    // HV Reference points
    static final double[] BNH_REF_POINT = new double[]{135.5510091212441, 4.000134245207236};
    static final double[] OSY_REF_POINT = new double[]{-45.65999303898395, 75.99998952782383};
    static final double[] SRN_REF_POINT = new double[]{222.8448578266786, 2.376103226277362};
    static final double[] TNK_REF_POINT = new double[]{1.0384498374343492, 1.0384498374343492};
    static final double[] ZDT4_REF_POINT = new double[]{1.0, 1.0};
    static final double[] ZDT6_REF_POINT = new double[]{1.0, 1.0};
    static final double[] DTLZ1_5OBJ_REF_POINT = new double[]{0.5, 0.5, 0.5, 0.5, 0.5};
    static final double[] DTLZ2_5OBJ_REF_POINT = new double[]{1.0, 1.0, 1.0, 1.0, 1.0};
    // Reference Pareto Front File
    static final File BNH_REF_SET_FILE = new File("E:/POST-GECCO-2016/Results/ref_sets/bnh_obj_refset_1000.dat");
    static final File OSY_REF_SET_FILE = new File("E:/POST-GECCO-2016/Results/ref_sets/osy_obj_refset_1000.dat");
    static final File SRN_REF_SET_FILE = new File("E:/POST-GECCO-2016/Results/ref_sets/srn_obj_refset_1000.dat");
    static final File TNK_REF_SET_FILE = new File("E:/POST-GECCO-2016/Results/ref_sets/tnk_obj_refset_1000.dat");
    static final File ZDT4_REF_SET_FILE = new File("E:/POST-GECCO-2016/Results/ref_sets/zdt4_obj_refset_1000.dat");
    static final File ZDT6_REF_SET_FILE = new File("E:/POST-GECCO-2016/Results/ref_sets/zdt6_obj_refset_0999.dat");
    static final File DTLZ1_5OBJ_REF_SET_FILE = new File("E:/POST-GECCO-2016/Results/ref_sets/dtlz1_obj_refset_0128.dat");
    static final File DTLZ2_5OBJ_REF_SET_FILE = new File("E:/POST-GECCO-2016/Results/ref_sets/dtlz2_obj_refset_0128.dat");

    // Problem Gefinition
    static URL url;
    static OptimizationProblem optimizationProblem;

    static IndividualEvaluator individualEvaluator;
    static ASF_Minimizer asfMinimizer;
    static KKT_Calculator kktCalculator;
    static ExtremeLocalSearch extLS;
    // Number of runs
    static int runCount;
    static double seed;
    // General Parameters
    static int popSize; // Population size
    static int emo2017GenCount; // Generations count
    static int etaC; // SBX distribution index
    static int etaM; // PM distribution index
    // Epsilon-Dominance Parameter
    static double epsilon;
    // Output Directory
    static File topOutputDir;
    // Ideal Point
    static double[] idealPoint;
    // Reference Point
    static double[] refPoint;
    // Reference Set File
    static File refSetFile;

//    static {
//        try {
//
//            printer = new PrintWriter(new File("F:\\IEEE-TEVC-DC-NSGA-III\\Results\\phases.txt"));
//
//            // Problem Definition
//            url = NSGA3Engine.class.getResource("../samples/zdt6-02-10.xml");     // Modify Here
//            optimizationProblem = StaXParser.readProblem(url.openStream());
//            individualEvaluator = new ZDT6Evaluator();                      // Modify Here
//            idealPoint = ZDT6_IDEAL_POINT;                                  // Modify Here
//            refPoint = ZDT6_REF_POINT;                                      // Modify Here
//            refSetFile = ZDT6_REF_SET_FILE;                                 // Modify Here
//            asfMinimizer = new ZDT6_ASF_Minimizer();                        // Modify Here
//            kktCalculator = new Zdt6KKTPMCalculator();                      // Modify Here
//            extLS = new Zdt6ExtremeLocalSearch();                           // Modify Here
//            // Number of runs
//            runCount = 1;                                                  // Modify Here
//            seed = 0.7;
//            // General Parameters
//            popSize = 4; // Population size                               // Modify Here
//            emo2017GenCount = 100; // Generations count // Modify Here
//            etaC = 30; // SBX distribution index
//            etaM = 20; // PM distribution index
//            // Epsilon-Dominance Parameter
//            epsilon = 0.0;
//            // Output Directory
//            topOutputDir = new File("E:\\POST-GECCO-2016\\Results");
//        } catch (IOException ex) {
//            Logger.getLogger(TestScript_EMO2017.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (XMLStreamException ex) {
//            Logger.getLogger(TestScript_EMO2017.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (InvalidOptimizationProblemException ex) {
//            Logger.getLogger(TestScript_EMO2017.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (MWException ex) {
//            Logger.getLogger(TestScript_EMO2017.class.getName()).log(Level.SEVERE, null, ex);
//        }
//    }

    private static AbstractGeneticEngine nsga3Engine;
    private static AbstractGeneticEngine emo2017Engine;

    public static void main(String[] args) throws MWException, IOException, XMLStreamException, InvalidOptimizationProblemException, FileNotFoundException, DoubleAssignmentException {
        if (optimizationProblem.objectives.length == 2) {
            optimizationProblem.setSteps(popSize - 1);
        }
        // Set parameters
        optimizationProblem.setPopulationSize(popSize);
        optimizationProblem.setRealCrossoverDistIndex(etaC);
        optimizationProblem.setRealMutationDistIndex(etaM);
        // Test NSGA3_DC_EMO2017 Engine
        int emo2017TotalFunEval = testEmo2017(optimizationProblem);
        // Test NSGA-III Engine
        testNSGA3(optimizationProblem, emo2017TotalFunEval);
        // Calculate the Hypervolume of All runs and dump the min, median and maximum
        System.out.println();
        System.out.println("* NSGA-III");
        dumpStats(optimizationProblem, nsga3Engine);
        System.out.println();
        System.out.println("* EMO-2017");
        dumpStats(optimizationProblem, emo2017Engine);
        // Close phases printer
        printer.close();
    }

    private static void testNSGA3(OptimizationProblem optimizationProblem, int emo2017TotalFunEval) throws DoubleAssignmentException, MWException, IOException {
        // Test NSGA-III Engine
        optimizationProblem.setGenerationsCount(Integer.MAX_VALUE);
        nsga3Engine = new NSGA3Engine(optimizationProblem, individualEvaluator);
        nsga3Engine.DUMP_ALL_GENERATIONS_DECISION_SPACE = true;
        nsga3Engine.DUMP_ALL_GENERATIONS_KKTPM = true;
        nsga3Engine.DUMP_ALL_GENERATIONS_MATLAB_SCRIPTS = true;
        nsga3Engine.DUMP_ALL_GENERATIONS_META_DATA = true;
        nsga3Engine.DUMP_ALL_GENERATIONS_OBJECTIVE_SPACE = true;
        RandomNumberGenerator.setSeed(seed);
        for (int runIndex = 0; runIndex < runCount; runIndex++) {
            System.out.println();
            System.out.println(String.format("Run(%02d)", runIndex));
            Individual[] nsga3FinalPop = nsga3Engine.start(getRunOutputDir(getEngineOutputDir(optimizationProblem, nsga3Engine.getAlgorithmName()), runIndex),
                    runIndex,
                    epsilon,
                    null,
                    null,
                    Double.MAX_VALUE,
                    emo2017TotalFunEval / runCount);
            // Reset the number of function evaluations to start
            // counting from Zero again in the next iteration
            individualEvaluator.resetFunctionEvaluationsCount();
        }
    }

    private static int testEmo2017(OptimizationProblem optimizationProblem) throws DoubleAssignmentException, MWException, IOException {
        // Test NSGA3_DC_EMO2017 Engine
        optimizationProblem.setGenerationsCount(emo2017GenCount);
        emo2017Engine = new NSGA3_DC_EMO2017(optimizationProblem, individualEvaluator, extLS);
        emo2017Engine.DUMP_ALL_GENERATIONS_DECISION_SPACE = true;
        emo2017Engine.DUMP_ALL_GENERATIONS_KKTPM = true;
        emo2017Engine.DUMP_ALL_GENERATIONS_MATLAB_SCRIPTS = true;
        emo2017Engine.DUMP_ALL_GENERATIONS_META_DATA = true;
        emo2017Engine.DUMP_ALL_GENERATIONS_OBJECTIVE_SPACE = true;
        RandomNumberGenerator.setSeed(seed);
        int emo2017TotalFunEval = 0;
        for (int runIndex = 0; runIndex < runCount; runIndex++) {
            System.out.println();
            System.out.println(String.format("Run(%02d)", runIndex));
            Individual[] emo2017FinalPop = emo2017Engine.start(
                    getRunOutputDir(getEngineOutputDir(optimizationProblem, emo2017Engine.getAlgorithmName()), runIndex),
                    runIndex,
                    epsilon,
                    asfMinimizer,
                    kktCalculator,
                    Double.MAX_VALUE,
                    Integer.MAX_VALUE);
            emo2017TotalFunEval += individualEvaluator.getFunctionEvaluationsCount();
            // Reset the number of function evaluations to start
            // counting from Zero again in the next iteration
            individualEvaluator.resetFunctionEvaluationsCount();
        }
        return emo2017TotalFunEval;
    }

    public static File getRunOutputDir(File problemOutputDir, int runIndex) {
        File runOutputDir = new File(problemOutputDir.getPath() + File.separator + String.format("run%03d/", runIndex));
        if (!runOutputDir.exists()) {
            runOutputDir.mkdir();
        }
        return runOutputDir;
    }

    public static File getEngineOutputDir(OptimizationProblem optimizationProblem, String algorithmName) {
        File outputDir = new File(topOutputDir + File.separator + String.format("%s-%03d-%03d-P%04d/%s/",
                optimizationProblem.getProblemID(),
                optimizationProblem.getRealCrossoverDistIndex(),
                optimizationProblem.getRealMutationDistIndex(),
                optimizationProblem.getPopulationSize(),
                algorithmName));
        // Make directories
        if (!outputDir.exists()) {
            outputDir.mkdirs();
        }
        return outputDir;
    }

    private static void dumpStats(OptimizationProblem optimizationProblem, AbstractGeneticEngine engine) throws IOException {
        double[] allRunHV = new double[runCount];
        for (int runIndex = 0; runIndex < runCount; runIndex++) {
            File runOutputDir = getRunOutputDir(getEngineOutputDir(optimizationProblem, engine.getAlgorithmName()), runIndex);
            File[] files = runOutputDir.listFiles(new FileNamePrefixFilter("obj"));
            Arrays.sort(files);
            File lastFile = files[files.length - 1];
            allRunHV[runIndex] = calculateHV(lastFile, idealPoint, getAdjustedRefPoint(refPoint));
            // Print progress
            System.out.print(String.format("\tCollecting HV: Run %-3d: %7.5f%n", runIndex, allRunHV[runIndex]));
        }
        System.out.println();
        // Collect the indices of worst, median and best runs
        int worstRunIndex = Mathematics.getMinIndex(allRunHV);
        int medianRunIndex = Mathematics.getMedianIndex(allRunHV);
        int bestRunIndex = Mathematics.getMaxIndex(allRunHV);
        System.out.println(String.format("\tWorst = %d, Median = %d and best = %d%n", worstRunIndex, medianRunIndex, bestRunIndex));
        // Get the directories coressponding to worst, median and best runs
        File worstRunOutputDir = getRunOutputDir(getEngineOutputDir(optimizationProblem, engine.getAlgorithmName()), worstRunIndex);
        File medianRunOutputDir = getRunOutputDir(getEngineOutputDir(optimizationProblem, engine.getAlgorithmName()), medianRunIndex);
        File bestRunOutputDir = getRunOutputDir(getEngineOutputDir(optimizationProblem, engine.getAlgorithmName()), bestRunIndex);
        // HV
        System.out.println("Hypervolume Calculations...");
        // Create an output file for worst, median and best runs HV stats
        File hvWorstRunOutFile = new File(getEngineOutputDir(optimizationProblem, engine.getAlgorithmName()) + File.separator + "hv_worst.txt");
        File hvMedianRunOutFile = new File(getEngineOutputDir(optimizationProblem, engine.getAlgorithmName()) + File.separator + "hv_median.txt");
        File hvBestRunOutFile = new File(getEngineOutputDir(optimizationProblem, engine.getAlgorithmName()) + File.separator + "hv_best.txt");
        // Dump FE vs. HV stats for worst, median and best runs
        collectFEvsHV(worstRunOutputDir, hvWorstRunOutFile);
        collectFEvsHV(medianRunOutputDir, hvMedianRunOutFile);
        collectFEvsHV(bestRunOutputDir, hvBestRunOutFile);
        // GD
        System.out.println("Generational Distance Calculations...");
        // Create an output file for worst, median and best runs GD stats
        File gdWorstRunOutFile = new File(getEngineOutputDir(optimizationProblem, engine.getAlgorithmName()) + File.separator + "gd_worst.txt");
        File gdMedianRunOutFile = new File(getEngineOutputDir(optimizationProblem, engine.getAlgorithmName()) + File.separator + "gd_median.txt");
        File gdBestRunOutFile = new File(getEngineOutputDir(optimizationProblem, engine.getAlgorithmName()) + File.separator + "gd_best.txt");
        // Dump FE vs. HV stats for worst, median and best runs
        collectFEvsGD(worstRunOutputDir, refSetFile, gdWorstRunOutFile, optimizationProblem.objectives.length);
        collectFEvsGD(medianRunOutputDir, refSetFile, gdMedianRunOutFile, optimizationProblem.objectives.length);
        collectFEvsGD(bestRunOutputDir, refSetFile, gdBestRunOutFile, optimizationProblem.objectives.length);
        // IGD
        System.out.println("Inverted Generational Distance Calculations...");
        // Create an output file for worst, median and best runs GD stats
        File igdWorstRunOutFile = new File(getEngineOutputDir(optimizationProblem, engine.getAlgorithmName()) + File.separator + "igd_worst.txt");
        File igdMedianRunOutFile = new File(getEngineOutputDir(optimizationProblem, engine.getAlgorithmName()) + File.separator + "igd_median.txt");
        File igdBestRunOutFile = new File(getEngineOutputDir(optimizationProblem, engine.getAlgorithmName()) + File.separator + "igd_best.txt");
        // Dump FE vs. HV stats for worst, median and best runs
        collectFEvsIGD(worstRunOutputDir, refSetFile, igdWorstRunOutFile, optimizationProblem.objectives.length);
        collectFEvsIGD(medianRunOutputDir, refSetFile, igdMedianRunOutFile, optimizationProblem.objectives.length);
        collectFEvsIGD(bestRunOutputDir, refSetFile, igdBestRunOutFile, optimizationProblem.objectives.length);
        // Extreme Points Stagnation Point
        System.out.println("Stagnation FE Search...");
        File stagnationFeFile = new File(getEngineOutputDir(optimizationProblem, engine.getAlgorithmName()) + File.separator + "stagnation_fe.txt");
        int stagnationGenIndex = dumpStagnationFE(medianRunOutputDir, stagnationFeFile, optimizationProblem.objectives.length);
        // Dump additional info
        File additionalOutFile = new File(getEngineOutputDir(optimizationProblem, engine.getAlgorithmName()) + File.separator + "additional_info.txt");
        dumpAdditionalInfo(worstRunIndex, medianRunIndex, bestRunIndex, stagnationGenIndex, additionalOutFile);
    }

    private static void collectFEvsHV(File runOutputDir, File outFile) throws IOException {
        File[] objFiles = runOutputDir.listFiles(new FileNamePrefixFilter("obj"));
        File[] metaFiles = runOutputDir.listFiles(new FileNamePrefixFilter("meta"));
        double[] allGenHV = new double[objFiles.length];
        int[] allGenFE = new int[objFiles.length];
        for (int i = 0; i < objFiles.length; i++) {
            allGenHV[i] = calculateHV(objFiles[i], idealPoint, getAdjustedRefPoint(refPoint));
            allGenFE[i] = getFE(metaFiles[i]);
        }
        try (PrintWriter printer = new PrintWriter(outFile)) {
            for (int i = 0; i < allGenFE.length; i++) {
                printer.println(String.format("%010d %10.8f", allGenFE[i], allGenHV[i]));
            }
        }
    }

    private static double calculateHV(File popFile, double[] minValues, double[] maxValues) throws IOException {
        double hv;
        if (maxValues.length <= 5) {
            // If you have 5 or less objectives calculate the exact
            // hypervolume using Zitzler's algorithm
            SolutionSet population = Utilities.haithamReadNonDominatedSolutionSet(popFile.getPath());
            hv = new Hypervolume().haithamHypervolume(
                    population.writeObjectivesToMatrix(),
                    minValues,
                    maxValues,
                    minValues.length);
        } else {
            // Otherwise, use the WFG hypervolume algorithm
            hv = WFGHV.haithamHypervolume(popFile.getPath(), maxValues);
        }
        return hv;
    }

    private static double calculateGD(File popFile, File refSetFile, int objCount) throws IOException {
        // Create Empty list
        List<VirtualIndividual> popList = new ArrayList<>();
        List<VirtualIndividual> refSetList = new ArrayList<>();
        // Retrieve population (objective space)
        try (BufferedReader reader = new BufferedReader(new FileReader(popFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] splits = line.split(" ");
                VirtualIndividual virtualIndividual = new VirtualIndividual(objCount);
                for (int i = 0; i < splits.length; i++) {
                    virtualIndividual.setObjective(i, Double.parseDouble(splits[i]));
                }
                popList.add(virtualIndividual);
            }
        }
        VirtualIndividual[] pop = new VirtualIndividual[popList.size()];
        popList.toArray(pop);
        // Retrieve Pareto front (objective space)
        try (BufferedReader reader = new BufferedReader(new FileReader(refSetFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] splits = line.split(" ");
                VirtualIndividual virtualIndividual = new VirtualIndividual(objCount);
                for (int i = 0; i < splits.length; i++) {
                    virtualIndividual.setObjective(i, Double.parseDouble(splits[i]));
                }
                refSetList.add(virtualIndividual);
            }
        }
        VirtualIndividual[] refSet = new VirtualIndividual[refSetList.size()];
        refSetList.toArray(refSet);
        // Calculate and return GD
        return PerformanceMetrics.calculateGenerationalDistance(objCount, pop, refSet, 2);
    }

    private static double calculateIGD(File popFile, File refSetFile, int objCount) throws IOException {
        // Create Empty list
        List<VirtualIndividual> popList = new ArrayList<>();
        List<VirtualIndividual> refSetList = new ArrayList<>();
        // Retrieve population (objective space)
        try (BufferedReader reader = new BufferedReader(new FileReader(popFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] splits = line.split(" ");
                VirtualIndividual virtualIndividual = new VirtualIndividual(objCount);
                for (int i = 0; i < splits.length; i++) {
                    virtualIndividual.setObjective(i, Double.parseDouble(splits[i]));
                }
                popList.add(virtualIndividual);
            }
        }
        VirtualIndividual[] pop = new VirtualIndividual[popList.size()];
        popList.toArray(pop);
        // Retrieve Pareto front (objective space)
        try (BufferedReader reader = new BufferedReader(new FileReader(refSetFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] splits = line.split(" ");
                VirtualIndividual virtualIndividual = new VirtualIndividual(objCount);
                for (int i = 0; i < splits.length; i++) {
                    virtualIndividual.setObjective(i, Double.parseDouble(splits[i]));
                }
                refSetList.add(virtualIndividual);
            }
        }
        VirtualIndividual[] refSet = new VirtualIndividual[refSetList.size()];
        refSetList.toArray(refSet);
        // Calculate and return GD
        return PerformanceMetrics.calculateInvertedGenerationalDistance(objCount, pop, refSet, 2);
    }

    private static double[] getAdjustedRefPoint(double[] refPoint) {
        double[] adjustedRefPoint = new double[refPoint.length];
        for (int i = 0; i < refPoint.length; i++) {
            if (refPoint[i] > 0) {
                adjustedRefPoint[i] = refPoint[i] * 1.01;
            } else {
                adjustedRefPoint[i] = refPoint[i] * 0.99;
            }
        }
        return adjustedRefPoint;
    }

    private static int getFE(File metaDataFile) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(metaDataFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("fun_eval")) {
                    String[] splits = line.split("=");
                    return Integer.parseInt(splits[1].trim());
                }
            }
            throw new IllegalArgumentException(String.format("The meta data file \"%s\" does not have an entry of the form \"fun_eval = num\".", metaDataFile.getPath()));
        }
    }

    private static void collectFEvsGD(File runOutputDir, File refSetFile, File outFile, int objCount) throws IOException {
        File[] objFiles = runOutputDir.listFiles(new FileNamePrefixFilter("obj"));
        File[] metaFiles = runOutputDir.listFiles(new FileNamePrefixFilter("meta"));
        double[] allGenGD = new double[objFiles.length];
        int[] allGenFE = new int[objFiles.length];
        for (int i = 0; i < objFiles.length; i++) {
            allGenGD[i] = calculateGD(objFiles[i], refSetFile, objCount);
            allGenFE[i] = getFE(metaFiles[i]);
        }
        try (PrintWriter printer = new PrintWriter(outFile)) {
            for (int i = 0; i < allGenFE.length; i++) {
                printer.println(String.format("%010d %10.8f", allGenFE[i], allGenGD[i]));
            }
        }
    }

    private static void collectFEvsIGD(File runOutputDir, File refSetFile, File outFile, int objCount) throws IOException {
        File[] objFiles = runOutputDir.listFiles(new FileNamePrefixFilter("obj"));
        File[] metaFiles = runOutputDir.listFiles(new FileNamePrefixFilter("meta"));
        double[] allGenIGD = new double[objFiles.length];
        int[] allGenFE = new int[objFiles.length];
        for (int i = 0; i < objFiles.length; i++) {
            allGenIGD[i] = calculateIGD(objFiles[i], refSetFile, objCount);
            allGenFE[i] = getFE(metaFiles[i]);
        }
        try (PrintWriter printer = new PrintWriter(outFile)) {
            for (int i = 0; i < allGenFE.length; i++) {
                printer.println(String.format("%010d %10.8f", allGenFE[i], allGenIGD[i]));
            }
        }
    }

    private static int dumpStagnationFE(File runOutputDir, File outFile, int objCount) throws IOException {
        // Get a sorted array of meta files
        File[] metaFiles = runOutputDir.listFiles(new FileNamePrefixFilter("meta"));
        Arrays.sort(metaFiles);
        // Create an empty structure to store all extrems points of all generations
        double[][][] extremePoints = new double[metaFiles.length][objCount][objCount];
        // Parse meta files to extract extreme points and sill in the structure
        for (int metaFileIndex = 0; metaFileIndex < metaFiles.length; metaFileIndex++) {
            parseMetaFile(metaFiles, metaFileIndex, objCount, extremePoints);
        }
        // Get the corresponding FE values
        int[] funEvals = new int[metaFiles.length];
        for (int i = 0; i < metaFiles.length; i++) {
            funEvals[i] = getFE(metaFiles[i]);
        }
        int finalMetaFileIndex = getStagnationMetaFileIndex(extremePoints);
        // Dump the FE corresponding to the stagnation generation
        try (PrintWriter printer = new PrintWriter(outFile)) {
            printer.println(String.valueOf(funEvals[finalMetaFileIndex]));
        }
        // Return stagnation file index
        return finalMetaFileIndex;
    }

    private static int getStagnationMetaFileIndex(double[][][] extremePoints) {
        // Compare extreme points from generation to generation
        int finalMetaFileIndex = 0;
        for (int metaFileIndex = 1; metaFileIndex < extremePoints.length; metaFileIndex++) {
            midLoop:
            for (int i = 0; i < extremePoints[metaFileIndex].length; i++) {
                for (int j = 0; j < extremePoints[metaFileIndex][i].length; j++) {
                    if (Mathematics.compare(
                            extremePoints[metaFileIndex][i][j],
                            extremePoints[metaFileIndex - 1][i][j],
                            10e-8) != 0) {
                        finalMetaFileIndex = metaFileIndex;
                        break midLoop;
                    }
                }
            }
        }
        return finalMetaFileIndex;
    }

    private static void parseMetaFile(File[] metaFiles, int metaFileIndex, int objCount, double[][][] extremePoints) throws IOException, NumberFormatException {
        try (BufferedReader reader = new BufferedReader(new FileReader(metaFiles[metaFileIndex]))) {
            int extPointIndex = 0;
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("extreme_point")) {
                    String extPointStr = line.split("=")[1].trim();
                    extPointStr = extPointStr.substring(1, extPointStr.length() - 1);
                    String[] splits = extPointStr.split(",");
                    double[] extPointVector = new double[objCount];
                    for (int i = 0; i < splits.length; i++) {
                        extPointVector[i] = Double.parseDouble(splits[i].trim());
                    }
                    extremePoints[metaFileIndex][extPointIndex] = extPointVector;
                    extPointIndex++;
                }
            }
        }
    }

    private static void dumpAdditionalInfo(
            int worstRunIndex,
            int medianRunIndex,
            int bestRunIndex,
            int stagnationGenIndex,
            File outFile) throws IOException {
        try (PrintWriter printer = new PrintWriter(outFile)) {
            printer.println(String.format("worst_run = %d", worstRunIndex));
            printer.println(String.format("median_run = %d", medianRunIndex));
            printer.println(String.format("best_run = %d", bestRunIndex));
            printer.println(String.format("median_run_stagnation_gen = %d", stagnationGenIndex));
        }
    }

    public static class FileNamePrefixFilter implements FileFilter {

        private final String prefix;

        public FileNamePrefixFilter(String prefix) {
            this.prefix = prefix;
        }

        @Override
        public boolean accept(File file) {
            if (file.getName().startsWith(prefix)) {
                return true;
            } else {
                return false;
            }
        }

    }
}
