/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package optimization;

import utils.InputOutput;
import utils.PerformanceMetrics;
import utils.RandomNumberGenerator;
import utils.GeneralUtilities;
import com.mathworks.toolbox.javabuilder.MWException;
import emo.DoubleAssignmentException;
import emo.Individual;
import emo.OptimizationProblem;
import emo.VirtualIndividual;
import engines.AbstractGeneticEngine;
import engines.UnifiedNSGA3Engine;
import engines.NSGA2Engine;
import engines.NSGA3Engine;
import evaluators.BNHEvaluator;
import evaluators.OSYEvaluator;
import evaluators.SRNEvaluator;
import evaluators.TNKEvaluator;
import evaluators.ZDT3ModifiedEvaluator;
import evaluators.ZDT2Evaluator;
import evaluators.ZDT3Evaluator;
import evaluators.ZDT4Evaluator;
import evaluators.ZDT6Evaluator;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;
import javax.xml.stream.XMLStreamException;
import kkt.KKT_Calculator;
import asf.ASF_Minimizer;
import asf.OSY_ASF_Minimizer;
import parsing.IndividualEvaluator;
import parsing.InvalidOptimizationProblemException;
import parsing.StaXParser;

/**
 *
 * @author Haitham
 */
public class TestScript_FixedHypervolume extends TestScript {

    // Number of runs performed to take averages
    public static int runsCount = 31;
    public static int currectRunIndex = 0;
    // Target Hypervolume
    static double targetHV = 0.302;
    static int maxFuncEvaluations = 30000;

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
            // ********** MODIFY ********** MODIFY ********** MODIFY **********
            // *                         MODIFY START                         *
            // ********** MODIFY ********** MODIFY ********** MODIFY **********
            // Read Problem
            URL url = NSGA3Engine.class.getResource("../samples/tnk.xml");
            in = url.openStream();
            OptimizationProblem optimizationProblem = StaXParser.readProblem(in);
            // Create Evaluator
            IndividualEvaluator individualEvaluator = new TNKEvaluator();
            // Create the ASF minimizer for the local search used only if the value of localSearchUsage is not NO_LOCAL_SEARCH)
            ASF_Minimizer asfMinimizer = null;
            // Create a KKT calculator
            KKT_Calculator kktCalculator = null;
            // Set initial seed (overriding the one from the XML file)
            double seed = 0.2;
            RandomNumberGenerator.setSeed(seed);
            // ********** MODIFY ********** MODIFY ********** MODIFY **********
            // *                          MODIFY END                          *
            // ********** MODIFY ********** MODIFY ********** MODIFY **********
            double[] REFERENCE_POINT, IDEAL_POINT;
            // Calculate Hypervolume using the ideal point and the adjusted nadir point
            IDEAL_POINT = individualEvaluator.getIdealPoint();
            REFERENCE_POINT = individualEvaluator.getReferencePoint();
            // Introduce marginal displacements in ref. point position (used to
            // calculate HV) away from Nadir point position in both directions.
            for (int i = 0; i < REFERENCE_POINT.length; i++) {
                REFERENCE_POINT[i] += REFERENCE_POINT[i] * 0.01;
            }
            // Overriding parameters settings (for testing purposes)
            // etaC Parameter
            int etaCStart = 30;
            int etaCEnd = 30;
            // etaM Parameter
            int etaMStart = 20;
            int etaMEnd = 20;
            // Population Parameter
            int popSizeStart = 16;
            int popSizeEnd = 128;
            // Generations Parameter
            int genCountStart = 1000000; // Fixed HV (generations are no more a stopping criterion)
            int genCountEnd = 1000000; // Fixed HV (generations are no more a stopping criterion)
            // Parameters Steps
            int etaCStep = 30;
            int etaMStep = 30;
            int popSizeStep = 4;
            int genCountStep = 20;
            // Set you epsilon for epsilon-domination
            double epsilon = 0.0;

