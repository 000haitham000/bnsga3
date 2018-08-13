/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package engines;

import asf.LocalSearchOutput;
import com.mathworks.toolbox.javabuilder.MWException;
import emo.Individual;
import emo.IndividualsSet;
import emo.OptimizationProblem;
import emo.OptimizationUtilities;
import emo.VirtualIndividual;
import static engines.NSGA3_ExtremeLocalSearch.LS_FREQUENCY;
import extremels.ExtremeLocalSearch;
import extremels.LocalSearchOutput2;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import kktpm.KKTPMCalculator;
import optimization.TestScript_EMO2017;
import parsing.IndividualEvaluator;
import parsing.KKTPM;
import parsing.XMLParser;
import reference_directions.ReferenceDirection;
import utils.InputOutput;
import utils.Mathematics;
import utils.RandomNumberGenerator;

/**
 *
 * @author Haitham
 */
public class NSGA3_DC_TEVC extends NSGA3_DC_EMO2017 implements InternalLocalSearchInterface {

    private final double[] refDirsToLastStartDist;

    public NSGA3_DC_TEVC(
            OptimizationProblem optimizationProblem,
            IndividualEvaluator individualEvaluator,
            int[] divisions,
            ExtremeLocalSearch extLS) {
        super(optimizationProblem, individualEvaluator, divisions, extLS);
        refDirsToLastStartDist = new double[referenceDirectionsList.size()];
        Arrays.fill(refDirsToLastStartDist, Double.POSITIVE_INFINITY);
    }

    public NSGA3_DC_TEVC(
            OptimizationProblem optimizationProblem,
            IndividualEvaluator individualEvaluator,
            ExtremeLocalSearch extLS) {
        super(optimizationProblem, individualEvaluator, extLS);
        refDirsToLastStartDist = new double[referenceDirectionsList.size()];
        Arrays.fill(refDirsToLastStartDist, Double.POSITIVE_INFINITY);
    }

    /**
     * This map is created specifically to store the KKTPM of the points whose
     * KKTPM has already been calculated so far, in order to prevent repeated
     * calculations.
     */
    public HashMap<Individual, KKTPM> selectedSoFarKktpmMap = new HashMap<>();

