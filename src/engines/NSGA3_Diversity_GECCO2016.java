/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package engines;

import asf.LocalSearchOutput;
import com.mathworks.toolbox.javabuilder.MWException;
import emo.Individual;
import emo.OptimizationProblem;
import emo.OptimizationUtilities;
import static engines.NSGA3Engine.UTOPIAN_EPSILON;
import static engines.NSGA3Engine.associate;
import extremels.ExtremeLocalSearch;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import optimization.TestScript_EMO2017;
import parsing.IndividualEvaluator;
import reference_directions.ReferenceDirection;
import utils.Mathematics;
import utils.RandomNumberGenerator;

/**
 *
 * @author Haitham
 */
public class NSGA3_Diversity_GECCO2016 extends NSGA3_ExtremeLocalSearch implements InternalLocalSearchInterface {

//    private PrintWriter funEvalPrinter;
    public NSGA3_Diversity_GECCO2016(
            OptimizationProblem optimizationProblem,
            IndividualEvaluator individualEvaluator,
            int[] divisions,
            ExtremeLocalSearch extLS) {
        super(optimizationProblem, individualEvaluator, divisions, extLS);
    }

    public NSGA3_Diversity_GECCO2016(
            OptimizationProblem optimizationProblem,
            IndividualEvaluator individualEvaluator,
            ExtremeLocalSearch extLS) {
        super(optimizationProblem, individualEvaluator, extLS);
    }

