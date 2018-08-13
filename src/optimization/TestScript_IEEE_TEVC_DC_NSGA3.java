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
import asf.DTLZ4_ASF_Minimizer;
import asf.DTLZ7_ASF_Minimizer;
import asf.OSY_ASF_Minimizer;
import asf.SRN_ASF_Minimizer;
import asf.TNK_ASF_Minimizer;
import asf.UF1_ASF_Minimizer;
import asf.UF2_ASF_Minimizer;
import asf.WFG1_ASF_Minimizer;
import asf.ZDT1_ASF_Minimizer;
import asf.ZDT2_ASF_Minimizer;
import asf.ZDT3_ASF_Minimizer;
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
import engines.NSGA3_DC_TEVC;
import engines.UnifiedNSGA3Engine;
import evaluators.BNHEvaluator;
import evaluators.GeneralDTLZ1Evaluator;
import evaluators.GeneralDTLZ2Evaluator;
import evaluators.GeneralDTLZ4Evaluator;
import evaluators.GeneralDTLZ7Evaluator;
import evaluators.OSYEvaluator;
import evaluators.SRNEvaluator;
import evaluators.TNKEvaluator;
import evaluators.UF1Evaluator;
import evaluators.UF2Evaluator;
import evaluators.ZDT1Evaluator;
import evaluators.ZDT2Evaluator;
import evaluators.ZDT3Evaluator;
import evaluators.ZDT4Evaluator;
import evaluators.ZDT6Evaluator;
import evaluators.wfg.WFG1;
import evaluators.xmlbased.Wfg1Evaluator;
import extremels.BnhExtremeLocalSearch;
import extremels.Dtlz1ExtremeLocalSearch;
import extremels.Dtlz2ExtremeLocalSearch;
import extremels.Dtlz4ExtremeLocalSearch;
import extremels.Dtlz7ExtremeLocalSearch;
import extremels.ExtremeLocalSearch;
import extremels.OsyExtremeLocalSearch;
import extremels.SrnExtremeLocalSearch;
import extremels.TnkExtremeLocalSearch;
import extremels.Uf1ExtremeLocalSearch;
import extremels.Uf2ExtremeLocalSearch;
import extremels.Wfg1ExtremeLocalSearch;
import extremels.Zdt1ExtremeLocalSearch;
import extremels.Zdt2ExtremeLocalSearch;
import extremels.Zdt3ExtremeLocalSearch;
import extremels.Zdt4ExtremeLocalSearch;
import extremels.Zdt6ExtremeLocalSearch;
import haitham.Utilities;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
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
import kkt.xmlbased.Dtlz4KKTPMCalculator_03obj;
import kkt.xmlbased.Dtlz4KKTPMCalculator_05obj;
import kkt.xmlbased.Dtlz4KKTPMCalculator_10obj;
import kkt.xmlbased.Dtlz7KKTPMCalculator_03obj;
import kkt.xmlbased.OsyKKTPMCalculator;
import kkt.xmlbased.OsyKKTPMCalculatorNumerical;
import kkt.xmlbased.SrnKKTPMCalculator;
import kkt.xmlbased.TnkKKTPMCalculator;
import kkt.xmlbased.Uf1KKTPMCalculator;
import kkt.xmlbased.Uf2KKTPMCalculator;
import kkt.xmlbased.Wfg1KKTPMCalculator;
import kkt.xmlbased.Zdt1KKTPMCalculator;
import kkt.xmlbased.Zdt2KKTPMCalculator;
import kkt.xmlbased.Zdt3KKTPMCalculator;
import kkt.xmlbased.Zdt4KKTPMCalculator;
import kkt.xmlbased.Zdt6KKTPMCalculator;
import kkt.xmlbased.Zdt6KKTPMCalculatorNumerical;
import moeaframework.moead.GenericProblem;
import moeaframework.moead.ReportingExecutor;
import org.moeaframework.Executor;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.PRNG;
import org.moeaframework.core.Solution;
import org.moeaframework.core.variable.EncodingUtils;
import parsing.IndividualEvaluator;
import parsing.InvalidOptimizationProblemException;
import parsing.StaXParser;
import reference_directions.NestedReferenceDirectionsFactory;
import reference_directions.ReferenceDirection;
import utils.Mathematics;
import utils.PerformanceMetrics;
import utils.RandomNumberGenerator;

/**
 *
 * @author Haitham
 */
public class TestScript_IEEE_TEVC_DC_NSGA3 {

