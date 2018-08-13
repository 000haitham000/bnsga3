/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package engines;

import utils.Mathematics;
import utils.PerformanceMetrics;
import com.mathworks.toolbox.javabuilder.MWException;
import emo.DoubleAssignmentException;
import emo.Individual;
import emo.OptimizationProblem;
import emo.OptimizationUtilities;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import kkt.KKT_Calculator;
import asf.ASF_Minimizer;
import asf.LocalSearch;
import static engines.AbstractGeneticEngine.DUMP_ALL_GENERATIONS_DECISION_SPACE;
import static engines.AbstractGeneticEngine.DUMP_ALL_GENERATIONS_MATLAB_SCRIPTS;
import static engines.AbstractGeneticEngine.DUMP_ALL_GENERATIONS_NORMALIZED_MATLAB_SCRIPTS;
import static engines.AbstractGeneticEngine.DUMP_ALL_GENERATIONS_OBJECTIVE_SPACE;
import static engines.AbstractGeneticEngine.DUMP_ANIMATED_MATLAB_SCRIPT;
import java.io.File;
import optimization.TestScript_SingleObjective;
import parsing.IndividualEvaluator;
import parsing.KKTPM;
import reference_directions.ReferenceDirection;
import utils.InputOutput;

/**
 *
 * @author toshiba
 */
public class NSGA2Engine extends AbstractGeneticEngine {

    public NSGA2Engine(
            OptimizationProblem optimizationProblem,
            IndividualEvaluator individualEvaluator) {
        super(optimizationProblem, individualEvaluator);
    }

    @Override
    public Individual[] niching(List<List<Individual>> fronts, int remainingIndvsCount) throws DoubleAssignmentException {
        int individualsCount = 0;
        // Get the last front Fl index
        int frontIndex = -1;
        while (individualsCount < optimizationProblem.getPopulationSize()) {
            frontIndex++;
            individualsCount += fronts.get(frontIndex).size();
        }
        // Get the last front Fl itself
        List<Individual> lastFront = fronts.get(frontIndex);
        // Make an array from the list
        Individual[] individualsArr = new Individual[lastFront.size()];
        lastFront.toArray(individualsArr);

        Individual.restartCrowdingDistanceCalculations(individualsArr);
        // Calculate the crowding distance of the last front
        //Individual.restartCrowdingDistanceCalculations(individualsArr); not needed anymore (already done in the calling method)
        updateCrowdingDistances(individualsArr);

        // Sort descendingly using crowding distance
        Individual.setNsga2comparisonObjectiveIndex(-1);
        Arrays.sort(individualsArr);
        // Copy the descendingly sorted individuals to an array and return the array
        Individual[] remainingIndividuals = new Individual[remainingIndvsCount];
        for (int i = 0; i < remainingIndividuals.length; i++) {
            remainingIndividuals[i] = individualsArr[i];
        }
        if (DEBUG_ALL) {
            System.out.println("---------------");
            System.out.println("Niching Results");
            System.out.println("---------------");
            for (Individual individual : remainingIndividuals) {
                System.out.println(individual.getShortVariableSpace());
            }
        }
        return remainingIndividuals;
    }

    public void updateCrowdingDistances(Individual[] individuals) throws DoubleAssignmentException {
        Individual[] individualsCopy = new Individual[individuals.length];
        System.arraycopy(individuals, 0, individualsCopy, 0, individuals.length);
        // NSGA-II code
        for (int m = 0; m < optimizationProblem.objectives.length; m++) {
            // Sort the last front according to the current objective
            Individual.setNsga2comparisonObjectiveIndex(m);
            Arrays.sort(individualsCopy);
            // Set the min max solutions (with respect to the current objective) to infinity
            individualsCopy[0].setNsga2crowdingDistance(MAX_DOUBLE_VALUE);
            individualsCopy[individualsCopy.length - 1].setNsga2crowdingDistance(MAX_DOUBLE_VALUE);
            for (int i = 1; i < individualsCopy.length - 1; i++) {
                double currentCrowdedDistance = individualsCopy[i].getNsga2crowdingDistance();
                double objMinValue = individualsCopy[0].getObjective(m);
                double objMaxValue = individualsCopy[individualsCopy.length - 1].getObjective(m);
                double previousIndividualObjValue = individualsCopy[i - 1].getObjective(m);
                double nextIndividualObjValue = individualsCopy[i + 1].getObjective(m);
                double currentIndividualCrowdedDistance = currentCrowdedDistance + (nextIndividualObjValue - previousIndividualObjValue) / (objMaxValue - objMinValue);
                individualsCopy[i].setNsga2crowdingDistance(currentIndividualCrowdedDistance);
            }
        }
        for (Individual individual : individualsCopy) {
            individual.flagNSGA2CrowdingDistanceAlreadySet();
        }
    }

    @Override
    public Individual[] start(
            File outputDir,
            int runIndex,
            double epsilon,
            //int localSearchFlag,
            ASF_Minimizer asfMinizer,
            KKT_Calculator kktCalculator
    ) throws FileNotFoundException, DoubleAssignmentException, IOException, MWException {
        return start(outputDir, runIndex, epsilon, /*localSearchFlag, */ asfMinizer, kktCalculator, Double.MAX_VALUE, Integer.MAX_VALUE);
    }

    @Override
    public Individual[] start(
            File outputDir,
            int runIndex,
            double epsilon,
            ASF_Minimizer asfMinizer,
            KKT_Calculator kktCalculator,
            double hvLimit,
            int funcEvaluationsLimit
    ) throws FileNotFoundException, DoubleAssignmentException, IOException, MWException {
        return start(outputDir, runIndex, epsilon, asfMinizer, kktCalculator, hvLimit, funcEvaluationsLimit, -1);
    }