            for (int etaC = etaCStart; etaC <= etaCEnd; etaC += etaCStep) {
                optimizationProblem.setRealCrossoverDistIndex(etaC);
                for (int etaM = etaMStart; etaM <= etaMEnd; etaM += etaMStep) {
                    optimizationProblem.setRealMutationDistIndex(etaM);
                    for (int popSize = popSizeStart; popSize <= popSizeEnd; popSize += popSizeStep) {
                        optimizationProblem.setPopulationSize(popSize);
                        // The following line is very important. It overrides the number of steps in
                        // order to guarantee a number of directions equal to the population size
                        // ONLY IN TWO OBJECTIVES.. ONLY IN TWO OBJECTIVES.. ONLY IN TWO OBJECTIVES..
                        optimizationProblem.setSteps(15);
                        for (int genCount = genCountStart; genCount <= genCountEnd; genCount += genCountStep) {
                            optimizationProblem.setGenerationsCount(genCount);

                            // ********** MODIFY ********** MODIFY ********** MODIFY **********
                            // *                          MODIFY START                          *
                            // ********** MODIFY ********** MODIFY ********** MODIFY **********
                            AbstractGeneticEngine geneticEngine = new UnifiedNSGA3Engine(optimizationProblem, individualEvaluator);
                            //AbstractGeneticEngine geneticEngine = new NSGA3Engine(optimizationProblem, individualEvaluator, "D:/Extra/R2.ref");
                            // ********** MODIFY ********** MODIFY ********** MODIFY **********
                            // *                          MODIFY END                          *
                            // ********** MODIFY ********** MODIFY ********** MODIFY **********

                            File outputDir = new File(topOutDir.getPath() + File.separator + String.format("%s-%03d-%03d_P%03d_HV_%4.4f/%s/",
                                    optimizationProblem.getProblemID(),
                                    optimizationProblem.getRealCrossoverDistIndex(),
                                    optimizationProblem.getRealMutationDistIndex(),
                                    optimizationProblem.getPopulationSize(),
                                    targetHV,
                                    geneticEngine.getAlgorithmName()));
                            // Make directories
                            if (!outputDir.exists()) {
                                outputDir.mkdirs();
                            }
                            // perform several runs (runsCount) and collect number of individual evaluations
                            int[] individualEvaluations = new int[runsCount];
                            // perform several runs (runsCount) and collect number of individual evaluations
                            double[] hypervolumes = new double[runsCount];
                            for (int runIndex = 0; runIndex < runsCount; runIndex++) {
                                System.out.println("*****************");
                                System.out.format("    Run(%03d)  %n", runIndex);
                                System.out.println("*****************");
                                // Create a genetic engine for the problem
                                Individual[] finalPopulation = geneticEngine.start(outputDir, runIndex, epsilon, /*localSearchUsage, */asfMinimizer, kktCalculator, targetHV, maxFuncEvaluations);
                                currectRunIndex++;
                                // Generate output data file
                                String dataFileName = String.format(outputDir + "%s-G%03d-P%03d-run%03d-data.dat",
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
                                    matlabScriptFilePath = outputDir + matlabScriptFilePath;
                                    // Dump the Matlab plotting script
                                    StringBuilder sb = null;
                                    if (optimizationProblem.objectives.length == 2) {
                                        sb = InputOutput.createMatlabScript2D(finalPopulation);
                                    } else if (optimizationProblem.objectives.length == 3) {
                                        sb = InputOutput.createMatlabScript3D(finalPopulation);
                                    }
                                    if(sb != null) {
                                        InputOutput.writeText2File(sb.toString(), new File(matlabScriptFilePath));
                                    }
                                }
                                // Store function evaluations
                                individualEvaluations[runIndex] = individualEvaluator.getFunctionEvaluationsCount();
                                // Reset the number of function evaluations to start
                                // counting from Zero again in the next iteration
                                individualEvaluator.resetFunctionEvaluationsCount();
                                // Store hypervolume
                                if (optimizationProblem.objectives.length == 2) {
                                    hypervolumes[runIndex]
                                            = PerformanceMetrics.calculateHyperVolumeForTwoObjectivesOnly(
                                                    geneticEngine,
                                                    finalPopulation,
                                                    REFERENCE_POINT,
                                                    IDEAL_POINT,
                                                    epsilon);
                                } else {
                                    throw new UnsupportedOperationException(
                                            "The current implementation cannot "
                                            + "calculate hypervolume for "
                                            + "more than 2 dimensions.");
                                }
                            }
                            // Dump your report
                            String funcEvalFileName = String.format(outputDir + "%s-P%03d-evaluations.dat",
                                    optimizationProblem.getProblemID(),
                                    optimizationProblem.getPopulationSize());
                            InputOutput.dumpFunctionEvaluations(
                                    optimizationProblem,
                                    individualEvaluations,
                                    hypervolumes,
                                    funcEvalFileName);
                            System.out.println(Arrays.toString(individualEvaluations));
                        }
                    }
                }
            }

            System.out.println(
                    "Selection Modification Percentage = "
                    + (double) UnifiedNSGA3Engine.bothFeasibleCount / UnifiedNSGA3Engine.totalSelectionsCount);
        } finally {
            if (in != null) {
                in.close();
            }
        }
    }

}