    // Ideal points
    static final double[] BNH_IDEAL_POINT = new double[]{5.577718464709733E-5, 4.000134245207236};
    static final double[] OSY_IDEAL_POINT = new double[]{-273.9999885325946, 4.010756522628899};
    static final double[] SRN_IDEAL_POINT = new double[]{10.10117140168606, -217.73837054184486};
    static final double[] TNK_IDEAL_POINT = new double[]{0.041664126903726846, 0.04166412690372694};
    static final double[] ZDT1_IDEAL_POINT = new double[]{0.0, 0.0};
    static final double[] ZDT2_IDEAL_POINT = new double[]{0.0, 0.0};
    static final double[] ZDT3_IDEAL_POINT = new double[]{0.0, 0.0};
    static final double[] ZDT4_IDEAL_POINT = new double[]{0.0, 0.0};
    static final double[] ZDT6_IDEAL_POINT = new double[]{0.0, 0.0};
    static final double[] DTLZ1_3OBJ_IDEAL_POINT = new double[]{0.0, 0.0, 0.0};
    static final double[] DTLZ1_5OBJ_IDEAL_POINT = new double[]{0.0, 0.0, 0.0, 0.0, 0.0};
    static final double[] DTLZ1_10OBJ_IDEAL_POINT = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0};
    static final double[] DTLZ2_3OBJ_IDEAL_POINT = new double[]{0.0, 0.0, 0.0};
    static final double[] DTLZ2_5OBJ_IDEAL_POINT = new double[]{0.0, 0.0, 0.0, 0.0, 0.0};
    static final double[] DTLZ2_10OBJ_IDEAL_POINT = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0};
    static final double[] DTLZ4_3OBJ_IDEAL_POINT = new double[]{0.0, 0.0, 0.0};
    static final double[] DTLZ4_5OBJ_IDEAL_POINT = new double[]{0.0, 0.0, 0.0, 0.0, 0.0};
    static final double[] DTLZ4_10OBJ_IDEAL_POINT = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0};
    static final double[] DTLZ7_3OBJ_IDEAL_POINT = new double[]{0.0, 0.0, 0.0};
    static final double[] WFG1_3OBJ_IDEAL_POINT = new double[]{0.0, 0.0, 0.0};
    static final double[] UF1_IDEAL_POINT = new double[]{0.0, 0.0};
    static final double[] UF2_IDEAL_POINT = new double[]{0.0, 0.0};
    // HV Reference points
    static final double[] BNH_REF_POINT = new double[]{135.5510091212441, 4.000134245207236};
    static final double[] OSY_REF_POINT = new double[]{-45.65999303898395, 75.99998952782383};
    static final double[] SRN_REF_POINT = new double[]{222.8448578266786, 2.376103226277362};
    static final double[] TNK_REF_POINT = new double[]{1.0384498374343492, 1.0384498374343492};
    static final double[] ZDT1_REF_POINT = new double[]{1.0, 1.0};
    static final double[] ZDT2_REF_POINT = new double[]{1.0, 1.0};
    static final double[] ZDT3_REF_POINT = new double[]{1.0, 1.0};
    static final double[] ZDT4_REF_POINT = new double[]{1.0, 1.0};
    static final double[] ZDT6_REF_POINT = new double[]{1.0, 1.0};
    static final double[] DTLZ1_3OBJ_REF_POINT = new double[]{0.5, 0.5, 0.5};
    static final double[] DTLZ1_5OBJ_REF_POINT = new double[]{0.5, 0.5, 0.5, 0.5, 0.5};
    static final double[] DTLZ1_10OBJ_REF_POINT = new double[]{0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5};
    static final double[] DTLZ2_3OBJ_REF_POINT = new double[]{1.0, 1.0, 1.0};
    static final double[] DTLZ2_5OBJ_REF_POINT = new double[]{1.0, 1.0, 1.0, 1.0, 1.0};
    static final double[] DTLZ2_10OBJ_REF_POINT = new double[]{1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0};
    static final double[] DTLZ4_3OBJ_REF_POINT = new double[]{1.0, 1.0, 1.0};
    static final double[] DTLZ4_5OBJ_REF_POINT = new double[]{1.0, 1.0, 1.0, 1.0, 1.0};
    static final double[] DTLZ4_10OBJ_REF_POINT = new double[]{1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0};
    static final double[] DTLZ7_3OBJ_REF_POINT = new double[]{1.0, 1.0, 3.0};
    static final double[] WFG1_3OBJ_REF_POINT = new double[]{2.0, 4.0, 6.0};
    static final double[] UF1_REF_POINT = new double[]{1.0, 1.0};
    static final double[] UF2_REF_POINT = new double[]{1.0, 1.0};

    // Reference Pareto Front File
    static final File BNH_REF_SET_FILE = new File("F:/POST-GECCO-2016/Results/ref_sets/bnh_obj_refset_1000.dat");
    static final File OSY_REF_SET_FILE = new File("E:/Dropbox/Reference_Pareto_Sets/osy_obj_refset_1000.dat");
    static final File SRN_REF_SET_FILE = new File("F:/POST-GECCO-2016/Results/ref_sets/srn_obj_refset_1000.dat");
    static final File TNK_REF_SET_FILE = new File("F:/POST-GECCO-2016/Results/ref_sets/tnk_obj_refset_1000.dat");

    static final File ZDT1_REF_SET_FILE = new File("F:/POST-GECCO-2016/Results/ref_sets/zdt1_obj_refset_1000.dat");
    static final File ZDT2_REF_SET_FILE = new File("F:/POST-GECCO-2016/Results/ref_sets/zdt2_obj_refset_1000.dat");
    static final File ZDT3_REF_SET_FILE = new File("F:/POST-GECCO-2016/Results/ref_sets/zdt3_obj_refset_1000.dat");

    static final File ZDT4_REF_SET_FILE = new File("F:/POST-GECCO-2016/Results/ref_sets/zdt4_obj_refset_1000.dat");
    static final File ZDT6_REF_SET_FILE = new File("F:/POST-GECCO-2016/Results/ref_sets/zdt6_obj_refset_0999.dat");

    static final File DTLZ1_3OBJ_REF_SET_FILE = new File("F:/IEEE-TEVC-DC-NSGA-III/DTLZ1_DTLZ2_Pareto_fronts/dtlz1_03obj_pf.dat");
    static final File DTLZ1_5OBJ_REF_SET_FILE = new File("F:/IEEE-TEVC-DC-NSGA-III/DTLZ1_DTLZ2_Pareto_fronts/dtlz1_05obj_pf.dat");
    static final File DTLZ1_10OBJ_REF_SET_FILE = new File("F:/IEEE-TEVC-DC-NSGA-III/DTLZ1_DTLZ2_Pareto_fronts/dtlz1_10obj_pf.dat");

    static final File DTLZ2_3OBJ_REF_SET_FILE = new File("F:/IEEE-TEVC-DC-NSGA-III/DTLZ1_DTLZ2_Pareto_fronts/dtlz2_03obj_pf.dat");
    static final File DTLZ2_5OBJ_REF_SET_FILE = new File("F:/IEEE-TEVC-DC-NSGA-III/DTLZ1_DTLZ2_Pareto_fronts/dtlz2_05obj_pf.dat");
    static final File DTLZ2_10OBJ_REF_SET_FILE = new File("F:/IEEE-TEVC-DC-NSGA-III/DTLZ1_DTLZ2_Pareto_fronts/dtlz2_10obj_pf.dat");

    static final File DTLZ4_3OBJ_REF_SET_FILE = new File("F:/IEEE-TEVC-DC-NSGA-III/DTLZ1_DTLZ2_Pareto_fronts/dtlz2_03obj_pf.dat"); // Same front as DTLZ2
    static final File DTLZ4_5OBJ_REF_SET_FILE = new File("F:/IEEE-TEVC-DC-NSGA-III/DTLZ1_DTLZ2_Pareto_fronts/dtlz2_05obj_pf.dat");
    static final File DTLZ4_10OBJ_REF_SET_FILE = new File("F:/IEEE-TEVC-DC-NSGA-III/DTLZ1_DTLZ2_Pareto_fronts/dtlz2_10obj_pf.dat");

    static final File DTLZ7_3OBJ_REF_SET_FILE = new File("F:/POST-GECCO-2016/Results/ref_sets/dtlz7_obj_refset_0127_3obj_filtered.dat");

    static final File WFG1_3OBJ_REF_SET_FILE = new File("E:/Dropbox/Reference_Pareto_Sets/wfg1_3obj_obj_refset_92.dat");

    static final File UF1_REF_SET_FILE = new File("E:/Dropbox/Reference_Pareto_Sets/uf1_uf2_uf3_obj_refset_1000.dat");
    static final File UF2_REF_SET_FILE = new File("E:/Dropbox/Reference_Pareto_Sets/uf1_uf2_uf3_obj_refset_1000.dat");

    // Problem Gefinition
    static URL url;
    static OptimizationProblem optimizationProblem;

    static IndividualEvaluator individualEvaluator;
    static ASF_Minimizer asfMinimizer;
    static KKT_Calculator kktCalculator;
    static KKT_Calculator kktCalculatorNumerical;
    static ExtremeLocalSearch extLS;
    // Number of runs
    static int runCount;
    static double seed;
    // General Parameters
    static int popSize; // Population size
    static int emo2017GenCount; // Generations count
    static int targetKktpmState; // Generations count
    static int etaC; // SBX distribution index
    static int etaM; // PM distribution index
    // Epsilon-Dominance Parameter
    static double epsilon;
    // Interval over which results are collected
    static int interval;
    // IGD/GD comparison FE limit
    static int feLimit;
    // Output Directory
    static File topOutputDir;
    // Ideal Point
    static double[] idealPoint;
    // Reference Point
    static double[] refPoint;
    // Reference Set File
    static File refSetFile;
    // divisions
    static int[] divisions;

    public static final int GD = 0;
    public static final int IGD = 1;
    public static final int HV = 2;
    public static final int KKTPM = 3;
    public static final int COLLECT_BEST_KKTPM = 0;
    public static final int COLLECT_MEDIAN_KKTPM = 1;

    static {
        try {
            // Problem Definition
            url = NSGA3Engine.class.getResource("../samples/wfg1.xml");     // Modify Here
            optimizationProblem = StaXParser.readProblem(url.openStream());
            individualEvaluator = new WFG1(2, 2, 3);//new OSYEvaluator();   // Modify Here
            idealPoint = WFG1_3OBJ_IDEAL_POINT;                                  // Modify Here
            refPoint = WFG1_3OBJ_REF_POINT;                                      // Modify Here
            refSetFile = WFG1_3OBJ_REF_SET_FILE;                                 // Modify Here
            asfMinimizer = new WFG1_ASF_Minimizer(2);//OSY_ASF_Minimizer();  // Modify Here
            kktCalculator = new Wfg1KKTPMCalculator();//OsyKKTPMCalculator();//null;//                      // Modify Here
            kktCalculatorNumerical = new Wfg1KKTPMCalculator();//OsyKKTPMCalculatorNumerical();//null;                      // Modify Here
            extLS = new Wfg1ExtremeLocalSearch(2);//OsyExtremeLocalSearch();                           // Modify Here
            // Number of runs
            runCount = 3;                                                  // Modify Here
            seed = 0.1;
            // General Parameters
            popSize = 92; // Population size                               // Modify Here
            divisions = new int[]{12}; // Population size                               // Modify Here            
            emo2017GenCount = 200; // Generations count // Modify Here
            etaC = 30; // SBX distribution index
            etaM = 20; // PM distribution index
            // Epsilon-Dominance Parameter
            epsilon = 0.0;
            // Interval over which results are collected
            interval = popSize * 8;
            // IGD/GD comparison FE limit
            feLimit = 20000; // If this is larger than the actual number of function evaluations consumed, the final generation will be considered for collecting statistics.
            // Output Directory
            topOutputDir = new File("F:\\IEEE-TEVC-DC-NSGA-III\\Results");
        } catch (IOException ex) {
            Logger.getLogger(TestScript_IEEE_TEVC_DC_NSGA3.class.getName()).log(Level.SEVERE, null, ex);
        } catch (XMLStreamException ex) {
            Logger.getLogger(TestScript_IEEE_TEVC_DC_NSGA3.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvalidOptimizationProblemException ex) {
            Logger.getLogger(TestScript_IEEE_TEVC_DC_NSGA3.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MWException ex) {
            Logger.getLogger(TestScript_IEEE_TEVC_DC_NSGA3.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static AbstractGeneticEngine nsga3Engine;
    private static AbstractGeneticEngine tevcEngine;
    private static AbstractGeneticEngine tevcEngineNumerical;

    private static String input;

    public static void main(String[] args) throws MWException, IOException, XMLStreamException, InvalidOptimizationProblemException, FileNotFoundException, DoubleAssignmentException {
        System.out.println("If you want a full run press (r), "
                + "if you want just to collect the statistics press (Enter).");
        input = new Scanner(System.in).nextLine();
        runAllAlgorithms();
        // Collect results
        // IGD
        System.out.println("IGD Calculations ...");
        _collect(IGD, "igd_", ".txt", interval);
        // GD
        System.out.println("GD Calculations ...");
        _collect(GD, "gd_", ".txt", interval);
        // KKTPM
        System.out.println("Median KKTPM Calculations ...");
        targetKktpmState = COLLECT_MEDIAN_KKTPM;
        _collect(KKTPM, "kktpm_median_", ".txt", interval);
        _collectKktpmThreshold("kktpm_median_", ".txt", 0.01);
        System.out.println("Best KKTPM Calculations ...");
        targetKktpmState = COLLECT_BEST_KKTPM;
        _collect(KKTPM, "kktpm_best_", ".txt", interval);
        _collectKktpmThreshold("kktpm_best_", ".txt", 0.01);
        // Store best and median IGD runs
        System.out.println("Best/Median IGD Calculations ...");
        _collectBestAndMedianAllAlgorithms(IGD, feLimit);
        // Store best and median GD runs
        System.out.println("Best/Median GD Calculations ...");
        _collectBestAndMedianAllAlgorithms(GD, feLimit);
        // Store reference file path
        System.out.println("Store Reference File Path ...");
        _collectRefFilePath();
    }

    private static void _collectRefFilePath() throws IOException {
        appendToAdditionalInfo(tevcEngine.getAlgorithmName(), "ref_file_path", refSetFile.getPath());
        appendToAdditionalInfo(nsga3Engine.getAlgorithmName(), "ref_file_path", refSetFile.getPath());
        if (kktCalculatorNumerical != null) {
            appendToAdditionalInfo(tevcEngineNumerical.getAlgorithmName(), "ref_file_path", refSetFile.getPath());
        }
        if (optimizationProblem.constraints == null || optimizationProblem.constraints.length == 0) {
            appendToAdditionalInfo("moead", "ref_file_path", refSetFile.getPath());
        }
    }

    private static void runAllAlgorithms() throws DoubleAssignmentException, MWException, IOException {
        if (optimizationProblem.objectives.length == 2) {
            optimizationProblem.setSteps(popSize - 1);
        }
        // Set parameters
        optimizationProblem.setPopulationSize(popSize);
        optimizationProblem.setRealCrossoverDistIndex(etaC);
        optimizationProblem.setRealMutationDistIndex(etaM);
        // Test NSGA3_DC_TEVC Engine
        System.out.println(">> DC_NSGA_3");
        int emo2017TotalFunEval = testTEVC(optimizationProblem);
        // Test NSGA3_DC_TEVC_NUMERICAL Engine
        if (kktCalculatorNumerical != null) {
            System.out.println(">> DC_NSGA_3 (NUMERICAL)");
            testTEVCNumerical(optimizationProblem, emo2017TotalFunEval);
        }
        // Test NSGA-III Engine
        System.out.println(">> NSGA_3");
        testNSGA3(optimizationProblem, emo2017TotalFunEval);
        // Test MOEA/D
        if (optimizationProblem.constraints == null || optimizationProblem.constraints.length == 0) {
            System.out.println(">> MOEA/D");
            testMOEAD(optimizationProblem, emo2017TotalFunEval);
        }
    }

    private static void testNSGA3(OptimizationProblem optimizationProblem, int tevcTotalFunEval) throws DoubleAssignmentException, MWException, IOException {
        // Test NSGA-III Engine
        optimizationProblem.setGenerationsCount(Integer.MAX_VALUE);
        nsga3Engine = new UnifiedNSGA3Engine(optimizationProblem, individualEvaluator, divisions);
        if (input.equalsIgnoreCase("r")) {
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
                        kktCalculator,
                        Double.MAX_VALUE,
                        tevcTotalFunEval / runCount);
                // Reset the number of function evaluations to start
                // counting from Zero again in the next iteration
                individualEvaluator.resetFunctionEvaluationsCount();
            }
        }
    }

    private static void testMOEAD(OptimizationProblem optimizationProblem, int tevcTotalFunEval) throws DoubleAssignmentException, MWException, IOException {
        // Test NSGA-III Engine
        optimizationProblem.setGenerationsCount(Integer.MAX_VALUE);
        if (input.equalsIgnoreCase("r")) {
            PRNG.setRandom(new Random((long) (seed * Long.MAX_VALUE)));
            GenericProblem genericProblem = new GenericProblem(optimizationProblem, individualEvaluator);
            // Generate reference directions
            List<ReferenceDirection> referenceDirectionsList = new NestedReferenceDirectionsFactory(optimizationProblem.objectives.length).generateDirections(divisions);
            // Writing directions to a temp file that will be read by MOEA/D
            try (PrintWriter printer = new PrintWriter("e:/temp_dirs.dat")) {
                for (ReferenceDirection referenceDirection : referenceDirectionsList) {
                    for (int i = 0; i < referenceDirection.direction.length; i++) {
                        printer.format("%f ", referenceDirection.direction[i]);
                    }
                    printer.println();
                }
            }
            // Call MOEA/D
            List<NondominatedPopulation> results = new ReportingExecutor(optimizationProblem, individualEvaluator, kktCalculator, referenceDirectionsList)
                    .withAlgorithm("MOEAD")
                    .withProperty("populationSize", /*optimizationProblem.getPopulationSize()*/referenceDirectionsList.size()) // PLZ KEEP IN MIND THAT THE POPSIZE HERE NEED TO BE CLOSE TO THE ONE USED BY THE OTHER ALGORITHMS
                    .withProperty("de.crossoverRate", optimizationProblem.getRealCrossoverProbability())
                    .withProperty("pm.rate", optimizationProblem.getRealMutationProbability())
                    .withProperty("pm.distributionIndex", optimizationProblem.getRealMutationDistIndex())
                    .withProblem(genericProblem)
                    .withMaxEvaluations(tevcTotalFunEval / runCount)
                    .runSeeds(runCount);
//        for (Solution solution : results.get(0)) {
//            System.out.printf("%.5f %.5f\n",
//                    solution.getObjective(0),
//                    solution.getObjective(1));
//        }
        }
    }

    private static void testTEVCNumerical(OptimizationProblem optimizationProblem, int tevcTotalFunEval) throws DoubleAssignmentException, MWException, IOException {
        tevcEngineNumerical = new engines.NSGA3_DC_TEVC_NUMERICAL(optimizationProblem, individualEvaluator, divisions, extLS);
        if (input.equalsIgnoreCase("r")) {
            tevcEngineNumerical.DUMP_ALL_GENERATIONS_DECISION_SPACE = true;
            tevcEngineNumerical.DUMP_ALL_GENERATIONS_KKTPM = true;
            tevcEngineNumerical.DUMP_ALL_GENERATIONS_MATLAB_SCRIPTS = true;
            tevcEngineNumerical.DUMP_ALL_GENERATIONS_META_DATA = true;
            tevcEngineNumerical.DUMP_ALL_GENERATIONS_OBJECTIVE_SPACE = true;
            RandomNumberGenerator.setSeed(seed);
            for (int runIndex = 0; runIndex < runCount; runIndex++) {
                try {
                    TestScript_EMO2017.printer = new PrintWriter(new File(getRunOutputDir(getEngineOutputDir(optimizationProblem, tevcEngineNumerical.getAlgorithmName()), runIndex) + File.separator + "phases.txt"));
                    System.out.println();
                    System.out.println(String.format("Run(%02d)", runIndex));
                    Individual[] emo2017FinalPop = tevcEngineNumerical.start(
                            getRunOutputDir(getEngineOutputDir(optimizationProblem, tevcEngineNumerical.getAlgorithmName()), runIndex),
                            runIndex,
                            epsilon,
                            asfMinimizer,
                            kktCalculatorNumerical,
                            Double.MAX_VALUE,
                            tevcTotalFunEval / runCount);
                    System.out.println(String.format("FE Count (run %03d): ", runIndex) + individualEvaluator.getFunctionEvaluationsCount());
                    // Reset the number of function evaluations to start
                    // counting from Zero again in the next iteration
                    individualEvaluator.resetFunctionEvaluationsCount();
                } finally {
                    // Close phases printer
                    if (TestScript_EMO2017.printer != null) {
                        TestScript_EMO2017.printer.close();
                    }
                }
            }
        }
    }

    private static int testTEVC(OptimizationProblem optimizationProblem) throws DoubleAssignmentException, MWException, IOException {
        // Test NSGA3_DC_EMO2017 Engine
        optimizationProblem.setGenerationsCount(emo2017GenCount);
        tevcEngine = new NSGA3_DC_TEVC(optimizationProblem, individualEvaluator, divisions, extLS);
        if (input.equalsIgnoreCase("r")) {
            tevcEngine.DUMP_ALL_GENERATIONS_DECISION_SPACE = true;
            tevcEngine.DUMP_ALL_GENERATIONS_KKTPM = true;
            tevcEngine.DUMP_ALL_GENERATIONS_MATLAB_SCRIPTS = true;
            tevcEngine.DUMP_ALL_GENERATIONS_META_DATA = true;
            tevcEngine.DUMP_ALL_GENERATIONS_OBJECTIVE_SPACE = true;
            RandomNumberGenerator.setSeed(seed);
            int emo2017TotalFunEval = 0;
            for (int runIndex = 0; runIndex < runCount; runIndex++) {
                try {
                    TestScript_EMO2017.printer = new PrintWriter(new File(getRunOutputDir(getEngineOutputDir(optimizationProblem, tevcEngine.getAlgorithmName()), runIndex) + File.separator + "phases.txt"));
                    System.out.println();
                    System.out.println(String.format("Run(%02d)", runIndex));
                    Individual[] emo2017FinalPop = tevcEngine.start(
                            getRunOutputDir(getEngineOutputDir(optimizationProblem, tevcEngine.getAlgorithmName()), runIndex),
                            runIndex,
                            epsilon,
                            asfMinimizer,
                            kktCalculator,
                            Double.MAX_VALUE,
                            Integer.MAX_VALUE);
                    emo2017TotalFunEval += individualEvaluator.getFunctionEvaluationsCount();
                    System.out.println(String.format("FE Count (run %03d): ", runIndex) + individualEvaluator.getFunctionEvaluationsCount());
                    // Reset the number of function evaluations to start
                    // counting from Zero again in the next iteration
                    individualEvaluator.resetFunctionEvaluationsCount();
                } finally {
                    // Close phases printer
                    if (TestScript_EMO2017.printer != null) {
                        TestScript_EMO2017.printer.close();
                    }
                }
            }
            return emo2017TotalFunEval;
            // Return the total number of function evaluations used by all runs
        } else {
            return -1;
        }
    }

    private static void _collectBestAndMedianAllAlgorithms(int metricType, int feLimit) throws IOException {
        System.out.println("\tNGSA-III");
        _collectBestAndMedian(nsga3Engine.getAlgorithmName(), metricType, feLimit);
        System.out.println("\tTEVC");
        _collectBestAndMedian(tevcEngine.getAlgorithmName(), metricType, feLimit);
        if (kktCalculatorNumerical != null) {
            System.out.println("\tTEVC (Numerical)");
            _collectBestAndMedian(tevcEngineNumerical.getAlgorithmName(), metricType, feLimit);
        }
        if (optimizationProblem.constraints == null || optimizationProblem.constraints.length == 0) {
            System.out.println("\tMOEA/D");
            _collectBestAndMedian("moead", metricType, feLimit);
        }
    }

    private static void _collectBestAndMedian(String algorithmName, int metricType, int feLimit) throws IOException {
        double[] metricPerRun = new double[runCount];
        File[] lastGenerationToConsiderPerRun = new File[runCount];
        for (int runIndex = 0; runIndex < runCount; runIndex++) {
            File runOutputDir = getRunOutputDir(
                    getEngineOutputDir(
                            optimizationProblem,
                            algorithmName),
                    runIndex);
            // Get all the meta files of the current run
            File[] metaFiles = runOutputDir.listFiles(
                    new FileNamePrefixFilter("meta"));
            // Get the FE of each generation
            int[] fePerGen = new int[metaFiles.length];
            for (int i = 0; i < metaFiles.length; i++) {
                fePerGen[i] = getFE(metaFiles[i]);
            }
            // Get all the objective files
            File[] objFiles = runOutputDir.listFiles(
                    new FileNamePrefixFilter("obj"));
            // Select the generation closest to the specified FE limit
            int closestGenIndex = getClosestGeneration(metaFiles, fePerGen, feLimit);
            // Extract the objective space file of the current run at the specified FE limit
            lastGenerationToConsiderPerRun[runIndex] = objFiles[closestGenIndex];
            // Store the metric of the current run
            if (metricType == IGD) {
                metricPerRun[runIndex] = calculateIGD(
                        objFiles[closestGenIndex],
                        refSetFile,
                        optimizationProblem.objectives.length);
            } else if (metricType == GD) {
                metricPerRun[runIndex] = calculateGD(
                        objFiles[closestGenIndex],
                        refSetFile,
                        optimizationProblem.objectives.length);
            }
        }
        // Get the index of the minimum/median metric run
        int minMetricRunIndex = Mathematics.getMinIndex(metricPerRun);
        int medianMetricRunIndex = Mathematics.getMedianIndex(metricPerRun);
        // Append the new info to the additional Info file
        if (metricType == IGD) {
            appendToAdditionalInfo(algorithmName, "igd_best_run", String.valueOf(minMetricRunIndex));
            appendToAdditionalInfo(algorithmName, "igd_best", String.valueOf(metricPerRun[minMetricRunIndex]));
            appendToAdditionalInfo(algorithmName, "igd_median_run", String.valueOf(medianMetricRunIndex));
            appendToAdditionalInfo(algorithmName, "igd_median", String.valueOf(metricPerRun[medianMetricRunIndex]));
            // Store all metrics of all runs (used for statistical significance analysis later)
            writeToFile(metricPerRun,
                    new File(getEngineOutputDir(
                            optimizationProblem,
                            algorithmName).getPath(),
                            "igd_all_runs_" + algorithmName + ".txt"
                    )
            );
            // Copy the minimum IGD run objective file to the top directory
            Files.copy(
                    lastGenerationToConsiderPerRun[minMetricRunIndex].toPath(),
                    new File(getEngineOutputDir(optimizationProblem, algorithmName) + File.separator + "igd_best_run_" + algorithmName + ".txt").toPath(),
                    StandardCopyOption.REPLACE_EXISTING);
            // Copy the median IGD run objective file to the top directory
            Files.copy(
                    lastGenerationToConsiderPerRun[medianMetricRunIndex].toPath(),
                    new File(getEngineOutputDir(optimizationProblem, algorithmName) + File.separator + "igd_median_run_" + algorithmName + ".txt").toPath(),
                    StandardCopyOption.REPLACE_EXISTING);
        } else if (metricType == GD) {
            appendToAdditionalInfo(algorithmName, "gd_best_run", String.valueOf(minMetricRunIndex));
            appendToAdditionalInfo(algorithmName, "gd_best", String.valueOf(metricPerRun[minMetricRunIndex]));
            appendToAdditionalInfo(algorithmName, "gd_median_run", String.valueOf(medianMetricRunIndex));
            appendToAdditionalInfo(algorithmName, "gd_median", String.valueOf(metricPerRun[medianMetricRunIndex]));
            // Store all metrics of all runs (used for statistical significance analysis later)
            writeToFile(metricPerRun,
                    new File(getEngineOutputDir(
                            optimizationProblem,
                            algorithmName).getPath(),
                            "gd_all_runs_" + algorithmName + ".txt"
                    )
            );
            // Copy the minimum IGD run objective file to the top directory
            Files.copy(
                    lastGenerationToConsiderPerRun[minMetricRunIndex].toPath(),
                    new File(getEngineOutputDir(optimizationProblem, algorithmName) + File.separator + "gd_best_run_" + algorithmName + ".txt").toPath(),
                    StandardCopyOption.REPLACE_EXISTING);
            // Copy the median IGD run objective file to the top directory
            Files.copy(
                    lastGenerationToConsiderPerRun[medianMetricRunIndex].toPath(),
                    new File(getEngineOutputDir(optimizationProblem, algorithmName) + File.separator + "gd_median_run_" + algorithmName + ".txt").toPath(),
                    StandardCopyOption.REPLACE_EXISTING);
        }
//        // Store best metric progress
//        if (metricType == IGD) {
//            writeMetricProgressOfSingleRun(
//                    algorithmName,
//                    minMetricRunIndex,
//                    feLimit,
//                    metricType,
//                    "igd_best_");
//        } else if (metricType == GD) {
//            writeMetricProgressOfSingleRun(
//                    algorithmName,
//                    minMetricRunIndex,
//                    feLimit,
//                    metricType,
//                    "gd_best_");
//        }
//        // Store median metric progress
//        if (metricType == IGD) {
//            writeMetricProgressOfSingleRun(
//                    algorithmName,
//                    medianMetricRunIndex,
//                    feLimit,
//                    metricType,
//                    "igd_median_");
//        } else if (metricType == GD) {
//            writeMetricProgressOfSingleRun(
//                    algorithmName,
//                    medianMetricRunIndex,
//                    feLimit,
//                    metricType,
//                    "gd_median_");
//        }
    }

    private static void writeMetricProgressOfSingleRun(
            String algorithmName,
            int runIndex,
            int feLimit,
            int metricType,
            String outFilePrefix) throws IOException {
        // Create a file containing the metric across all generations of the run
        File runOutputDir = getRunOutputDir(
                getEngineOutputDir(
                        optimizationProblem,
                        algorithmName),
                runIndex);
        // Get all the meta files of the current run
        File[] metaFiles = runOutputDir.listFiles(
                new FileNamePrefixFilter("meta"));
        // Get all the corressponding obj files
        File[] objFiles = runOutputDir.listFiles(
                new FileNamePrefixFilter("obj"));
        // Get the FE of each generation
        int[] fePerGen = new int[metaFiles.length];
        for (int i = 0; i < metaFiles.length; i++) {
            fePerGen[i] = getFE(metaFiles[i]);
        }
        // Select the generation closest to the specified FE limit
        int closestGenIndex = getClosestGeneration(metaFiles, fePerGen, feLimit);
        // Write all metric values up to the closest generation to a file
        writeMetricProgressOverGenerations(
                algorithmName,
                closestGenIndex,
                metricType,
                fePerGen,
                objFiles,
                outFilePrefix);
    }

    private static void writeMetricProgressOverGenerations(
            String algorithmName,
            int closestGenIndex,
            int metricType,
            int[] fePerGen,
            File[] objFiles,
            String outFilePrefix) throws IOException, FileNotFoundException {
        // Store all metric values up to the closest generation
        List<FunEvalMetricPair> feVsMetric = new ArrayList<>();
        for (int i = 0; i < closestGenIndex; i++) {
            if (metricType == IGD) {
                feVsMetric.add(new FunEvalMetricPair(fePerGen[i],
                        calculateIGD(
                                objFiles[i],
                                refSetFile,
                                optimizationProblem.objectives.length)));
            } else if (metricType == GD) {
                feVsMetric.add(new FunEvalMetricPair(fePerGen[i],
                        calculateGD(
                                objFiles[i],
                                refSetFile,
                                optimizationProblem.objectives.length)));
            }
        }
        writeToFile(feVsMetric,
                new File(getEngineOutputDir(
                        optimizationProblem,
                        algorithmName).getPath(),
                        outFilePrefix + algorithmName + ".txt"
                )
        );
    }

    private static int getClosestGeneration(File[] metaFiles, int[] fePerGen, int feLimit) {
        // Select the generation closest to the specified FE limit
        int closestGenDiff = Integer.MAX_VALUE;
        int closestGenIndex = -1;
        for (int i = 0; i < metaFiles.length; i++) {
            int absoluteDiff = Math.abs(fePerGen[i] - feLimit);
            if (absoluteDiff < closestGenDiff) {
                closestGenDiff = absoluteDiff;
                closestGenIndex = i;
            }
        }
        return closestGenIndex;
    }

    private static void _collect(
            int metricType,
            String prefix,
            String suffix,
            int interval) throws IOException {
        // NSGA-III
        System.out.println("\tNSGA-III");
        _collectFeVsMetricToFile(nsga3Engine.getAlgorithmName(),
                metricType,
                prefix,
                suffix,
                interval);
        // TEVC
        System.out.println("\tTEVC");
        _collectFeVsMetricToFile(tevcEngine.getAlgorithmName(),
                metricType,
                prefix,
                suffix,
                interval);
        // TEVC
        if (kktCalculatorNumerical != null) {
            System.out.println("\tTEVC (Numerical)");
            _collectFeVsMetricToFile(tevcEngineNumerical.getAlgorithmName(),
                    metricType,
                    prefix,
                    suffix,
                    interval);
        }
        // MOEA/D
        if (optimizationProblem.constraints == null || optimizationProblem.constraints.length == 0) {
            System.out.println("\tMOEA/D");
            _collectFeVsMetricToFile("moead",
                    metricType,
                    prefix,
                    suffix,
                    interval);
        }
    }

    private static void _collectKktpmThreshold(
            String kktpmPrefix,
            String kktpmSuffix,
            double kktpmThresold) throws IOException {
        _getKktpmThresholdToFile(kktpmPrefix, kktpmSuffix, tevcEngine.getAlgorithmName(), kktpmThresold);
        _getKktpmThresholdToFile(kktpmPrefix, kktpmSuffix, nsga3Engine.getAlgorithmName(), kktpmThresold);
        if (kktCalculatorNumerical != null) {
            _getKktpmThresholdToFile(kktpmPrefix, kktpmSuffix, tevcEngineNumerical.getAlgorithmName(), kktpmThresold);
        }
        if (optimizationProblem.constraints == null || optimizationProblem.constraints.length == 0) {
            _getKktpmThresholdToFile(kktpmPrefix, kktpmSuffix, "moead", kktpmThresold);
        }
    }

    private static void _collectFeVsMetricToFile(
            String algorithmName,
            int metricType,
            String prefix,
            String suffix,
            int interval) throws IOException, FileNotFoundException {
        List<FunEvalMetricPair> feVsMetric = _collectFEvsMetric(
                optimizationProblem.objectives.length,
                algorithmName, metricType, interval);
        File outFile = new File(
                getEngineOutputDir(
                        optimizationProblem,
                        algorithmName)
                + File.separator + prefix + algorithmName + suffix);
        writeToFile(feVsMetric, outFile);
    }

    private static double getMedianKKTPM(File kktpmFile) throws IOException {
        List<Double> numList = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(kktpmFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                numList.add(Double.parseDouble(line.trim()));
            }
        }
        double[] numArr = new double[numList.size()];
        for (int i = 0; i < numList.size(); i++) {
            numArr[i] = numList.get(i);
        }
        return Mathematics.getMedian(numArr);
    }

    private static double getBestKKTPM(File kktpmFile) throws IOException {
        List<Double> numList = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(kktpmFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                numList.add(Double.parseDouble(line.trim()));
            }
        }
        double[] numArr = new double[numList.size()];
        for (int i = 0; i < numList.size(); i++) {
            numArr[i] = numList.get(i);
        }
        return Mathematics.getMin(numArr);
    }

    private static void _getKktpmThresholdToFile(
            String prefix,
            String suffix,
            String algorithmName,
            double kktpmThreshold) throws IOException {
        int fe = _getKktpmThreshold(prefix, suffix, algorithmName, kktpmThreshold);
        File kktpmThresholdOutFile = new File(
                getEngineOutputDir(
                        optimizationProblem,
                        algorithmName)
                + File.separator + String.format(prefix + "%05.5f_", kktpmThreshold)
                + algorithmName + suffix);
        try (PrintWriter printer = new PrintWriter(kktpmThresholdOutFile)) {
            printer.println(fe);
        }
    }

    private static int _getKktpmThreshold(
            String prefix,
            String suffix,
            String algorithmName,
            double kktpmThreshold) throws IOException {
        File kktpmFile = new File(
                getEngineOutputDir(optimizationProblem, algorithmName).getPath()
                + File.separator + prefix + algorithmName + suffix);
        try (BufferedReader reader = new BufferedReader(new FileReader(kktpmFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] splits = line.trim().split("\\s+");
                if (Double.parseDouble(splits[1]) < kktpmThreshold) {
                    return Integer.parseInt(splits[0]);
                }
            }
        }
        // The threshold has never been reached
        return -1;
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
        // Retrieve population (objective space)
        try (BufferedReader reader = new BufferedReader(new FileReader(popFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] splits = line.split("\\s+");
                VirtualIndividual virtualIndividual = new VirtualIndividual(objCount);
                for (int i = 0; i < splits.length; i++) {
                    virtualIndividual.setObjective(i, Double.parseDouble(splits[i]));
                }
                popList.add(virtualIndividual);
            }
        }
        VirtualIndividual[] pop = new VirtualIndividual[popList.size()];
        popList.toArray(pop);
        // Prepare reference set (ideally the Pareto front)
        if (optimizationProblem.getProblemID().toLowerCase().contains("dtlz1")) {
            // DTLZ1
            VirtualIndividual[] refSet = new VirtualIndividual[pop.length];
            for (int i = 0; i < pop.length; i++) {
                double sum = 0.0;
                for (int j = 0; j < pop[i].getObjectivesCount(); j++) {
                    sum += pop[i].getObjective(j);
                }
                refSet[i] = new VirtualIndividual(objCount);
                for (int j = 0; j < pop[i].getObjectivesCount(); j++) {
                    refSet[i].setObjective(j, pop[i].getObjective(j) / sum);
                }
            }
            // Calculate and return GD
            return PerformanceMetrics.calculateGenerationalDistance(objCount, pop, refSet, 2);
        } else if (optimizationProblem.getProblemID().toLowerCase().contains("dtlz2")
                || optimizationProblem.getProblemID().toLowerCase().contains("dtlz3")
                || optimizationProblem.getProblemID().toLowerCase().contains("dtlz4")) {
            // DTLZ2, DTLZ3 and DTLZ4
            VirtualIndividual[] refSet = new VirtualIndividual[pop.length];
            for (int i = 0; i < pop.length; i++) {
                double[] v = new double[pop[i].getObjectivesCount()];
                for (int j = 0; j < pop[i].getObjectivesCount(); j++) {
                    v[j] = pop[i].getObjective(j);
                }
                double norm = Mathematics.getNorm(v);
                refSet[i] = new VirtualIndividual(objCount);
                for (int j = 0; j < pop[i].getObjectivesCount(); j++) {
                    refSet[i].setObjective(j, pop[i].getObjective(j) / norm);
                }
            }
            // Calculate and return GD
            return PerformanceMetrics.calculateGenerationalDistance(objCount, pop, refSet, 2);
        } else {
            // Retrieve Pareto front (objective space)
            List<VirtualIndividual> refSetList = new ArrayList<>();
            try (BufferedReader reader = new BufferedReader(new FileReader(refSetFile))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] splits = line.split("\\s+");
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
    }

    private static double calculateIGD(File popFile, File refSetFile, int objCount) throws IOException {
        // Create Empty list
        List<VirtualIndividual> popList = new ArrayList<>();
        List<VirtualIndividual> refSetList = new ArrayList<>();
        // Retrieve population (objective space)
        try (BufferedReader reader = new BufferedReader(new FileReader(popFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] splits = line.split("\\s+");
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
                String[] splits = line.split("\\s+");
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

    private static void writeToFile(
            List<FunEvalMetricPair> feMetricPairs,
            File outFile) throws FileNotFoundException {
        PrintWriter printer = null;
        try {
            printer = new PrintWriter(outFile);
            for (FunEvalMetricPair feMetricPair : feMetricPairs) {
                printer.print(String.format(
                        "%06d %7.6f%n",
                        feMetricPair.getFunEval(),
                        feMetricPair.getMetric()));
            }
        } finally {
            if (printer != null) {
                printer.close();
            }
        }
    }

    private static void appendToAdditionalInfo(
            String algorithmName,
            String key,
            String value) throws IOException {
        File additionalInfoFile = new File(
                getEngineOutputDir(optimizationProblem, algorithmName).getPath()
                + File.separator + "additional_info.txt");
        PrintWriter printer = null;
        try {
            printer = new PrintWriter(new FileWriter(additionalInfoFile, true));
            printer.format("%s = %s%n", key, value);
        } finally {
            if (printer != null) {
                printer.close();
            }
        }
    }

    private static void writeToFile(double[] arr, File file) throws IOException {
        PrintWriter printer = null;
        try {
            printer = new PrintWriter(file);
            for (double num : arr) {
                printer.println(num);
            }
        } finally {
            if (printer != null) {
                printer.close();
            }
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

    private static List<FunEvalMetricPair> _collectFEvsMetric(
            int objCount,
            String algorithmName,
            int metricType,
            int interval) throws IOException {
        List<Integer> initialPopFeList = new ArrayList<>();
        List<Double> initialPopMetricList = new ArrayList<>();
//        int initialPopFeSum = 0;
//        double initialPopulationMetric = 0;
        // A list to store all FunEval/Metric pairs from all generations of all runs.
        List<FunEvalMetricPair> funEvalMetricList = new ArrayList<>();
        // Loop over all runs
        for (int runIndex = 0; runIndex < /*3*/ runCount; runIndex++) {
            // Ge the directory of the current run
            File runOutputDir = getRunOutputDir(getEngineOutputDir(optimizationProblem, algorithmName), runIndex);
            //File runOutputDir = getRunOutputDir(new File("F:/IEEE-TEVC-DC-NSGA-III/Results/temp/temp_zdt6"), runIndex);
            // Get all the meta files of the current run
            File[] metaFiles = runOutputDir.listFiles(new FileNamePrefixFilter("meta"));
            // Get all the objective space files of the current run
            File[] objFiles = runOutputDir.listFiles(new FileNamePrefixFilter("obj"));
            // Get all the KKTPM files of the current run
            File[] kktpmFiles = runOutputDir.listFiles(new FileNamePrefixFilter("kkt"));
            for (int i = 0; i < objFiles.length; i++) {
                // Estract the number of function evaluation from each meta file.
                int fe = getFE(metaFiles[i]);
                // Extract the curresponding objective space metric
                double metric = Double.NaN;
                switch (metricType) {
                    case GD:
                        metric = calculateGD(objFiles[i], refSetFile, objCount);
                        break;
                    case IGD:
                        metric = calculateIGD(objFiles[i], refSetFile, objCount);
                        break;
                    case HV:
                        metric = calculateHV(objFiles[i], idealPoint, refPoint);
                        break;
                    case KKTPM:
                        if (targetKktpmState == COLLECT_MEDIAN_KKTPM) {
                            metric = getMedianKKTPM(kktpmFiles[i]);
                        } else if (targetKktpmState == COLLECT_BEST_KKTPM) {
                            metric = getBestKKTPM(kktpmFiles[i]);
                        }
                    default:
                        break;
                }
                if (i == 0) {
                    initialPopFeList.add(fe);
                    initialPopMetricList.add(metric);
//                    initialPopFeSum += fe;
//                    initialPopulationMetric += metric;
                } else {
                    // Create a new entry
                    FunEvalMetricPair entry = new FunEvalMetricPair(fe, metric);
                    // Add notes
                    entry.setNotes(String.format("Run(%d) - Gen(%d)", runIndex, i));
                    // Add the new pair to the list
                    funEvalMetricList.add(entry);
                }
            }
        }
        // Sort the list ascendingly by FE
        Collections.sort(funEvalMetricList);
//        // Display
//        System.out.println("* All Entries");
//        int j = 0;
//        for (FunEvalMetricPair funEvalMetricPair : funEvalMetricList) {
//            System.out.format("%2d) %05d %5.4f : %s%n",
//                    j++,
//                    funEvalMetricPair.getFunEval(),
//                    funEvalMetricPair.getMetric(),
//                    funEvalMetricPair.getNotes());
//        }
        // A list to store metric average values of each interval
        List<FunEvalMetricPair> avgFunEvalMetricList = new ArrayList<>();
        // Record the first population
        int[] initialPopFeArr = new int[initialPopFeList.size()];
        double[] initialPopMetricArr = new double[initialPopFeList.size()];
        for (int i = 0; i < initialPopFeList.size(); i++) {
            initialPopFeArr[i] = initialPopFeList.get(i);
            initialPopMetricArr[i] = initialPopMetricList.get(i);
        }
//        avgFunEvalMetricList.add(
//                new FunEvalMetricPair(
//                        initialPopFeSum / runCount,
//                        initialPopulationMetric / runCount));
        avgFunEvalMetricList.add(
                new FunEvalMetricPair(
                        Mathematics.getMedian(initialPopFeArr),
                        Mathematics.getMedian(initialPopMetricArr)));
        // Record the rest of generations (after initial population)
        int i = 0;
        int count = 0;
        List<Integer> funEvalList = new ArrayList<>();
        List<Double> metricList = new ArrayList<>();
//        int funEvalSum = 0;
//        double metricSum = 0;
        int k = 1;
        while (i < funEvalMetricList.size()) {
            if (funEvalMetricList.get(i).getFunEval() < interval * k) {
                funEvalList.add(funEvalMetricList.get(i).getFunEval());
                metricList.add(funEvalMetricList.get(i).getMetric());
//                funEvalSum += funEvalMetricList.get(i).getFunEval();
//                metricSum += funEvalMetricList.get(i).getMetric();
                count++;
                i++;
            } else {
                if (count != 0) {
                    int[] funEvalArr = new int[funEvalList.size()];
                    double[] metricArr = new double[funEvalList.size()];
                    for (int j = 0; j < funEvalList.size(); j++) {
                        funEvalArr[j] = funEvalList.get(j);
                        metricArr[j] = metricList.get(j);
                    }
                    avgFunEvalMetricList.add(new FunEvalMetricPair(
                            Mathematics.getMedian(funEvalArr),
                            Mathematics.getMedian(metricArr)));
//                    avgFunEvalMetricList.add(new FunEvalMetricPair(
//                            funEvalSum / count,
//                            metricSum / count));
                    funEvalList.clear();
                    metricList.clear();
//                    funEvalSum = 0;
//                    metricSum = 0;
                    count = 0;
                }
                k++;
            }
        }
        if (count != 0) {
            int[] funEvalArr = new int[funEvalList.size()];
            double[] metricArr = new double[funEvalList.size()];
            for (int j = 0; j < funEvalList.size(); j++) {
                funEvalArr[j] = funEvalList.get(j);
                metricArr[j] = metricList.get(j);
            }
            avgFunEvalMetricList.add(new FunEvalMetricPair(
                    Mathematics.getMedian(funEvalArr),
                    Mathematics.getMedian(metricArr)));
//            avgFunEvalMetricList.add(new FunEvalMetricPair(
//                    funEvalSum / count,
//                    metricSum / count));
        }
//        // Display
//        System.out.println("* Averages Entries");
//        for (FunEvalMetricPair avgFunEvalMetricPair : avgFunEvalMetricList) {
//            System.out.format("%05d %5.4f%n", avgFunEvalMetricPair.getFunEval(), avgFunEvalMetricPair.getMetric());
//        }
        return avgFunEvalMetricList;
    }

    private static class FunEvalMetricPair implements Comparable<FunEvalMetricPair> {

        private final int funEval;
        private final double metric;
        private String notes;

        public FunEvalMetricPair(int funEval, double metric) {
            this.funEval = funEval;
            this.metric = metric;
        }

        /**
         * @return the funEval
         */
        public int getFunEval() {
            return funEval;
        }

        /**
         * @return the metric
         */
        public double getMetric() {
            return metric;
        }

        @Override
        public int compareTo(FunEvalMetricPair other) {
            //ascending order
            return this.funEval - other.funEval;
        }

        /**
         * @return the notes
         */
        public String getNotes() {
            return notes;
        }

        /**
         * @param notes the notes to set
         */
        public void setNotes(String notes) {
            this.notes = notes;
        }

        @Override
        public String toString() {
            return String.format("%05d %5.4f", this.funEval, this.metric);
        }
    }
}