    /**
     * Performs diversity-enhanced niching. The main algorithm can be summarized
     * as follows:
     * <ul>
     * <li>Every N generations do the following, otherwise proceed to normal
     * NSGA-III niching:
     * </li>
     * <ul>
     * <li>Search for extreme points using the unbalanced weighted sum
     * approach.</li>
     * <li>If population size is less than the number of reference directions
     * terminate (throw an exception)</li>
     * <li>Update extreme points and intercepts normally as in NSGA-III.
     * </li>
     * <li>Perform reference direction association normally as in NSGA-III.
     * </li>
     * <li>For each reference direction, add the most promising attached
     * individual to the new population. Even if this individual is already
     * dominated.</li>
     * <li>If the extreme points remained unchanged, use local search to enhance
     * diversity and convergence (in this specific order) of the current
     * population as follows:</li>
     * <ul>
     * <li>Try using local search to cover empty reference directions.
     * </li>
     * <li>If there are still available local search slots, apply local search
     * on those badly ranked individuals already included in the new population.
     * </li>
     * </ul>
     * <li>Since the previous steps do not guarantee filling the entire
     * population, the new population is completed by including the best ranked
     * individuals ignored so far.</li>
     * </ul>
     * </ul>
     *
     * @param fronts a list of fronts form F(1) to F(last) formed by the
     * non-dominated sorting procedure over the merged population.
     * @param mergedPopulation all the individuals of the previous generation
     * and their offspring merged together.
     * @throws EvaluationException If an error occurred while creating a new
     * individual (usually after a local search operation).
     */
    @Override
    protected void fillUpPopulation(
            List<List<Individual>> fronts,
            Individual[] mergedPopulation) {
        // * IMPORTANT NOTE: This code INTENTIONALLY does NOT handle the case
        // where the population size is less than the number of reference
        // directions. This case should NOT happen in the first place, because
        // if it did happen the algorithm will always favor those reference
        // directions at the beginning of the list at the expense of those
        // at the end of the list. For this reason, the code is intentionally
        // set to throw an exception (UnsupportedOperationException) if this
        // case is encountered.
        if (optimizationProblem.getPopulationSize() < referenceDirectionsList.size()) {
            throw new UnsupportedOperationException("Population size connot be "
                    + "less than the number of reference directions. If this "
                    + "happens, the algorithm will always favor those "
                    + "reference directions at the beginning of the list at "
                    + "the expense of those at the end of the list.");
        }
//        // TEST CASE
//        createBiObjTestCase2(mergedPopulation);
        // Perform association using all population members
        if (currentGenerationIndex % LS_FREQUENCY != 0 || LocalSearchUtils.noIntercepts(currentIntercepts)) {
            super.fillUpPopulation(fronts, mergedPopulation);
        } else {
            // Infeasible solutions should not go through normalization,
            // association or niching. This destroys normalization and degrades 
            // the results significantly. Consequently, we need to retrieve 
            // only the feasible solutions from the merged population.
            Individual[] feasibleOnly
                    = OptimizationUtilities.getFeasibleIndividuals(
                            mergedPopulation);
            // If normalization is not possible, just revert to default NSGA-III
            // niching.
            if (!isNormalizationPossible(feasibleOnly)) {
                super.fillUpPopulation(fronts, mergedPopulation);
                return;
            }
            // Perform reference direction association (the standard NSGA-III)
            // association procedure.
            double[][] distanceMatrix = associate(
                    feasibleOnly,
                    referenceDirectionsList,
                    currentIdealPoint,
                    currentIntercepts,
                    UTOPIAN_EPSILON,
                    null);
            // Create the set of individuals representing the new population
            Set<Individual> newPopSet = new HashSet<Individual>();
            // Perform modified niching
            addBestWithinNiche(newPopSet);
            int popSizeAfterBasicNiching = newPopSet.size();
            // If no extreme point LS was performed, go for the reference
            // directions based LS.
            if (!phase1InAction) {
                TestScript_EMO2017.printer.print("0 ");
                populationLocalSearch(newPopSet, feasibleOnly, distanceMatrix);
                System.out.println("    Population LS performed");
            } else {
                TestScript_EMO2017.printer.print("1 0 0");
                System.out.println("    No Population LS.");
            }
            // --- TO-BE-REMOVED-START ---
            TestScript_EMO2017.printer.println();
            // --- TO-BE-REMOVED-START ---

            int popSizeAfterPopLS = newPopSet.size();
            // As previously mentioned, even after local search, the number of 
            // selected (via niching) and new individuals (via local search)
            // might not be sufficient. So, in order to completely fill the next
            // population, we can take some of those individuals which were
            // filtered out by the niching scheme. Individuals will be taken 
            // according to their rank, the lower the better.
            completeFillingNewPop(newPopSet, mergedPopulation);
            int popSizeAfterFilling = newPopSet.size();

            // Dipslay info.
            System.out.format("PopSize after modified niching: %02d%n",
                    popSizeAfterBasicNiching);
            System.out.format("PopSize after pop local search: %02d%n",
                    popSizeAfterPopLS);
            System.out.format("PopSize after final filling:    %02d%n",
                    popSizeAfterFilling);

            // At this point, the number of selected individuals must be exactly
            // equal to the population size.
            // Add the selected individuls to the actual new population.
            int index = 0;
            for (Iterator<Individual> it = newPopSet.iterator();
                    it.hasNext() && index < optimizationProblem.getPopulationSize();
                    index++) {
                Individual individual = it.next();
                currentPopulation[index] = individual;
            }
        }
    }

    /**
     * Checks if the current population can be normalized using the NSGA-III
     * normalization procedures. The reasons which can prevent a population from
     * being normalized are: 1 - The number of non-dominated points is less than
     * two. 2 - All non-dominated points share the same value (in at least one
     * objective - weak domination). In such a case, one/some/all of the
     * intercepts will be Zero, which will result in NaN values later on.
     *
     * @param individuals The population to be checked
     * @return true if the population can be normalized and false otherwise
     */
    protected boolean isNormalizationPossible(Individual[] individuals) {
        // Since normalization is only performed using non-dominated individuals
        // let's get the non-dominated individuals first.
        Individual[] nonDomInds
                = OptimizationUtilities.getNonDominatedIndividuals(individuals, 0);
        // At least two points are required to calculate intercepts.
        if (nonDomInds.length < 2) {
            return false;
        }
        // If all the individuals have the same value (even in only one
        // objective), one/some/all intercepts can be Zero yielding NaN values
        // later on. Here we make sure that there are at least two different
        // values per objectives accross all the individuals.
        int objCount = nonDomInds[0].getObjectivesCount();
        outerLoop:
        for (int i = 0; i < objCount; i++) {
            for (int j = 1; j < nonDomInds.length; j++) {
                if (Mathematics.compare(
                        nonDomInds[j].getObjective(i),
                        nonDomInds[0].getObjective(i)) != 0) {
                    continue outerLoop;
                }
            }
            return false;
        }
        return true;
    }

