/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package optimization;

import utils.GeneralUtilities;
import utils.InputOutput;
import utils.PerformanceMetrics;
import utils.RandomNumberGenerator;
import com.mathworks.toolbox.javabuilder.MWException;
import emo.DoubleAssignmentException;
import emo.Individual;
import emo.OptimizationProblem;
import emo.VirtualIndividual;
import engines.AbstractGeneticEngine;
import asf.LocalSearch;
import engines.NSGA3Engine;
import evaluators.OSYEvaluator;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import javax.xml.stream.XMLStreamException;
import kkt.KKT_Calculator;
import asf.ASF_Minimizer;
import asf.BNH_ASF_Minimizer;
import asf.DTLZ1_ASF_Minimizer;
import asf.DTLZ2_ASF_Minimizer;
import asf.DTLZ7_ASF_Minimizer;
import asf.DTLZ8_ASF_Minimizer;
import asf.DTLZ9_ASF_Minimizer;
import asf.OSY_ASF_Minimizer;
import asf.SRN_ASF_Minimizer;
import asf.TNK_ASF_Minimizer;
import asf.ZDT1_ASF_Minimizer;
import asf.ZDT2_ASF_Minimizer;
import asf.ZDT3_ASF_Minimizer;
import asf.ZDT4_ASF_Minimizer;
import asf.ZDT6_ASF_Minimizer;
import asf.smooth.SmoothDtlz1AsfMinimizer;
import asf.smooth.SmoothDtlz2AsfMinimizer;
import asf.smooth.SmoothDtlz3AsfMinimizer;
import asf.smooth.SmoothDtlz4AsfMinimizer;
import asf.smooth.SmoothDtlz5AsfMinimizer;
import asf.smooth.SmoothDtlz6AsfMinimizer;
import asf.smooth.SmoothDtlz7AsfMinimizer;
import asf.smooth.SmoothDtlz8AsfMinimizer;
import asf.smooth.SmoothDtlz9AsfMinimizer;
import asf.smooth.SmoothZdt1AsfMinimizer;
import engines.NSGA2Engine;
import engines.NSGA3ConvergenceEngine;
import engines.NSGA3_ExtremeLocalSearch;
import engines.NSGA3_Diversity_GECCO2016;
import engines.NSGA3_DC_EMO2017;
import engines.ParameterlessUnsga3Engine;
import engines.UnifiedNSGA3Engine;
import evaluators.BNHEvaluator;
import evaluators.GeneralDTLZ1Evaluator;
import evaluators.GeneralDTLZ2Evaluator;
import evaluators.GeneralDTLZ3Evaluator;
import evaluators.GeneralDTLZ4Evaluator;
import evaluators.GeneralDTLZ5Evaluator;
import evaluators.GeneralDTLZ6Evaluator;
import evaluators.GeneralDTLZ7Evaluator;
import evaluators.GeneralDTLZ8Evaluator;
import evaluators.GeneralDTLZ9Evaluator;
import evaluators.SRNEvaluator;
import evaluators.TNKEvaluator;
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
import extremels.ExtremeLocalSearch;
import extremels.OsyExtremeLocalSearch;
import extremels.SrnExtremeLocalSearch;
import extremels.TnkExtremeLocalSearch;
import extremels.Zdt1ExtremeLocalSearch;
import extremels.Zdt2ExtremeLocalSearch;
import extremels.Zdt3ExtremeLocalSearch;
import extremels.Zdt4ExtremeLocalSearch;
import extremels.Zdt6ExtremeLocalSearch;
import java.io.PrintWriter;
import java.util.Date;
import kkt.xmlbased.BnhKKTPMCalculator;
import kkt.xmlbased.Dtlz1KKTPMCalculator_05obj;
import kkt.xmlbased.Dtlz2KKTPMCalculator_05obj;
import kkt.xmlbased.OsyKKTPMCalculator;
import kkt.xmlbased.SrnKKTPMCalculator;
import kkt.xmlbased.TnkKKTPMCalculator;
import parsing.IndividualEvaluator;
import parsing.InvalidOptimizationProblemException;
import parsing.StaXParser;

/**
 *
 * @author toshiba
 */
public class TestScript_FixedGenerations extends TestScript {

