/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package optimization;

import utils.InputOutput;
import utils.RandomNumberGenerator;
import com.mathworks.toolbox.javabuilder.MWException;
import emo.DoubleAssignmentException;
import emo.Individual;
import emo.OptimizationProblem;
import engines.AbstractGeneticEngine;
import static engines.AbstractGeneticEngine.DUMP_ALL_GENERATIONS_OBJECTIVE_SPACE;
import engines.UnifiedNSGA3Engine;
import engines.NSGA2Engine;
import engines.NSGA3Engine;
import evaluators.SampleOneObjectiveEvaluator;
import evaluators.SingleObjective_Ackley_Evaluator;
import evaluators.SingleObjective_Ellipsoidal_Evaluator;
import evaluators.SingleObjective_G01_Evaluator;
import evaluators.SingleObjective_G02_Evaluator;
import evaluators.SingleObjective_G04_Evaluator;
import evaluators.SingleObjective_G06_Evaluator;
import evaluators.SingleObjective_G07_Evaluator;
import evaluators.SingleObjective_G08_Evaluator;
import evaluators.SingleObjective_G09_Evaluator;
import evaluators.SingleObjective_G10_Evaluator;
import evaluators.SingleObjective_G18_Evaluator;
import evaluators.SingleObjective_G24_Evaluator;
import evaluators.SingleObjective_Griewank_Evaluator;
import evaluators.SingleObjective_Rastrigin_Evaluator;
import evaluators.SingleObjective_Rosenbrock_Evaluator;
import evaluators.SingleObjective_Schwefel_Evaluator;
import evaluators.SingleObjective_Shubert_Evaluator;
import evaluators.SingleObjective_Thompson_Evaluator;
import evaluators.SingleObjective_Zakharov_Evaluator;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import javax.xml.stream.XMLStreamException;
import parsing.IndividualEvaluator;
import parsing.InvalidOptimizationProblemException;
import parsing.StaXParser;

/**
 *
 * @author toshiba
 */
public class TestScript_SingleObjective extends TestScript {

    // Number of runs performed to take averages
    public static int runsCount = 31;
    public static int currectRunIndex = 0;

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
            URL url = NSGA3Engine.class.getResource("../samples/single-objective-rastrigin.xml");
            in = url.openStream();
            OptimizationProblem optimizationProblem = StaXParser.readProblem(in);
            // Create Evaluator
            IndividualEvaluator individualEvaluator = new SingleObjective_Rastrigin_Evaluator();
            AbstractGeneticEngine geneticEngine;
            //geneticEngine = new NSGA3Engine(optimizationProblem, individualEvaluator);
            geneticEngine = new UnifiedNSGA3Engine(optimizationProblem, individualEvaluator);
            double seed = 0.2;
            RandomNumberGenerator.setSeed(seed);
            // ********** MODIFY ********** MODIFY ********** MODIFY **********
            // *                          MODIFY END                          *
            // ********** MODIFY ********** MODIFY ********** MODIFY **********

            // Overriding parameters settings (for testing purposes)
            // etaC Parameter
            int etaCStart = 0;
            int etaCEnd = 0;
            // etaM Parameter
            int etaMStart = 20;
            int etaMEnd = 20;
            // Population Parameter
            int popSizeStart = 100;
            int popSizeEnd = 100;
            // Generations Parameter
            int genCountStart = 500;
            int genCountEnd = 500;
            // Parameters Steps
            int etaCStep = 10;
            int etaMStep = 10;
            int popSizeStep = 100;
            int genCountStep = 100;
            // Set you epsilon for epsilon-domination
            double epsilon = 0.0; // VERY IMPORTANT NOTE: A NON-ZERO EPSILON VALUE DISTURBS THE RESULTS GREATLY IN SINGLE-OBJECTIVE PROBLEMS

