/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package engines;

import com.mathworks.toolbox.javabuilder.MWException;
import emo.Individual;
import emo.OptimizationProblem;
import emo.OptimizationUtilities;
import emo.VirtualIndividual;
import static engines.NSGA3_ExtremeLocalSearch.MIN_DIFF;
import extremels.ExtremeLocalSearch;
import extremels.LocalSearchOutput2;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import optimization.TestScript_EMO2017;
import optimization.TestScript_IEEE_TEVC_DC_NSGA3;
import parsing.IndividualEvaluator;
import reference_directions.ReferenceDirection;
import utils.Mathematics;
import utils.RandomNumberGenerator;

/**
 *
 * @author seadahai
 */
public class LocalSearchUtils {

    //public final static int MAX_EXT_LS_COUNT = 10;
    /**
     * This function performs extreme local search (LS) when possible. The
     * method checks if <i>(1)</i> the this is the right generation to perform a
     * local search (according to <i>ls_frequency</i>), <i>(2)</i>then makes
     * sure that all the intercepts are non-zero. If both the two conditions
     * check, a third condition must be satisfied (for each extreme point) to
     * conduct an extreme LS. The condition makes sure that the point that will
     * be used to start an extreme LS for some point is not the same as the one
     * previously used for the same point. The number of extreme LS operations
     * can vary from one iteration to another. According to the aforementioned
     * conditions, the maximum number of extreme LS operations can reach M (the
     * number of extreme points) and the minimum is Zero (if the conditions are
     * not satisfied) or anything in between.
     *
     * @param parentPopulation parent population
     * @param offspringPopulation offspring of the parent population
     * @param currentExtremePoints current extreme points
     * @param lastSavedLsStartingPoints the last M points used to conduct
     * extreme LS operations for each extreme point.
     * @param currentIdealPoint the current ideal point
     * @param currentIntercepts the current intercepts
     * @param currentGenerationIndex the index of the current generation
     * @param extLS extreme LS engine
     * @param ls_frequency the frequency of considering LS (every how many
     * generations)
     * @param max_fun_eval the maximum number of function evaluations per a
     * single LS.
     * @param augmentation_factor the weight factor multiplied by obj(i) when
     * conducting an extreme LS for the extreme point (i). It is typically a
     * small number used to avoid weakly dominated extreme points.
     * @param optimizationProblem optimization problem object
     * @param individualEvaluator problem evaluator
     * @return <i>true</i> if at least one extreme LS is conducted, and
     * <i>false</i> otherwise.
     * @throws UnsupportedOperationException If the population size is less than
     * the number of extreme points (whichis the same as the number of
     * objectives M).
     */
    public static boolean extremeLS(
            final Individual[] parentPopulation,
            final Individual[] offspringPopulation,
            final VirtualIndividual[] currentExtremePoints,
            final Individual[] lastSavedLsStartingPoints,
            final double[] currentIdealPoint,
            final double[] currentIntercepts,
            final int currentGenerationIndex,
            final ExtremeLocalSearch extLS,
            final int ls_frequency,
            final int max_fun_eval,
            final double augmentation_factor,
            final OptimizationProblem optimizationProblem,
            final IndividualEvaluator individualEvaluator) throws UnsupportedOperationException {
        boolean phase1InAction = false;
        if (currentGenerationIndex % ls_frequency == 0) {
            // Merge parent and offspring populations
            Individual[] mergedPop = new Individual[parentPopulation.length + offspringPopulation.length];
            System.arraycopy(
                    parentPopulation, 0, mergedPop, 0, parentPopulation.length);
            System.arraycopy(
                    offspringPopulation, 0, mergedPop, parentPopulation.length,
                    offspringPopulation.length);
            // Rule out infeasible solutions
            Individual[] feasibleOnly
                    = OptimizationUtilities.getFeasibleIndividuals(mergedPop);
            if (!noIntercepts(currentIntercepts)) {
                // Calculate the ideal point of the merged population (infeasible 
                // solutions are automatically ignored)
                double[] idealPoint = OptimizationUtilities.getIdealPoint(mergedPop);
                // Calculate the extreme points of the feasible set of solutions
                VirtualIndividual[] extremePoints
                        = OptimizationUtilities.getExtremePoints(
                                currentExtremePoints,
                                idealPoint,
                                null /*this param. is never used & should be removed*/,
                                feasibleOnly);

                // SHOULD-BE-REMOVED (START) (needs further checking)
                // If any of the intercepts are Zero, use One instead
                double[] tempIntercepts = new double[currentIntercepts.length];
                for (int i = 0; i < tempIntercepts.length; i++) {
                    if (Mathematics.compare(currentIntercepts[i], 0) == 0) {
                        tempIntercepts[i] = 1;
                    } else {
                        tempIntercepts[i] = currentIntercepts[i];
                    }
                }
                // SHOULD-BE-REMOVED (END)

                // Apply the appropriate local search to each extreme point
                int[] indiceaArr = getDescendinglySortedExtremeChanges(
                        extremePoints,
                        lastSavedLsStartingPoints,
                        currentIdealPoint,
                        currentIntercepts,
                        augmentation_factor);

                List<Individual> resultingInds = new ArrayList<Individual>();
                int count = 0;
                for (int i = 0; i < indiceaArr.length; i++) {
                    System.out.println("Check Extreme Point " + indiceaArr[i]);
                    // The following check makes sure that the extreme point has
                    // changed since our last extreme local search. If not, then
                    // there is no point repeating local search from the same
                    // starting point.
                    if (lastSavedLsStartingPoints[indiceaArr[i]] == null
                            //                            || differentEnough(
                            //                                    (Individual) extremePoints[i],
                            //                                    lastSavedLsStartingPoints[i], 
                            //                                    MIN_DIFF)
                            //                            || differentEnoughAsf(
                            //                                    (Individual) extremePoints[i],
                            //                                    lastSavedLsStartingPoints[i], i, 
                            //                                    currentIdealPoint, 
                            //                                    currentIntercepts, 
                            //                                    MIN_DIFF)
                            || differentEnoughExtremeValue(
                                    (Individual) extremePoints[indiceaArr[i]],
                                    lastSavedLsStartingPoints[indiceaArr[i]], indiceaArr[i],
                                    currentIdealPoint,
                                    currentIntercepts,
                                    augmentation_factor,
                                    MIN_DIFF)) {
                        // Save the new starting point
                        lastSavedLsStartingPoints[indiceaArr[i]] = (Individual) extremePoints[indiceaArr[i]];
//                        if (count < MAX_EXT_LS_COUNT) {
                        count++;
                        try {
                            // Perform Local Search
                            LocalSearchOutput2 lsOutput = extLS.extremePointLocalSearch(
                                    indiceaArr[i],
                                    ((Individual) extremePoints[indiceaArr[i]]).real,
                                    currentIdealPoint,
                                    tempIntercepts,
                                    augmentation_factor,
                                    max_fun_eval);
                            // Fixing limits: Sometimes Matlab returns a value that
                            // marginally crosses the limit by something like
                            // -1e-21 which is actually Zero with respect to both
                            // Java and Matlab (the precision of Java is usually
                            // set to 1e-10 and the precision of Matlab is usually
                            // set to 1e-12). This value however might be violating
                            // boxing constraints. That's why we need to fix
                            // variable limits.
                            OptimizationUtilities.fixVariableLimits(optimizationProblem, lsOutput.x);
                            // Create a concrete individual from the returned LS point.
                            Individual resultingIndividual = new Individual(
                                    optimizationProblem,
                                    individualEvaluator,
                                    lsOutput.x);
                            // It is very important to note that the f-value returned from
                            // Matlab is not the actual objective space of the resulting 
                            // point. It is rather the value of the weighted sum objective
                            // function. A more clever implementation of 
                            // Matlab-extreme-point-LS could have saved the actual objective
                            // space and return it instead. Here - for assurance purposes - 
                            // we re-evaluate the returned point (this way an erronous
                            // Matlab code will not ruin our operation by insertion
                            // fallacious objective values). However, this additional
                            // evaluation is not counted i.e. does not increase the total
                            // number of function evaluations, that is why you see the
                            // following decrement of 1.
                            individualEvaluator.setFunctionEvaluationsCount(
                                    individualEvaluator.getFunctionEvaluationsCount() - 1);
                            // Increment the total evaluations count
                            individualEvaluator.setFunctionEvaluationsCount(
                                    individualEvaluator.getFunctionEvaluationsCount()
                                    + lsOutput.evaluationsCount);
                            // Add the resulting individual only if it dominates
                            // the already existing extreme point or if they are
                            // both non-dominated with respect to each other.
                            // Notice that using epsilon domination here, can
                            // help avoiding weakly dominated extreme points,
                            // which may greatly harm the results.
                            if(!((Individual) extremePoints[indiceaArr[i]]).dominates(resultingIndividual, 1e-3)) {
                                resultingInds.add(resultingIndividual);
                            }
                        } catch (MWException ex) {
                            Logger.getLogger(NSGA3_ExtremeLocalSearch.class.getName()).log(
                                    Level.SEVERE, null, ex);
                        }
                        phase1InAction = true;
                        System.out.println("    Extreme LS performed in direction " + i);
//                        } else {
//                            System.out.println("    Extreme LS budget exceeded.");
//                        }
                    } else {
                        System.out.println("    No Extreme LS in direction " + i + ". Extreme point is settled.");
                    }
                }

                // Put these new points (hopefully better extreme points) in  place of
                // randomly selected individuals of the offpring population. The 
                // following lines make sure that they are not placed in the same spot
                // by chance. Notice that the following loop will run infinitely if the
                // population size (offspring size) is smaller than the number of
                // objectives (which although highly unlikely is being checked here).
                if (offspringPopulation.length > resultingInds.size()) {
                    Set<Integer> usedIndices = new HashSet<Integer>();
                    for (Individual resultingPoint : resultingInds) {
                        int randomIndex;
                        do {
                            randomIndex = RandomNumberGenerator.rnd(
                                    0, offspringPopulation.length - 1);
                        } while (usedIndices.contains(randomIndex));
                        offspringPopulation[randomIndex] = resultingPoint;
                        usedIndices.add(randomIndex);
                    }
                } else {
                    throw new UnsupportedOperationException(
                            "Population size must be larger than the number "
                            + "of objectives.");
                }
            }
        }
        return phase1InAction;
    }

