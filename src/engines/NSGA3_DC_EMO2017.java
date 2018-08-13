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
import emo.VirtualIndividual;
import static engines.NSGA3_ExtremeLocalSearch.LS_FREQUENCY;
import extremels.ExtremeLocalSearch;
import extremels.LocalSearchOutput2;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
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
public class NSGA3_DC_EMO2017 extends /*NSGA3_ExtremeLocalSearch*/NSGA3_Diversity_GECCO2016 implements InternalLocalSearchInterface {

    public NSGA3_DC_EMO2017(
            OptimizationProblem optimizationProblem,
            IndividualEvaluator individualEvaluator,
            int[] divisions,
            ExtremeLocalSearch extLS) {
        super(optimizationProblem, individualEvaluator, divisions, extLS);
    }

    public NSGA3_DC_EMO2017(
            OptimizationProblem optimizationProblem,
            IndividualEvaluator individualEvaluator,
            ExtremeLocalSearch extLS) {
        super(optimizationProblem, individualEvaluator, extLS);
    }

    /**
     * Looks for bad associations in order to enhance them (later) using local
     * search. The term "bad associations" refers to those reference directions
     * whose attached points have bad KKTPM values. For example, so far it could
     * have been the case that a rank 4 individual is already incorporated into
     * the next population just because it is the only representative of its
     * reference direction, while another rank 1 individual associated to
     * another direction is not. Remember that in the original NSGA-III
     * algorithm, this rank 4 individual would NOT have been included unless all
     * better ranked individuals have. However, in this algorithm, this
     * individual may be preferred over better ranked individuals if it is the
     * only representative of its direction. Hence, a local search can be very
     * useful in such a case starting from this rank-4 individual along its
     * corresponding direction. Notice that no local search is performed inside
     * the body of this method. Only, some reference directions are stored along
     * with the points closest to them, in order to perform local search later
     * on.
     *
     * @param newPopSet the new population being formed (added to)
     * @param localSearchDirsSet the set of reference directions upon which
     * local search will be performed later on
     * @param localSearchStartingPointsList the local search starting points
     * corresponding to the reference direction in the set
     */
    protected void selectWorstKKTPMPoints(
            Set<Individual> newPopSet,
            Set<ReferenceDirection> localSearchDirsSet,
            List<Individual> localSearchStartingPointsList) {
        boolean phase3InAction = false;
        // If the empty directions are not sufficient, look for those
        // directions whose attached points have the worst KKKTPM values.
        if (localSearchDirsSet.size() < MAX_LOCAL_SEARCH_OPERATIONS_PER_GENERATION) {
            phase3InAction = true;
            // Create an array from the set of solutions selected so far
            Individual[] selectedSoFar = newPopSet.toArray(new Individual[newPopSet.size()]);
            // Get the utopian point from the ideal point. Using the exact ideal
            // point will result in zeros in the weights vector which will in 
            // turn result in NaN extreme points values.
            double[] utopian = new double[this.currentIdealPoint.length];
            for (int i = 0; i < utopian.length; i++) {
                utopian[i] = this.currentIdealPoint[i] - 0.01 * Math.abs(this.currentIdealPoint[i]);
            }
            // Calculate the corressponding KKTPM values of these individuals
            KKTPM[] kktpmArr = kktCalculator.calculatePopulationKKT(selectedSoFar, utopian);
            if(this instanceof NSGA3_DC_TEVC) {
                for (int i = 0; i < selectedSoFar.length; i++) {
                    ((NSGA3_DC_TEVC)this).selectedSoFarKktpmMap.put(selectedSoFar[i], kktpmArr[i]);
                }
            }
            // Update the the number of additional function evaluations consumed so far becuase of numerical gradients
            for (int i = 0; i < kktpmArr.length; i++) {
                individualEvaluator.setFunctionEvaluationsCount(individualEvaluator.getFunctionEvaluationsCount() + kktpmArr[i].getFunEvalCount());
                individualEvaluator.setNumericalFunEvalCount(individualEvaluator.getNumericalFunctionEvalCount() + kktpmArr[i].getFunEvalCount());
            }
            // Sort the individuals selected so far descendingly according on their
            // KKTPM.
            for (int i = 0; i < selectedSoFar.length - 1; i++) {
                for (int j = i + 1; j < selectedSoFar.length; j++) {
                    if (kktpmArr[i].getKktpm() < kktpmArr[j].getKktpm()) {
                        // Swap the two individuals
                        Individual tempInd = selectedSoFar[i];
                        selectedSoFar[i] = selectedSoFar[j];
                        selectedSoFar[j] = tempInd;
                        // Swap their corresponding KKTPM values
                        KKTPM tempKKTPM = kktpmArr[i];
                        kktpmArr[i] = kktpmArr[j];
                        kktpmArr[j] = tempKKTPM;
                    }
                }
            }
            // Pick as many individuals starting from the highest to the lowest 
            // KKTPM values as long as there is room for more local search
            // operations.
            for (int i = 0;
                    i < selectedSoFar.length
                    && localSearchDirsSet.size() < MAX_LOCAL_SEARCH_OPERATIONS_PER_GENERATION;
                    i++) {
                if (!Double.isNaN(kktpmArr[i].getKktpm())) {
                    localSearchStartingPointsList.add(selectedSoFar[i]);
                    localSearchDirsSet.add(selectedSoFar[i].getReferenceDirection());
                }
            }
        }
        if(phase3InAction) {
            TestScript_EMO2017.printer.print("1");
        } else {
            TestScript_EMO2017.printer.print("0");
        }
    }