            //List<String> hypervolumesList = new ArrayList<String>();
            for (int etaC = etaCStart; etaC <= etaCEnd; etaC += etaCStep) {
                optimizationProblem.setRealCrossoverDistIndex(etaC);
                for (int etaM = etaMStart; etaM <= etaMEnd; etaM += etaMStep) {
                    optimizationProblem.setRealMutationDistIndex(etaM);

                    for (int popSize = popSizeStart; popSize <= popSizeEnd; popSize += popSizeStep) {
                        optimizationProblem.setPopulationSize(popSize);
                        for (int genCount = genCountStart; genCount <= genCountEnd; genCount += genCountStep) {
                            optimizationProblem.setGenerationsCount(genCount);

                            // Make directories
                            File outputDir = new File(topOutDir.getPath() + File.separator + String.format("%s-%03d-%03d-P%04d-G%04d/%s/",
                                    optimizationProblem.getProblemID(),
                                    optimizationProblem.getRealCrossoverDistIndex(),
                                    optimizationProblem.getRealMutationDistIndex(),
                                    optimizationProblem.getPopulationSize(),
                                    optimizationProblem.getGenerationsCount(),
                                    geneticEngine.getAlgorithmName()
                            ));
                            // Make directories
                            if (!outputDir.exists()) {
                                outputDir.mkdirs();
                            }

                            /*
                             String dirPath = String.format("D:/Dropbox/Work/NSGA/results/%s-%03d-%03d/%s/",
                             optimizationProblem.getProblemID(),
                             optimizationProblem.getRealCrossoverDistIndex(),
                             optimizationProblem.getRealMutationDistIndex(),
                             geneticEngine.getAlgorithmName());
                             File dirs = new File(dirPath);
                             if (!dirs.exists()) {
                             dirs.mkdirs();
                             }
                             */
                            // Create an array to store the best objective value at each run
                            double[] objValues = new double[runsCount];
                            // Loop for (runCount)
                            for (int runIndex = 0; runIndex < runsCount; runIndex++) {
                                System.out.println("*****************");
                                System.out.format("    Run(%03d)  %n", runIndex);
                                System.out.println("*****************");
                                // Create a genetic engine for the problem
                                Individual[] finalPopulation
                                        = geneticEngine.start(
                                                outputDir,
                                                runIndex,
                                                epsilon,
                                                /*localSearchUsage,*/
                                                null /* No ASF in single-objective problems */,
                                                null /* No KKT in single-objective problems */);

//                                // TO-BE-REMOVED-START (ONLY FOR THOMPSON PROBLEM)
//                                // Extract coordinates
//                                int m = 3;
//                                double[][] coordinates = new double[finalPopulation[0].real.length / m][m];
//                                int counter = 0;
//                                for (int i = 0; i < finalPopulation[0].real.length; i += m) {
//                                    double norm = SingleObjective_Thompson_Evaluator.norm(finalPopulation[0].real, i, i + m - 1);
//                                    for (int j = 0; j < m; j++) {
//                                        coordinates[counter][j] = finalPopulation[0].real[i + j] / norm;
//                                    }
//                                    counter++;
//                                }
//                                // Display coordinates
//                                for (int i = 0; i < m; i++) {
//                                    System.out.format("C%02d = [", i);
//                                    for (int j = 0; j < finalPopulation[0].real.length / m; j++) {
//                                        System.out.format("%05.3f ", coordinates[j][i]);
//                                    }
//                                    System.out.println("];");
//                                }
//                                // TO-BE-REMOVED-END (ONLY FOR THOMPSON PROBLEM)
                                currectRunIndex++;
                                // Generate output data file
                                String dataFileName = String.format(outputDir + "/%s-G%03d-P%03d-run%03d-data.dat",
                                        optimizationProblem.getProblemID(),
                                        optimizationProblem.getGenerationsCount(),
                                        optimizationProblem.getPopulationSize(),
                                        runIndex);
                                InputOutput.dumpSingleObjectivePopulation("Final Population",
                                        optimizationProblem,
                                        finalPopulation,
                                        dataFileName);
                                // Remember that the final population contains only
                                // non-dominated individuals, which means that in the
                                // case of single objective optimization finalPopulation
                                // array will contain only one individual (the best
                                // individual).
                                objValues[runIndex] = finalPopulation[0].getObjective(0);
                            }
                            String metricsFileName = String.format(outputDir + "/%s-G%03d-P%03d-metrics.dat",
                                    optimizationProblem.getProblemID(),
                                    optimizationProblem.getGenerationsCount(),
                                    optimizationProblem.getPopulationSize());
                            InputOutput.dumpSingleObjectiveAverage(
                                    optimizationProblem,
                                    objValues,
                                    metricsFileName);
                            // Dump the collected results into a file formatted as required
                            InputOutput.dumpSingleObjGenerationWiseAcrossRunsResults(
                                    data,
                                    String.format("%s/%s_across_runs_stats.dat",
                                            outputDir,
                                            geneticEngine.getAlgorithmName()));
                            // This is only for Single-Objective problems
                            // (because for each popultaion size, generations count,
                            // etaC and etaM combination, a new data matrix should
                            // be created to hold the results of all the runs of
                            // this combination).
                            firstCallToAppendResults = true;
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

    private static boolean firstCallToAppendResults = true;
    private static double[][][] data;

    public static void appendResults(
            Individual[] individuals,
            int runIndex,
            int generationIndex,
            int generationsCount,
            int populationSize) {
        if (firstCallToAppendResults) {
            firstCallToAppendResults = false;
            data = new double[runsCount][generationsCount][populationSize];
        }
        for (int i = 0; i < individuals.length; i++) {
            if (individuals[i].isFeasible()) {
                data[runIndex][generationIndex][i] = individuals[i].getObjective(0);
            } else {
                data[runIndex][generationIndex][i] = Double.NaN;
            }
        }
    }
}