    /**
     * If the new population is still incomplete, this method checks empty
     * reference directions for local search. Up to an upper limit
     * (MAX_LOCAL_SEARCH_OPERATIONS_PER_GENERATION), these empty reference
     * directions are randomly selected for performing local search. Notice that
     * no local search is performed inside the body of this method. Only, some
     * empty reference directions are stored along with the points closest to
     * them (from ALL fronts) in order to perform local search later on. Notice
     * also that the point selected for an empty direction is not originally
     * associated to that direction (remember, it is empty). Several other
     * approaches can be used to pick the LS starting point for a specific
     * direction (see the other overload of this method).
     *
     * @param newPopSet the new population being formed (added to)
     * @param localSearchDirsSet the set of reference directions upon which
     * local search will be performed later on
     * @param localSearchStartingPointsList the local search starting points
     * corresponding to the reference direction in the set
     * @param feasibleOnly the feasible individuals in the current merged
     * population
     * @param distanceMatrix association matrix
     * @param referenceDirectionsList the list of all reference directions
     */
    public static void coverEmptyRefDirs(
            Set<Individual> newPopSet,
            Set<ReferenceDirection> localSearchDirsSet,
            List<Individual> localSearchStartingPointsList,
            Individual[] feasibleOnly,
            double[][] distanceMatrix,
            List<ReferenceDirection> referenceDirectionsList,
            int MAX_LOCAL_SEARCH_OPERATIONS_PER_GENERATION,
            OptimizationProblem optimizationProblem) {
        boolean phase2InAction = false;
        if (newPopSet.size() < optimizationProblem.getPopulationSize()) {
            // At this point, any direction that has at least one associated point
            // will add one point to the next population. However this might be
            // insufficient, because of at least one of the following reasons:
            // 1 - Some reference directions might not have any associations.
            // 2 - Population size might be larger that the number of directions.
            // Here (as a means of adding more individuals to the next 
            // population), a local search is conducted to find points 
            // for empty reference directions.

            // Find empty reference directions.
            List<Integer> emptyRefDirsIndices = new ArrayList<Integer>();
            for (int i = 0; i < referenceDirectionsList.size(); i++) {
                if (referenceDirectionsList.get(i).surroundingIndividuals == null || referenceDirectionsList.get(i).surroundingIndividuals.isEmpty()) {
                    emptyRefDirsIndices.add(i);
                }
            }
            // Pick a subset of these empty reference directions
            int selectedDirsCount = 0;
            for (int i = 0; i < emptyRefDirsIndices.size() && selectedDirsCount < MAX_LOCAL_SEARCH_OPERATIONS_PER_GENERATION;) {
                phase2InAction = true;
                Integer emptyRefDirIndex = emptyRefDirsIndices.get(RandomNumberGenerator.rnd(0, emptyRefDirsIndices.size() - 1));
                // Add if not already existing
                if (!localSearchDirsSet.contains(referenceDirectionsList.get(emptyRefDirIndex))) {
                    localSearchDirsSet.add(referenceDirectionsList.get(emptyRefDirIndex));
                    localSearchStartingPointsList.add(feasibleOnly[getClosestIndividualIndex(emptyRefDirIndex, distanceMatrix)]);
                    selectedDirsCount++;
                    i++;
                }
            }
        }
        if (phase2InAction) {
            TestScript_EMO2017.printer.print("1 ");
        } else {
            TestScript_EMO2017.printer.print("0 ");
        }
    }