    /**
     * This method performs "internal" local search (LS) which is slightly
     * different from the one in NSGA3_Diversity_GECCO2016. They both start by
     * trying to cover empty reference directions (phase-2), then if there is
     * still room for more LS they conduct more LS operations starting from the
     * worst individuals found in the population so far (phase-3). While phase-2
     * is exactly the same, phase-3 is performed differently. Here the worst
     * points are identified using their KKTPM (instead of their rank). Which
     * provides a finer grained selection process.
     *
     * @param newPopSet the new population to which new solutions are added
     * @param feasibleOnly all feasible solutions
     * @param distanceMatrix distance matrix (coming out of association)
     * @throws EvaluationException
     */
    @Override
    public void populationLocalSearch(
            Set<Individual> newPopSet,
            Individual[] feasibleOnly,
            double[][] distanceMatrix) {
        // The set of directions that might need to be locally searched
        Set<ReferenceDirection> localSearchDirsSet
                = new HashSet<ReferenceDirection>();
        // The list of LS starting points corresponding to those direction
        List<Individual> localSearchStartingPointsList
                = new ArrayList<Individual>();

        // Cover empty reference directions if any existed
        LocalSearchUtils.coverEmptyRefDirs(newPopSet, localSearchDirsSet,
                localSearchStartingPointsList, feasibleOnly, distanceMatrix,
                referenceDirectionsList, MAX_LOCAL_SEARCH_OPERATIONS_PER_GENERATION,
                optimizationProblem, refDirsToLastStartDist);
        int _fromEmptyDirs = localSearchDirsSet.size();
        System.out.format("\tFrom Empty RefDirs. = %d%n", _fromEmptyDirs);

        // Cover worst KKTPM points if there is still room in the available 
        // local search slots.
        selectWorstKKTPMPoints(newPopSet, localSearchDirsSet,
                localSearchStartingPointsList);
        int _fromWorstKKTPM = localSearchDirsSet.size() - _fromEmptyDirs;
        System.out.format("\tFrom Worst KKTPM Ind.'s = %d%n", _fromWorstKKTPM);

        // At this point, we have exactly a number of direction/starting
        // point pairs equal to MAX_LOCAL_SEARCH_OPERATIONS_PER_GENERATION.
        // Convert the Set to a List for convenience.
        List<ReferenceDirection> localSearchDirsList
                = new ArrayList<>(localSearchDirsSet);

        // Perform local search on those selected directions and their
        // corressponding starting points.
        for (int i = 0; i < localSearchDirsList.size(); i++) {
            try {

                /**
                 * The following code has been removed (commented) because it
                 * did NOT make sense. If the maximum number of allowed function
                 * evaluations is to be proportional to some poximity metric, 
                 * this metric should be calculated using the scalarized version
                 * of the problem, not the original multiobjective version of 
                 * it. For example, a Pareto optimal point that is very far from
                 * the desired point may still need many FEs to reach the
                 * desired point. Relating the FE limit to its multiobjective
                 * KKTPM (which will be close to zero as a Pareto optimal) will 
                 * not give it enough FEs.
                 */
//                // --- Use a KKTPM proportional FE limit - STRAT ---
//                // Get the KKTPM of the current point (get it from the hashmap 
//                // if possible to prevent repeated calculations)
//                KKTPM kktpm = selectedSoFarKktpmMap.get(localSearchStartingPointsList.get(i));
//                if (kktpm == null) {
//                    // Notice that the required KKTPM value may not exist in the
//                    // Hasmap. How? Consider the following situation. An empty 
//                    // reference direction is added to the list of directions to 
//                    // be locally searched along with its starting point (the 
//                    // closest point in the first front). This starting point 
//                    // might have not been among the points selected so far 
//                    // (selectedSoFar) and thus its KKTPM was not calculated, 
//                    // which means that its KKTPM has not been added to the 
//                    // hashmap (selectedSoFarKktpmMap) and its KKTPM should be 
//                    // calculated right now.
//                    Individual[] startingPoint = new Individual[]{localSearchStartingPointsList.get(i)};
//                    kktpm = kktCalculator.calculatePopulationKKT(
//                            startingPoint,
//                            OptimizationUtilities.pullPointBack(currentIdealPoint, 0.01))[0];
//                    // Update FE to reflect numerical gradient evaluations
//                    int solutionEvaluations = 1; //kktpm.getFunEvalCount();
//                    individualEvaluator.setFunctionEvaluationsCount(
//                            individualEvaluator.getFunctionEvaluationsCount()
//                            + solutionEvaluations + kktpm.getFunEvalCount());
//                }
//                // Perform local search
//                LocalSearchOutput asfOutput = asfMinimizer.minimizeASF(
//                        localSearchStartingPointsList.get(i).real,
//                        Mathematics.getUnitVector(
//                                localSearchDirsList.get(i).direction),
//                        (int) (kktpm.getKktpm() * MAX_FUNC_EVAL), // Only allow the required portion of the maximum allowable function evaliations
//                        //MAX_FUNC_EVAL,
//                        currentIdealPoint,
//                        currentIntercepts,
//                        NSGA3ConvergenceEngine.UTOPIAN_EPSILON);
//                System.out.format("KKTPM = %5.4f -> FEs = %03d%n", kktpm.getKktpm(), (int) (kktpm.getKktpm() * MAX_FUNC_EVAL));
//                // --- Use a KKTPM proportional FE limit - STRAT ---

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
                Logger.getLogger(NSGA3_DC_TEVC.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    /**
     * Uncomment the following method to mutate any repeating individuals in the
     * population. This procedure is helpful in maintaining diversity especially
     * with small population sizes.
     */
    /*
    @Override
    protected Individual[] getOffspringPopulation(Individual[] oldPopulation) {
        Individual[] offspringPopulation = super.getOffspringPopulation(oldPopulation); //To change body of generated methods, choose Tools | Templates.
        for (int i = 0; i < offspringPopulation.length - 1; i++) {
            for (int j = i + 1; i < offspringPopulation.length; i++) {
                if(offspringPopulation[i].equals(offspringPopulation[j])) {
                    mutation_ind(offspringPopulation[j]);
                }
            }
        }
        return offspringPopulation;
    }
     */
    @Override
    protected void iterationStart() {
        super.iterationStart();
        // Remeber that new individuals introduced to the population have not been ranked yet.
        rankIndividuals(currentPopulation, epsilon, "");
        // I am not sure if this step is necessary but just in case.
        Individual[] feasibles = OptimizationUtilities.getFeasibleIndividuals(currentPopulation);
        associate(feasibles, referenceDirectionsList, currentIdealPoint, currentIntercepts, UTOPIAN_EPSILON, "");
    }

    public static int totalSelectionsCount = 0;
    public static int bothFeasibleCount = 0;

    @Override
    protected Individual tournamentSelect(IndividualsSet subset) {
        totalSelectionsCount++;
        Individual individual1 = subset.getIndividual1();
        Individual individual2 = subset.getIndividual2();
        // If only one of the solutions is infeasible, return the feasible solution
        if (optimizationProblem.constraints != null
                && optimizationProblem.constraints.length != 0
                && (individual1.isFeasible() ^ individual2.isFeasible())) {
            // If the problem is constrained and one of the individuals
            // under investigation is feasible while the other is infeasible,
            // return the feasible one (which is normally the dominating
            // individual).
            if (individual1.isFeasible()) {
                return individual1;
            } else {
                return individual2;
            }
        } else if (!individual1.isFeasible() && !individual2.isFeasible()) {
            // If both the two solutions are infeasible, return the less violating.
            if (Mathematics.compare(
                    individual1.getTotalConstraintViolation(),
                    individual2.getTotalConstraintViolation()) == 1) {
                // individual1 is less violating (remember: the more negative
                // the value, the more the violation)
                return individual1;
            } else {
                // individual2 is less violating
                return individual2;
            }
        } //else if (optimizationProblem.objectives.length == 1) {
        // If we have only one objective and both solutions are feasible,
        // return the better in terms of objective value (i.e. the
        // dominating solution). If the two ranks are equal this means that
        // the two individuals are identical so return any of them.
        // (Remember: in the case of single objective, one idividual must
        // dominate the other unless both are identical to each other. This
        // is the only case where they will have the same rank)
        //if (individual1.dominates(individual2)) {
        //return individual1;
        //} else {
        //return individual2;
        //}
        //}
        else {
            if (currentGenerationIndex != 0 && individual1.getReferenceDirection().equals(individual2.getReferenceDirection())) {
                bothFeasibleCount++;
                // If both the two solutions are feasible. You have the
                // following two options:
                // If the two individuals belong to the same reference direction,
                // return the one with lower rank.
                if (individual1.getRank() < individual2.getRank()) {
                    return individual1;
                } else if (individual2.getRank() < individual1.getRank()) {
                    return individual2;
                } else {
                    // If they both belong to the same rank return the one
                    // closest to the reference direction.
                    if (Mathematics.compare(
                            individual1.getPerpendicularDistance(),
                            individual2.getPerpendicularDistance()) == -1) {
                        return individual1;
                    } else {
                        return individual2;
                    }
                }
            } else {
                // If the two individuals are associated with two different reference
                // directions, then return one of them randomly
                if (RandomNumberGenerator.randomperc() <= 0.5) {
                    return individual1;
                } else {
                    return individual2;
                }
            }
        }
    }

    @Override
    public String getAlgorithmName() {
        return "nsga3_dc_tevc";
    }
}