    @Override
    public Individual[] start(
            File outputDir,
            int runIndex,
            double epsilon,
            ASF_Minimizer asfMinimizer,
            KKT_Calculator kktCalculator,
            double hvLimit,
            int funcEvaluationsLimit,
            double kktMetricLimit
    ) throws FileNotFoundException, DoubleAssignmentException, IOException, MWException {
        // Set necessary fields
        this.asfMinimizer = asfMinimizer;
        this.kktCalculator = kktCalculator;
        this.runIndex = runIndex;
        this.epsilon = epsilon;
        this.hvLimit = hvLimit;
        this.funcEvaluationsLimit = funcEvaluationsLimit;
        this.kktMetricLimit = kktMetricLimit;
        // Generate the initial population and perform all necessary
        // initializations before starting the main loop
        currentPopulation = initialize();
        // Each loop represents one generation
        do {
            // Whatever logic is needed at the beginning of every iteration
            iterationStart();
            // Create the offspring (tournament selection & crossover)
            Individual[] offspringPopulation = getOffspringPopulation(currentPopulation);
            // Mutation (binary & real)
            mutate(offspringPopulation);
            // Update objective values & constraints violation values of the offspring
            updateObjectives(offspringPopulation);
            // Whatever logic is necessary after creating the offspring
            postOffspringCreation(offspringPopulation);
            // Merge the parents and the offspring in a single population
            Individual[] mergedPopulation = merge(currentPopulation, offspringPopulation);
            // Non-dominated sorting
            List<List<Individual>> fronts = rankIndividuals(mergedPopulation, epsilon, String.format("%d-population+%d-offspring", currentGenerationIndex, currentGenerationIndex));
            // Whatever logic is necessary after the non-dominated sorting
            postNondominatedSorting(fronts);
            // Update ideal point
            updateIdealPoint(mergedPopulation, currentIdealPoint, String.format("Ideal Point(merged population)"));
            // Create a new population for the next generation, according to the
            // non-dominated sorting performed and using niching if necessary.
            fillUpPopulation(fronts, mergedPopulation);
            // Report generation-wise information
            report(outputDir);
            // Update all the metrics that might be used for termination.
            updateTerminationMetrics();
            // Display the population at the end of this generation if debugging
            // flags are set.
            displayPopulationIfDebug(currentGenerationIndex, currentPopulation);
            // Whatever logic is needed at the end of every iteration
            iterationEnd();
        } while (!terminationCondition());
        // Prepare the final population.
        Individual[] finalPopulation = prepareFinalPopulation();
        // Whatever logic is needed before returning the final population
        finalize(finalPopulation);
        // Return the final population
        return finalPopulation;
    }

    @Override
    protected Individual[] prepareFinalPopulation() {
        return OptimizationUtilities.getNonDominatedIndividuals(currentPopulation, epsilon);
    }

    /**
     *
     * @param fronts
     * @param mergedPopulation
     * @throws DoubleAssignmentException
     * @throws EvaluationException
     */
    @Override
    protected void fillUpPopulation(List<List<Individual>> fronts, Individual[] mergedPopulation) throws DoubleAssignmentException {
        // Get reamining individuals
        int remainingIndividualsCount = getRemainingCount(fronts);
        // Niching
        Individual[] lastFrontSubset = niching(fronts, remainingIndividualsCount);
        // Refill Population
        int limitingFrontIndex = getLimitingFrontIndex(fronts);
        reFillPopulation(currentPopulation, mergedPopulation, lastFrontSubset, limitingFrontIndex);
    }

    private Individual[] initialize() {
        // Initialize necessary fields
        initializeFields();
        // Generate intial population
        Individual[] initialPopulation = generateInitialPopulation("INITIAL");
        // Perform initial ranking
        rankIndividuals(initialPopulation, epsilon, "0"); // return value ignored
        // Create the initial ideal point
        currentIdealPoint = getInitialIdealPoint(initialPopulation, "0");
        return initialPopulation;
    }

    @Override
    protected void updateTerminationMetrics() throws UnsupportedOperationException, MWException {
        // Update the current hypervolume (if hypervolume is used as a stopping criterion)
        updateCurrentHV(hvLimit, currentPopulation, epsilon);
        // Update the current median KKT metric (if median KKT metric is used as a stopping criterion)
        updateCurrentKktMetric(kktMetricLimit, currentGenerationIndex, kktCalculator, currentPopulation);
        // Increment generationsIndex
        currentGenerationIndex++;
    }

    private void updateCurrentKktMetric(double kktMetricLimit, int generationsCounter, KKT_Calculator kktCalculator, Individual[] currentPopulation) throws MWException {
        if (kktMetricLimit != -1 && generationsCounter % 5 == 0) {
            KKTPM[] kktpm = kktCalculator.calculatePopulationKKT(currentPopulation);
            double[] kktpmValues = new double[kktpm.length];
            for (int i = 0; i < kktpm.length; i++) {
                kktpmValues[i] = kktpm[i].getKktpm();
            }
            currentKktMetric = Mathematics.getMedian(kktpmValues);
        }
    }

    private void updateCurrentHV(double hvLimit, Individual[] currentPopulation, double epsilon) throws UnsupportedOperationException {
        if (hvLimit != Double.MAX_VALUE) {
            // Calculate current hypervolume
            if (optimizationProblem.objectives.length == 2) {
                currentHV = PerformanceMetrics.calculateHyperVolumeForTwoObjectivesOnly(
                        this,
                        currentPopulation,
                        hvReferencePoint,
                        hvIdealPoint,
                        epsilon);
            } else {
                throw new UnsupportedOperationException("Hypervolume can only be calculated for 2 objectives");
            }
        }
    }

    @Override
    public String getAlgorithmName() {
        return "nsga2";
    }
}