    /**
     * Checks if two individuals differ by at least <i>MIN_DIFF</i> in at least
     * one decision variable.
     *
     * @param individual1 first individual
     * @param individual2 second individual
     * @param delta the minimum difference
     * @return <i>true</i> if they are different, <i>false</i> otherwise.
     */
    public static boolean differentEnough(Individual individual1, Individual individual2, double delta) {
        for (int i = 0; i < individual1.real.length; i++) {
            if (Math.abs(individual1.real[i] - individual2.real[i]) > delta) {
                return true;
            }
        }
        return false;
    }

    /**
     * Check if there is at least one Zero intercept.
     *
     * @param intercepts the intercepts to check.
     * @return true if any of the current intercepts is Zero, false otherwise
     */
    public static boolean noIntercepts(final double[] intercepts) {
        if (intercepts == null) {
            return true;
        }
        for (int i = 0; i < intercepts.length; i++) {
            if (Mathematics.compare(intercepts[i], 0) == 0) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns the index of the closest individual to the reference direction at
     * the specified index.
     *
     * @param refDirIndex index of the reference direction.
     * @param distanceMatrix distance matrix (coming out of association)
     * @return the index of the closest individual to the reference direction
     */
    public static int getClosestIndividualIndex(
            Integer refDirIndex,
            double[][] distanceMatrix) {
        int closestIndividualIndex = -1;
        double minDist = Double.POSITIVE_INFINITY;
        for (int i = 0; i < distanceMatrix.length; i++) {
            if (distanceMatrix[i][refDirIndex] < minDist) {
                closestIndividualIndex = i;
                minDist = distanceMatrix[i][refDirIndex];
            }
        }
        return closestIndividualIndex;
    }

    /**
     * Returns the index of the closest individual to the reference direction at
     * the specified index.
     *
     * @param refDirIndex index of the reference direction.
     * @param distanceMatrix distance matrix (coming out of association)
     * @return the index of the closest individual to the reference direction
     */
    public static int getClosestFront1IndividualIndex(
            Integer refDirIndex,
            double[][] distanceMatrix,
            Individual[] individuals) {
        int closestIndividualIndex = -1;
        double minDist = Double.POSITIVE_INFINITY;
        for (int i = 0; i < distanceMatrix.length; i++) {
            if (individuals[i].getRank() == 1 && distanceMatrix[i][refDirIndex] < minDist) {
                closestIndividualIndex = i;
                minDist = distanceMatrix[i][refDirIndex];
            }
        }
        return closestIndividualIndex;
    }

    /**
     * This method differs from its other overload in two ways: 1 - If the new
     * LS starting point is not closer to the reference direction than the
     * previous one, the direction is ignored (this is a better approach for
     * handling disconnected fronts) 2 - It selects the closest individual from
     * the first front individuals 3 - An empty ref dir is a one that has no
     * first front associations, not the one having no associations at all.
     * only.
     *
     * @param newPopSet
     * @param localSearchDirsSet
     * @param localSearchStartingPointsList
     * @param feasibleOnly
     * @param distanceMatrix
     * @param referenceDirectionsList
     * @param MAX_LOCAL_SEARCH_OPERATIONS_PER_GENERATION
     * @param optimizationProblem
     * @param refDirsToLastStartDist
     */
    static void coverEmptyRefDirs(
            Set<Individual> newPopSet,
            Set<ReferenceDirection> localSearchDirsSet,
            List<Individual> localSearchStartingPointsList,
            Individual[] feasibleOnly,
            double[][] distanceMatrix,
            List<ReferenceDirection> referenceDirectionsList,
            int MAX_LOCAL_SEARCH_OPERATIONS_PER_GENERATION,
            OptimizationProblem optimizationProblem,
            double[] refDirsToLastStartDist) {
        boolean phase2InAction = false;
        if (newPopSet.size() < optimizationProblem.getPopulationSize()) {
            // At this point, any direction that has at least one associated point
            // will add one point to the next population. However this might be
            // insufficient, because of at least one of the following reasons:
            // 1 - Some reference directions might not have any associations.
            // 2 - Population size might be larger that the number of directions.
            // Here (as a means of adding more individuals to the next 
            // population), a local search is conducted to find points 
            // for empty reference directions.

            // Find empty reference directions.
            List<Integer> emptyRefDirsIndices = new ArrayList<>();
            for (int i = 0; i < referenceDirectionsList.size(); i++) {
                if (!hasFirstFronAssociations(referenceDirectionsList.get(i))) {
                    //if (referenceDirectionsList.get(i).surroundingIndividuals == null || referenceDirectionsList.get(i).surroundingIndividuals.isEmpty()) {
                    // If the new LS starting point is not closer to the 
                    // reference direction than the previous one, ignore this
                    // direction.
                    int closestF1IndIndex = getClosestFront1IndividualIndex(i, distanceMatrix, feasibleOnly); // Modified
                    if (distanceMatrix[closestF1IndIndex][i] < refDirsToLastStartDist[i]) { // Modified
                        emptyRefDirsIndices.add(i);
                        refDirsToLastStartDist[i] = distanceMatrix[closestF1IndIndex][i]; // Modified
                    } // Modified
                }
            }
            // Pick a subset of these empty reference directions
            int selectedDirsCount = 0;
            for (int i = 0; i < emptyRefDirsIndices.size() && selectedDirsCount < MAX_LOCAL_SEARCH_OPERATIONS_PER_GENERATION;) {
                phase2InAction = true;
//                // Select a random direction from the remaining empty directions
//                Integer emptyRefDirIndex = emptyRefDirsIndices.get(RandomNumberGenerator.rnd(0, emptyRefDirsIndices.size() - 1));
                // Select the closest direction to the feasible region out of the remaining empty directions
                Integer emptyRefDirIndex = getClosestDirIndexToFeasibleFirstFront(distanceMatrix, feasibleOnly, emptyRefDirsIndices);
                emptyRefDirsIndices.remove(emptyRefDirIndex);
                // Add the selected direction if it is not already existing (if
                // the closest direction to feasible region is selected, the 
                // following IF statment should always get executed i.e. be 
                // true). If a random direction is selected, this might not be 
                // the case.
                if (!localSearchDirsSet.contains(referenceDirectionsList.get(emptyRefDirIndex))) {
                    localSearchDirsSet.add(referenceDirectionsList.get(emptyRefDirIndex));
                    localSearchStartingPointsList.add(feasibleOnly[getClosestFront1IndividualIndex(emptyRefDirIndex, distanceMatrix, feasibleOnly)]); // Modified
                    selectedDirsCount++;
                    i++;
                }
            }
        }
        if (phase2InAction) {
            TestScript_EMO2017.printer.print("1 ");
        } else {
            TestScript_EMO2017.printer.print("0 ");
        }
    }

    private static Integer getClosestDirIndexToFeasibleFirstFront(
            double[][] distanceMatrix,
            Individual[] feasibleOnly,
            List<Integer> allowedDirsIndices) {
        int closestDirToFeasibleRegionIndex = -1;
        double minDist = Double.MAX_VALUE;
        for (int i = 0; i < distanceMatrix.length; i++) {
            for (int j = 0; j < distanceMatrix[i].length; j++) {
                if (feasibleOnly[i].getRank() == 1 && allowedDirsIndices.contains(j)) {
                    if (distanceMatrix[i][j] < minDist) {
                        closestDirToFeasibleRegionIndex = j;
                        minDist = distanceMatrix[i][j];
                    }
                }
            }
        }
        return closestDirToFeasibleRegionIndex;
    }

    private static boolean hasFirstFronAssociations(ReferenceDirection dir) {
        if (dir.surroundingIndividuals == null || dir.surroundingIndividuals.isEmpty()) {
            return false;
        } else {
            for (Individual ind : dir.surroundingIndividuals) {
                if (ind.getRank() == 1) {
                    return true;
                }
            }
            return false;
        }
    }

    private static boolean differentEnoughAsf(
            Individual newExtremePoint,
            Individual oldExtremePoint,
            int objectiveIndex,
            double[] idealPoint,
            double[] intercepts,
            double delta) {
        double oldAsf = 0;
        double newAsf = 0;
        for (int i = 0; i < newExtremePoint.getObjectivesCount(); i++) {
            double w;
            if (i == objectiveIndex) {
                w = 1.0;
            } else {
                w = 1e-6;
            }
            oldAsf = Math.max((oldExtremePoint.getObjective(i) - idealPoint[i]) / intercepts[i] / w, oldAsf);
            newAsf = Math.max((newExtremePoint.getObjective(i) - idealPoint[i]) / intercepts[i] / w, newAsf);
        }
        return Math.abs(newAsf - oldAsf) >= delta;
    }

    private static boolean differentEnoughExtremeValue(
            Individual newExtremePoint,
            Individual oldExtremePoint,
            int objectiveIndex,
            double[] ideal,
            double[] intercepts,
            double augmentationFactor,
            double delta) {
        double oldExtremeValue = 0;
        double newExtremeValue = 0;
        for (int i = 0; i < newExtremePoint.getObjectivesCount(); i++) {
            if (i != objectiveIndex) {
                oldExtremeValue += (oldExtremePoint.getObjective(i) - ideal[i]) / intercepts[i];
                newExtremeValue += (newExtremePoint.getObjective(i) - ideal[i]) / intercepts[i];
            }
        }
        oldExtremeValue += augmentationFactor * (oldExtremePoint.getObjective(objectiveIndex) - ideal[objectiveIndex]) / intercepts[objectiveIndex];
        newExtremeValue += augmentationFactor * (newExtremePoint.getObjective(objectiveIndex) - ideal[objectiveIndex]) / intercepts[objectiveIndex];
        return Math.abs(newExtremeValue - oldExtremeValue) >= delta;
    }

    private static int[] getDescendinglySortedExtremeChanges(VirtualIndividual[] extremePoints, Individual[] lastSavedLsStartingPoints, double[] ideal, double[] intercepts, double augmentationFactor) {
        double[] changeMagnitudes = new double[extremePoints.length];
        for (int i = 0; i < extremePoints.length; i++) {
            if (lastSavedLsStartingPoints[i] == null) {
                changeMagnitudes[i] = Double.MAX_VALUE;
            } else {
                double oldExtremeValue = 0;
                double newExtremeValue = 0;
                for (int j = 0; j < extremePoints[i].getObjectivesCount(); j++) {
                    if (j != i) {
                        oldExtremeValue += (lastSavedLsStartingPoints[i].getObjective(j) - ideal[j]) / intercepts[j];
                        newExtremeValue += (extremePoints[i].getObjective(j) - ideal[j]) / intercepts[j];
                    }
                }
                oldExtremeValue += augmentationFactor * (lastSavedLsStartingPoints[i].getObjective(i) - ideal[i]) / intercepts[i];
                newExtremeValue += augmentationFactor * (extremePoints[i].getObjective(i) - ideal[i]) / intercepts[i];
                changeMagnitudes[i] = Math.abs(newExtremeValue - oldExtremeValue);
            }
        }
        // Get sorted indices
        int[] indices = new int[changeMagnitudes.length];
        for (int i = 0; i < indices.length; i++) {
            indices[i] = i;
        }
        // Sort
        for (int i = 0; i < indices.length - 1; i++) {
            for (int j = i + 1; j < indices.length; j++) {
                if (changeMagnitudes[i] < changeMagnitudes[j]) {
                    // Swap
                    double tempMagnitude = changeMagnitudes[i];
                    changeMagnitudes[i] = changeMagnitudes[j];
                    changeMagnitudes[j] = tempMagnitude;
                    int tempIndex = indices[i];
                    indices[i] = indices[j];
                    indices[j] = tempIndex;
                }
            }
        }
        return indices;
    }
}