    // According to the values of the following flags the script calculates
    // Hypervolume(for two objectives only), IGD and GD.
    static boolean calculateHV = false;
    static boolean calculateGD = false;
    static boolean calculateIGD = false;
    // Number of auto generated Pareto points (used to calculate GD and/or IGD)
    static int paretoOptimalFrontPointsCount = 10000;
    // Number of runs performed to take averages
    public static int runsCount = 1;
    public static int currentRunIndex = 0;

    /**
     * @param args the command line arguments
     * @throws javax.xml.stream.XMLStreamException
     * @throws parsing.InvalidOptimizationProblemException
     * @throws net.sourceforge.jeval.EvaluationException
     * @throws java.io.IOException
     * @throws emo.DoubleAssignmentException
     */
    public static void main(String[] args)
            throws
            XMLStreamException,
            InvalidOptimizationProblemException,
            IOException,
            DoubleAssignmentException,
            MWException {
        InputStream in = null;
        try {
            // Set you epsilon for epsilon-domination
            double epsilon = 0.0;
            // ********** MODIFY ********** MODIFY ********** MODIFY **********
            // *                         MODIFY START                         *
            // ********** MODIFY ********** MODIFY ********** MODIFY **********
            // Read Problem
            URL url = NSGA3Engine.class.getResource("../samples/uf2-30.xml");
            in = url.openStream();
            OptimizationProblem optimizationProblem = StaXParser.readProblem(in);
            // Create Evaluator
            //IndividualEvaluator individualEvaluator = new WFG1(2, optimizationProblem.getRealVariablesCount() - 2, optimizationProblem.objectives.length);
            //IndividualEvaluator individualEvaluator = new WFG1(2,2,3);
            IndividualEvaluator individualEvaluator = new UF2Evaluator();
            // -----------------------------------------------------------------
            // Overriding parameters in the XML problem definition (for testing purposes) - START
            // etaC Parameter
            int etaCStart = 30;
            int etaCEnd = 30;
            // etaM Parameter
            int etaMStart = 20;
            int etaMEnd = 20;
            // Population Parameter
            int popSizeStart = 92;
            int popSizeEnd = 92;
            // Generations Parameter
            int genCountStart = 2000;
            int genCountEnd = 2000;
            // Parameters Steps
            int etaCStep = 10;
            int etaMStep = 10;
            int popSizeStep = 10;
            int genCountStep = 10;
            // Overriding parameters in the XML problem definition (for testing purposes) - END
            // -----------------------------------------------------------------
            // Create the ASF minimizer if you intend to do ASF-based local search
            ASF_Minimizer asfMinimizer = null;
            //ASF_Minimizer asfMinimizer = new NonSmoothZdt3AsfMinimizer();
            //ASF_Minimizer asfMinimizer = new SmoothZdt1AsfMinimizer();
            //ASF_Minimizer asfMinimizer = new ZDT1_ASF_Minimizer();
            //ASF_Minimizer asfMinimizer = new SmoothDtlz9AsfMinimizer();
            //ASF_Minimizer asfMinimizer = new BNH_ASF_Minimizer();
            //ASF_Minimizer asfMinimizer = new OSY_ASF_Minimizer();
            //ASF_Minimizer asfMinimizer = new TNK_ASF_Minimizer();
            //ASF_Minimizer asfMinimizer = new SRN_ASF_Minimizer();
            //asfMinimizer = new DTLZ1_ASF_Minimizer();

            // Create a KKT calculator
            KKT_Calculator kktCalculator = null;
            //KKT_Calculator kktCalculator = new BnhKKTPMCalculator();
            //KKT_Calculator kktCalculator = new OsyKKTPMCalculator();
            //KKT_Calculator kktCalculator = new TnkKKTPMCalculator();
            //KKT_Calculator kktCalculator = new SrnKKTPMCalculator();
            //new ZDT1_KKT_Calculator_Direct();
            //kktCalculator = new Dtlz1KKTPMCalculator_05obj();

            // Create the appropriate extreme point local search object
            ExtremeLocalSearch extLS = null;
            //ExtremeLocalSearch extLS = new Zdt3ExtremeLocalSearch();
            //ExtremeLocalSearch extLS = new BnhExtremeLocalSearch();
            //ExtremeLocalSearch extLS = new OsyExtremeLocalSearch();
            //ExtremeLocalSearch extLS = new TnkExtremeLocalSearch();
            //ExtremeLocalSearch extLS = new SrnExtremeLocalSearch();
            //extLS = new Dtlz1ExtremeLocalSearch();

            // Uncomment the following line only if you need the scaled version of the problem (Scaling is supported only for DTLZ1 and DTLZ2 until now(30Oct.2014))
            //((GeneralDTLZ1Evaluator)individualEvaluator).setScaled(true);
            double seed = 0.7;
            RandomNumberGenerator.setSeed(seed);
            // ********** MODIFY ********** MODIFY ********** MODIFY **********
            // *                          MODIFY END                          *
            // ********** MODIFY ********** MODIFY ********** MODIFY **********
            VirtualIndividual[] paretoFront = null;
            if (calculateGD || calculateIGD) {
                System.out.format("* Generating reference Pareto front...%n");
                paretoFront = individualEvaluator.getParetoFront(
                        optimizationProblem.objectives.length,
                        paretoOptimalFrontPointsCount);
                System.out.format("%d Pareto points generated.%n", paretoFront.length);
            }
            double[] REFERENCE_POINT = null, IDEAL_POINT = null;
            if (calculateHV) {
                IDEAL_POINT = individualEvaluator.getIdealPoint();
                REFERENCE_POINT = individualEvaluator.getReferencePoint();
                // Introduce marginal displacements in ref. point position (used to
                // calculate HV) away from Nadir point position in both directions.
                for (int i = 0; i < REFERENCE_POINT.length; i++) {
                    REFERENCE_POINT[i] += REFERENCE_POINT[i] * 0.01;
                }
            }
            //List<String> hypervolumesList = new ArrayList<String>();
            for (int etaC = etaCStart; etaC <= etaCEnd; etaC += etaCStep) {
                optimizationProblem.setRealCrossoverDistIndex(etaC);
                for (int etaM = etaMStart; etaM <= etaMEnd; etaM += etaMStep) {
                    optimizationProblem.setRealMutationDistIndex(etaM);
                    for (int popSize = popSizeStart; popSize <= popSizeEnd; popSize += popSizeStep) {
                        optimizationProblem.setPopulationSize(popSize);
                        // The following line is very important. It overrides the number of steps in
                        // order to guarantee a number of directions equal to the population size
                        // ONLY IN TWO OBJECTIVES.. ONLY IN TWO OBJECTIVES.. ONLY IN TWO OBJECTIVES..
                        if (optimizationProblem.objectives.length == 2) {
                            optimizationProblem.setSteps(popSize - 1);
                        }
                        for (int genCount = genCountStart; genCount <= genCountEnd; genCount += genCountStep) {
                            optimizationProblem.setGenerationsCount(genCount);

                            // ********** MODIFY ********** MODIFY ********** MODIFY **********
                            // *                          MODIFY START                        *
                            // ********** MODIFY ********** MODIFY ********** MODIFY **********
                            // Create the engine
                            //AbstractGeneticEngine geneticEngine = new NSGA2Engine(optimizationProblem, individualEvaluator);
                            //AbstractGeneticEngine geneticEngine = new NSGA3Engine(optimizationProblem, individualEvaluator);
                            //AbstractGeneticEngine geneticEngine = new UnifiedNSGA3Engine(optimizationProblem, individualEvaluator, new int[]{3, 2, 1});
                            //AbstractGeneticEngine geneticEngine = new ParameterlessUnsga3Engine(optimizationProblem, individualEvaluator);
                            AbstractGeneticEngine geneticEngine = new UnifiedNSGA3Engine(optimizationProblem, individualEvaluator);

                            //AbstractGeneticEngine geneticEngine = new NSGA3ConvergenceEngine(optimizationProblem, individualEvaluator);
                            //AbstractGeneticEngine geneticEngine = new NSGA3DiversityEngine(optimizationProblem, individualEvaluator); // AN EXCEPTION IS THROWN WITH CONSTRAINED PROBLEMS
                            //AbstractGeneticEngine geneticEngine = new NSGA3EngineFinalGenLS(optimizationProblem, individualEvaluator);
                            //AbstractGeneticEngine geneticEngine = new NSGA3DiversityEngine2(optimizationProblem, individualEvaluator);
                            //AbstractGeneticEngine geneticEngine = new NSGA3DiversityEngine3(optimizationProblem, individualEvaluator);
                            //AbstractGeneticEngine geneticEngine = new NSGA3DiversityEngine_29DEC(optimizationProblem, individualEvaluator, new File(outDir));
                            //AbstractGeneticEngine geneticEngine = new NSGA3_ExtremeLocalSearch(optimizationProblem, individualEvaluator, extLS);
                            //AbstractGeneticEngine geneticEngine = new NSGA3_Diversity_GECCO2016(optimizationProblem, individualEvaluator, extLS);
                            //AbstractGeneticEngine geneticEngine = new NSGA3_DC_EMO2017(optimizationProblem, individualEvaluator, extLS);
                            //int[] divisions = {3,2};
                            //AbstractGeneticEngine geneticEngine = new NSGA3SearchEngine(optimizationProblem, individualEvaluator, divisions);
                            //AbstractGeneticEngine geneticEngine = new NSGA3Engine(optimizationProblem, individualEvaluator, "D:/Extra/R2.ref");
                            // ********** MODIFY ********** MODIFY ********** MODIFY **********
                            // *                          MODIFY END                          *
                            // ********** MODIFY ********** MODIFY ********** MODIFY **********
                            File outputDir = new File(topOutDir + File.separator + String.format("%s-%03d-%03d-P%04d-G%04d/%s/",
                                    optimizationProblem.getProblemID(),
                                    optimizationProblem.getRealCrossoverDistIndex(),
                                    optimizationProblem.getRealMutationDistIndex(),
                                    optimizationProblem.getPopulationSize(),
                                    optimizationProblem.getGenerationsCount(),
                                    geneticEngine.getAlgorithmName()));
                            // Make directories
                            if (!outputDir.exists()) {
                                outputDir.mkdirs();
                            }
                            // perform several runs (runsCount) and collect metrics
                            double[] hyperVolume = null, gd = null, igd = null;
                            if (calculateHV) {
                                hyperVolume = new double[runsCount];
                            }
                            if (calculateGD) {
                                gd = new double[runsCount];
                            }
                            if (calculateIGD) {
                                igd = new double[runsCount];
                            }
                            int totalEvaluationsCount = 0;
                            for (int runIndex = 0; runIndex < runsCount; runIndex++) {
                                System.out.println("*****************");
                                System.out.format("    Run(%03d)  %n", runIndex);
                                System.out.println("*****************");
                                // Create output directory of this specific run
                                File runOutputDir = new File(outputDir.getPath() + File.separator + String.format("run%03d/", runIndex));
                                if (!runOutputDir.exists()) {
                                    runOutputDir.mkdir();
                                }
                                // Start the engine
                                //Individual[] finalPopulation = geneticEngine.start(outputDir, runIndex, epsilon, asfMinimizer, kktCalculator);
                                long startTime = new Date().getTime();
                                Individual[] finalPopulation = geneticEngine.start(runOutputDir, runIndex, epsilon, asfMinimizer, kktCalculator, Double.MAX_VALUE, Integer.MAX_VALUE);
                                long endTime = new Date().getTime();
                                System.out.println("Time = " + (endTime-startTime)/1000.0);
                                System.out.println("*** EVAL COUNT = " + individualEvaluator.getFunctionEvaluationsCount());
                                totalEvaluationsCount += individualEvaluator.getFunctionEvaluationsCount();
                                System.out.println("Infeasible solutions reached = " + LocalSearch.infeasibleSolutionsReachedCount);
                                LocalSearch.infeasibleSolutionsReachedCount = 0; // Reset counter
                                System.out.println("Same solutions reached = " + LocalSearch.sameSolutionsReachedCount);
                                LocalSearch.sameSolutionsReachedCount = 0; // Reset counter
                                for (int i = 0; i < LocalSearch.startsOfSuccessfulLocalSearches.size(); i++) {
                                    System.out.format("      Gen.: %d%n", LocalSearch.generationsOfSuccessfulLocalSearches.get(i));
                                    System.out.format("Start Ind.: %s%n", LocalSearch.startsOfSuccessfulLocalSearches.get(i));
                                    System.out.format("  End Ind.: %s%n%n", LocalSearch.endsOfSuccessfulLocalSearches.get(i));
                                }
                                System.out.println("First Bounding Direction Selected: " + LocalSearch.firstBoundingDirSelectionCount);
                                System.out.println("Second Bounding Direction Selected: " + LocalSearch.secondBoundingDirSelectionCount);

                                // Reset and clear statistics collection objects
                                LocalSearch.firstBoundingDirSelectionCount = 0;
                                LocalSearch.secondBoundingDirSelectionCount = 0;
                                LocalSearch.generationsOfSuccessfulLocalSearches.clear();
                                LocalSearch.startsOfSuccessfulLocalSearches.clear();
                                LocalSearch.endsOfSuccessfulLocalSearches.clear();
                                // Generate output data file
                                String dataFileName = String.format(outputDir.getPath() + File.separator + "%s-G%03d-P%03d-run%03d-data.dat",
                                        optimizationProblem.getProblemID(),
                                        optimizationProblem.getGenerationsCount(),
                                        optimizationProblem.getPopulationSize(),
                                        runIndex);
                                InputOutput.dumpPopulation("Final Population",
                                        optimizationProblem,
                                        finalPopulation,
                                        dataFileName);
                                if (optimizationProblem.objectives.length == 2 || optimizationProblem.objectives.length == 3) {
                                    // Prepare Matlab script file name
                                    String matlabScriptFilePath
                                            = String.format("Matlab_%s_G%03d_P%03d_run%03d_data.m",
                                                    optimizationProblem.getProblemID(),
                                                    optimizationProblem.getGenerationsCount(),
                                                    optimizationProblem.getPopulationSize(),
                                                    runIndex);
                                    matlabScriptFilePath = GeneralUtilities.replaceDashesWithUnderscores(matlabScriptFilePath);
                                    matlabScriptFilePath = outputDir.getPath() + File.separator + matlabScriptFilePath;
                                    // Dump the Matlab plotting script
                                    StringBuilder sb = null;
                                    if (optimizationProblem.objectives.length == 2) {
                                        sb = InputOutput.createMatlabScript2D(finalPopulation);
                                    } else if (optimizationProblem.objectives.length == 3) {
                                        sb = InputOutput.createMatlabScript3D(finalPopulation);
                                    }
                                    if (sb != null) {
                                        InputOutput.writeText2File(sb.toString(), new File(matlabScriptFilePath));
                                    }
                                }
                                // Reset the number of function evaluations to start
                                // counting from Zero again in the next iteration
                                individualEvaluator.resetFunctionEvaluationsCount();
                                // Hypervolume
                                if (optimizationProblem.objectives.length == 2) {
                                    if (calculateHV) {
                                        hyperVolume[runIndex]
                                                = PerformanceMetrics.calculateHyperVolumeForTwoObjectivesOnly(
                                                        geneticEngine,
                                                        finalPopulation,
                                                        REFERENCE_POINT,
                                                        IDEAL_POINT,
                                                        epsilon);
                                    }
                                }
                                // GD
                                if (calculateGD) {
                                    gd[runIndex] = PerformanceMetrics.calculateGenerationalDistance(
                                            optimizationProblem.objectives.length,
                                            finalPopulation,
                                            paretoFront,
                                            2);
                                }
                                // IGD
                                if (calculateIGD) {
                                    igd[runIndex] = PerformanceMetrics.calculateInvertedGenerationalDistance(
                                            optimizationProblem.objectives.length,
                                            finalPopulation,
                                            paretoFront,
                                            2);
                                }
                                // Increment run index
                                currentRunIndex++;
                            }
                            System.out.format("Total Evaluations Count = %d%n", totalEvaluationsCount);
                            System.out.format("Avg Evaluations Count(per run) = %d%n", (totalEvaluationsCount / runsCount));
                            // Dump function evaluations count
                            PrintWriter evalsPrinter = null;
                            try {
                                evalsPrinter = new PrintWriter(outputDir.getPath() + File.separator + "eval_count.txt");
                                evalsPrinter.format("Avg Evaluations Count(per run) = %d%n", (totalEvaluationsCount / runsCount));
                            } finally {
                                if (evalsPrinter != null) {
                                    evalsPrinter.close();
                                }
                            }
                            if (calculateHV || calculateGD || calculateIGD) {
                                String metricsFileName = String.format(outputDir + "%s-G%03d-P%03d-metrics.dat",
                                        optimizationProblem.getProblemID(),
                                        optimizationProblem.getGenerationsCount(),
                                        optimizationProblem.getPopulationSize());
                                InputOutput.dumpPerformanceMetrics(
                                        optimizationProblem,
                                        hyperVolume,
                                        gd,
                                        igd,
                                        metricsFileName);
                            }
                        }
                    }
                }
            }
        } finally {
            if (in != null) {
                in.close();
            }
        }
    }
}