    /**
     * This method performs "internal" local search (LS) which is slightly 
     * different from the one in NSGA3_Diversity_GECCO2016. They both 
     * start by trying to cover empty reference directions (phase-1), then if 
     * there is still room for more LS they conduct more LS operations starting 
     * from the worst individuals found in the population so far (phase-2).
     * While phase-1 is exactly the same, phase-2 is performed differently. 
     * Here the worst points are identified using their KKTPM (instead of their 
     * rank). Which provides a finer grained selection process.
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
                optimizationProblem);
        int _fromEmptyDirs = localSearchDirsSet.size();
        System.out.format("\tFrom Empty RefDirs. = %d%n", _fromEmptyDirs);

        // Cover rank 2 associations if any exists and if there is still
        // room in the local search slots.
        selectWorstKKTPMPoints(newPopSet, localSearchDirsSet,
                localSearchStartingPointsList);
        int _fromRank2 = localSearchDirsSet.size() - _fromEmptyDirs;
        System.out.format("\tFrom Worst KKTPM Ind.'s = %d%n", _fromRank2);

        // At this point, we have exactly a number of direction/starting
        // point pairs equal to MAX_LOCAL_SEARCH_OPERATIONS_PER_GENERATION.
        // Convert the Set to a List for convenience.
        List<ReferenceDirection> localSearchDirsList
                = new ArrayList<>(localSearchDirsSet);

        // Perform local search on those selected directions and their
        // corressponding starting points.
        for (int i = 0; i < localSearchDirsList.size(); i++) {
            try {
                // Get the KKTPM of the current point
                double kktpm = kktCalculator.calculatePopulationKKT(
                        new Individual[]{localSearchStartingPointsList.get(i)},
                        currentIdealPoint)[0].getKktpm();
                // Perform local search
                LocalSearchOutput asfOutput = asfMinimizer.minimizeASF(
                        localSearchStartingPointsList.get(i).real,
                        Mathematics.getUnitVector(
                                localSearchDirsList.get(i).direction),
                        (int) (kktpm * MAX_FUNC_EVAL), // Only allow the required portion of the maximum allowable function evaliations
                        currentIdealPoint,
                        currentIntercepts,
                        NSGA3ConvergenceEngine.UTOPIAN_EPSILON);
                System.out.format("KKTPM = %5.4f -> FEs = %03d%n", kktpm, (int) (kktpm * MAX_FUNC_EVAL));
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
                Logger.getLogger(NSGA3_DC_EMO2017.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    @Override
    public String getAlgorithmName() {
        return "nsga3_dc_emo2017";
    }
}