    /**
     * Perform niching according to rank and association (in order). Among all
     * the individuals attached to some direction, the top ranked ones are
     * selected. Then, the closest individual to the reference direction is
     * selected. Using this approach, an already dominated individual (A) which
     * best represents its reference direction, will be favored over a
     * non-dominated individual (B), which will be left behind if there exists
     * at least one other non-dominated individual (C) attached to the same
     * reference direction as (B) but closer (in terms of perpendicular
     * distance).
     *
     * @param newPopSet the set of individuals to be updated (added to).
     */
    protected void addBestWithinNiche(Set<Individual> newPopSet) {
        // NICHING: We need to choose the most suitable point for each reference
        // direction according to the following factors (in order):
        // 1 - Rank
        // 2 - Proximity to the reference direction
        for (int i = 0; i < referenceDirectionsList.size(); i++) {
            // Current reference direction
            ReferenceDirection referenceDirection
                    = referenceDirectionsList.get(i);
            // Do not apply to empty reference directions.
            if (referenceDirection.surroundingIndividuals != null
                    && !referenceDirection.surroundingIndividuals.isEmpty()) {
                // Find the best rank
                int minRank = Integer.MAX_VALUE;
                for (Individual individual
                        : referenceDirection.surroundingIndividuals) {
                    if (individual.getRank() < minRank) {
                        minRank = individual.getRank();
                    }
                }
                // Find the group of indiviuals having the same rank
                List<Individual> bestRanked = new ArrayList<Individual>();
                for (Individual individual
                        : referenceDirection.surroundingIndividuals) {
                    if (individual.getRank() == minRank) {
                        bestRanked.add(individual);
                    }
                }
                // Among the best ranked individuals, check the closest to
                // the reference direction.
                Individual closestIndividual = null;
                double minDist = Double.POSITIVE_INFINITY;
                for (Individual individual : bestRanked) {
                    if (individual.getPerpendicularDistance() < minDist) {
                        closestIndividual = individual;
                        minDist = individual.getPerpendicularDistance();
                    }
                }
                // Add the chosen individual to the population
                newPopSet.add(closestIndividual);
            }
        }
    }

