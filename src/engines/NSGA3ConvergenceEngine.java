/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package engines;

import utils.Mathematics;
import asf.LocalSearch;
import com.mathworks.toolbox.javabuilder.MWException;
import emo.OptimizationProblem;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import parsing.IndividualEvaluator;

/**
 *
 * @author Haitham
 */
public class NSGA3ConvergenceEngine extends NSGA3Engine {

    // Interval of performing local search (every how many generations)
    public static final int KKT_LOCAL_SERACH_INTERVAL = 1;
    // Maximum function evaluations of the mincon MATLAB function
    public static final int LOCAL_SEARCH_MAX_FUN_EVAL = 500;

    public NSGA3ConvergenceEngine(OptimizationProblem optimizationProblem, IndividualEvaluator individualEvaluator, int[] divisions) {
        super(optimizationProblem, individualEvaluator, divisions);
    }

    public NSGA3ConvergenceEngine(OptimizationProblem optimizationProblem, IndividualEvaluator individualEvaluator) {
        super(optimizationProblem, individualEvaluator);
    }

    public NSGA3ConvergenceEngine(OptimizationProblem optimizationProblem, IndividualEvaluator individualEvaluator, String directionsFilePath) throws IOException {
        super(optimizationProblem, individualEvaluator, directionsFilePath);
    }

    @Override
    protected void iterationEnd() {
        super.iterationEnd();
        try {
            // Do NOT perform local search if any of the intercepts is Zero.
            // Having all the intercepts as Zeros means that either the
            // popultaion contains ONLY ONE feasible solution, or that all
            // the feasible solutions in the population are replicates of the
            // same solution. On the other hand, having one(some) intercepts
            // as zero(s) means that all the solutions are WEAKLY DOMINATED.
            // In other words, no strong domination among all the individuals
            // of the population. NEITHER of the above two situations should
            // THEORITICALLY prevent local search, but having Zero intercept
            // values will result in NAN objective values (due to
            // normalization). So from a PRACTICAL point of view, if any of the
            // intercepts is Zero, the local search is totally avoided.
            boolean noZeroIntercepts = true;
            if (currentIntercepts == null) {
                noZeroIntercepts = false;
            } else {
                for (int i = 0; i < currentIntercepts.length; i++) {
                    if (Mathematics.compare(currentIntercepts[i], 0) == 0) {
                        noZeroIntercepts = false;
                        break;
                    }
                }
            }
            LocalSearch.LocalSearchSummary localSearchSummary = null;
            if (this.asfMinimizer != null && noZeroIntercepts) {
                if (kktCalculator != null
                        && currentGenerationIndex % KKT_LOCAL_SERACH_INTERVAL == 0.0) {
                    localSearchSummary = LocalSearch.asfSearchKKTBased(
                            optimizationProblem,
                            individualEvaluator,
                            asfMinimizer,
                            kktCalculator,
                            LOCAL_SEARCH_MAX_FUN_EVAL,
                            currentPopulation,
                            currentGenerationIndex,
                            currentIdealPoint,
                            currentIntercepts,
                            UTOPIAN_EPSILON);
                }
                if (localSearchSummary != null && localSearchSummary.getResultingIndividual() != null) {
                    individualEvaluator.setFunctionEvaluationsCount(
                            individualEvaluator.getFunctionEvaluationsCount() + localSearchSummary.getLocalSearchOutput().evaluationsCount);
                    System.out.format("Local Search Function Evaluations (current generation) = %10d%n", localSearchSummary.getLocalSearchOutput().evaluationsCount);
                    System.out.format("Total Function Evaluations - EA+local (all generation) = %10d%n", individualEvaluator.getFunctionEvaluationsCount());
                } else {
                    System.out.println("No local Search");
                }
            } else {
                if (asfMinimizer == null) {
                    System.out.println("No local Search");
                } else {
                    System.out.println("No local Search: Zero intercepts encountered");
                }
            }
        } catch (MWException ex) {
            Logger.getLogger(NSGA3ConvergenceEngine.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public String getAlgorithmName() {
        return "nsga3_convergence";
    }
}