    /**
     * Performs local search over a set of direction/point pairs. These pairs
     * are selected using sub-routines according to a combination of factors
     * e.g. empty reference directions and/or bad associations. For more details
     * refer to the documentations of the designated sub-routines.
     *
     * @param newPopSet the new population being formed (added to)
     * @param feasibleOnly the feasible individuals in the current merged
     * population
     * @param distanceMatrix association matrix
     * @throws EvaluationException if an error occurred during creating new
     * individuals from local search output.
     */
    @Override
    public void populationLocalSearch(
            Set<Individual> newPopSet,
            Individual[] feasibleOnly,
            double[][] distanceMatrix) {
        // The set of directions that might need to be locally serached
        Set<ReferenceDirection> localSearchDirsSet
                = new HashSet<ReferenceDirection>();
        // The list of LS starting points corresponding to each direction
        List<Individual> localSearchStartingPointsList
                = new ArrayList<Individual>();
        // Cover empty reference directions if any existed
        LocalSearchUtils.coverEmptyRefDirs(newPopSet, localSearchDirsSet,
                localSearchStartingPointsList, feasibleOnly, distanceMatrix,
                referenceDirectionsList, MAX_LOCAL_SEARCH_OPERATIONS_PER_GENERATION,
                optimizationProblem);
        int _fromEmptyDirs = localSearchDirsSet.size();
//        fromEmptyDirs.add(_fromEmptyDirs);
        System.out.format("\tFrom Empty RefDirs. = %d%n", _fromEmptyDirs);
        // Cover rank 2 associations if any exists and if there is still
        // room in the local search slots.
        coverRank2Associations(newPopSet, localSearchDirsSet,
                localSearchStartingPointsList);
        int _fromRank2 = localSearchDirsSet.size() - _fromEmptyDirs;
//        fromRank2Assc.add(_fromRank2);
        System.out.format("\tFrom Rank(2) Ind.'s = %d%n", _fromRank2);
        // At this point, we have exactly a number of direction/starting
        // point pairs equal to MAX_LOCAL_SEARCH_OPERATIONS_PER_GENERATION.
        // Convert the Set to a List for convenience.
        List<ReferenceDirection> localSearchDirsList
                = new ArrayList<ReferenceDirection>(localSearchDirsSet);
        //List<Individual> localSearchStartingPointsList = new ArrayList<Individual>(localSearchStartingPointsSet);

//        List<Individual> pointsBefore = new ArrayList<Individual>();
//        List<Individual> pointsAfter = new ArrayList<Individual>();
        for (int i = 0; i < localSearchDirsList.size(); i++) {
            try {
                // Perform local search
                LocalSearchOutput asfOutput = asfMinimizer.minimizeASF(
                        localSearchStartingPointsList.get(i).real,
                        Mathematics.getUnitVector(
                                localSearchDirsList.get(i).direction),
                        MAX_FUNC_EVAL,
                        currentIdealPoint,
                        currentIntercepts,
                        NSGA3ConvergenceEngine.UTOPIAN_EPSILON);
                // Increment the number of function evaluations due to local
                // search.
                individualEvaluator.setFunctionEvaluationsCount(
                        individualEvaluator.getFunctionEvaluationsCount()
                        + asfOutput.evaluationsCount);
                // Local search may return a solution that is infeasible by a very
                // slight margin. The following call to fixVariableLimits(...) aims at
                // fixing design variables that are out of the pre-defined boxing
                // constraints.
                // NOTE-1: If the new solution violates a normal constraint (a non-boxing
                // constraint), the created individual will be deemed infeasible
                // through the updateIndividualObjectivesAndConstraints(...) method.
                // Update to Note-1: We had discovered this error before we noticed the
                // accuracy discrepancy between the Java code and Matlab's fmincon.
                // We did not check if this marginal infeasibility is still happenning
                // after having this discrepancy fixed (by increasing fmincon(...)
                // accuracy to at least the same level as the Java code).
                OptimizationUtilities.fixVariableLimits(optimizationProblem,
                        asfOutput.x);
                // Create a new individual form the returned LS
                Individual newIndividual
                        = new Individual(optimizationProblem,
                                individualEvaluator, asfOutput.x);
                // In the ideal case, the resulting individual from local
                // search should be feasible. However, due to some rare and
                // subtle factors the resulting individual returned might be
                // infeasible. For example, if we are using Matlab
                // fmincon(...) to perform local search, any discrepancy
                // between the degrees of accuracy of fmincon(...) and our
                // Java code, might result in a marginally infeasible
                // individual. That's why the next check is performed to
                // make sure that the resulting individual is feasible
                // before adding it to the new population. Otherwise, it
                // is ignored.
                if (newIndividual.isFeasible()) {
                    // Add the new individual to the new population.
                    newPopSet.add(newIndividual);
                }
            } catch (MWException ex) {
                Logger.getLogger(NSGA3_Diversity_GECCO2016.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    /**
     * Looks for bad associations in order to enhance them (later) using local
     * search. The term "bad associations" refers to those reference directions
     * whose attached points are of rank 2 or more. For example, it is possible
     * in this algorithm to include a rank 4 individual into the next population
     * just because it is the only representative of its reference direction. In
     * the original NSGA-III algorithm, this individual would not have been
     * included unless all better ranked individuals are. However, in this
     * algorithm, this individual will be preferred over better ranked
     * individuals if it is the only representative of its direction. Hence a
     * local search can be very useful in such a case. Notice that no local
     * search is performed inside the body of this method. Only, some reference
     * directions are stored along with the points closest to, in order to
     * perform local search later on.
     *
     * @param newPopSet the new population being formed (added to)
     * @param localSearchDirsSet the set of reference directions upon which
     * local search will be performed later on
     * @param localSearchStartingPointsList the local search starting points
     * corresponding to the reference direction in the set
     */
    protected void coverRank2Associations(
            Set<Individual> newPopSet,
            Set<ReferenceDirection> localSearchDirsSet,
            List<Individual> localSearchStartingPointsList) {
        // If the empty directions are not sufficient, look for those
        // directions whose attached points are high in rank (at least
        // their rank is 2).
        if (localSearchDirsSet.size() < MAX_LOCAL_SEARCH_OPERATIONS_PER_GENERATION) {
            List<Individual> selectedSoFar = new ArrayList<Individual>(newPopSet);
            // Sort the individuals selected so far descendingly according on their
            // rank.
            for (int i = 0; i < selectedSoFar.size() - 1; i++) {
                for (int j = i + 1; j < selectedSoFar.size(); j++) {
                    if (selectedSoFar.get(j).getRank() > selectedSoFar.get(i).getRank()) {
                        Individual temp = selectedSoFar.get(i);
                        selectedSoFar.set(i, selectedSoFar.get(j));
                        selectedSoFar.set(j, temp);
                    }
                }
            }
            // Pick those individuals with the highest rank, as long as their
            // rank is 2 or more.
            for (int i = 0;
                    i < selectedSoFar.size()
                    && selectedSoFar.get(i).getRank() >= 2
                    && localSearchDirsSet.size() < MAX_LOCAL_SEARCH_OPERATIONS_PER_GENERATION;
                    i++) {
                localSearchStartingPointsList.add(selectedSoFar.get(i));
                localSearchDirsSet.add(selectedSoFar.get(i).getReferenceDirection());
            }
        }
    }

    /**
     * Complete the population by adding some of those members that have not
     * been included in the population yet. Individuals are added according to
     * their rank. Best ranked individuals are added to the new population until
     * it reaches the required population size.
     *
     * @param newPopSet the new population being formed (added to)
     * @param mergedPopulation the whole merged population (individuals)
     */
    protected void completeFillingNewPop(Set<Individual> newPopSet, Individual[] mergedPopulation) {
        if (newPopSet.size() < optimizationProblem.getPopulationSize()) {
            int rank = 1;
            outerLoop:
            while (true) {
                if (rank > getWorstRank(mergedPopulation)) {
                    // This will happen only if most members are exact copies.
                    break;
                }
                for (Individual individual : mergedPopulation) {
                    if (individual.getRank() == rank
                            && !newPopSet.contains(individual)) {
                        newPopSet.add(individual);
                        if (newPopSet.size()
                                == optimizationProblem.getPopulationSize()) {
                            break outerLoop;
                        }
                    }
                }
                rank++;
            }
            while (newPopSet.size() < optimizationProblem.getPopulationSize()) {
                // This will happen only if most members are exact copies.
                // In such a case use mutated copies of some randomly selected 
                // members
                Individual randomInd = new Individual(
                        mergedPopulation[RandomNumberGenerator.rnd(
                                0, mergedPopulation.length - 1)]);
                mutation_ind(randomInd);
                newPopSet.add(randomInd);
            }
        }
    }

    /**
     * Return the worst rank among these individuals
     *
     * @param individuals the individuals to check
     * @return the rank of the worst ranked individual(s) in the population.
     */
    private int getWorstRank(Individual[] individuals) {
        int worstRank = -1;
        for (Individual individual : individuals) {
            if (worstRank == -1 || individual.getRank() > worstRank) {
                worstRank = individual.getRank();
            }
        }
        return worstRank;
    }

    @Override
    public String getAlgorithmName() {
        return "nsga3_diversity_gecco2016";
    }
}
