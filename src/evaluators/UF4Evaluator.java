/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package evaluators;

import utils.PerformanceMetrics;
import emo.Individual;
import emo.OptimizationProblem;
import java.util.Arrays;
import javax.xml.stream.XMLStreamException;
import org.moeaframework.problem.CEC2009.CEC2009;
import parsing.IndividualEvaluator;
import parsing.InvalidOptimizationProblemException;

/**
 *
 * @author toshiba
 */
public class UF4Evaluator extends IndividualEvaluator {

    public final static double[] NADIR_POINT = {1.0, 1.0};
    public final static double[] IDEAL_POINT = {-0.001, -0.001};

    @Override
    public double[] getReferencePoint() {
        return Arrays.copyOf(NADIR_POINT, NADIR_POINT.length);
    }

    @Override
    public double[] getIdealPoint() {
        return Arrays.copyOf(IDEAL_POINT, IDEAL_POINT.length);
    }

    @Override
    public Individual[] getParetoFront(int objectivesCount, int n) throws InvalidOptimizationProblemException, XMLStreamException {
        if (objectivesCount != 2) {
            throw new UnsupportedOperationException("# of objectives must be 2.");
        }
        // The following line should be replaced by an exact calculation of the
        // Pareto front.
        throw new UnsupportedOperationException(
                "Pareto front calculations is not available.");
    }

    public void updateIndividualObjectivesAndConstraints(
            OptimizationProblem problem,
            Individual individual) {
        // Calculate objectives
        calculateObjectives(individual);
        // Increase Evaluations Count by One (counted per individual)
        funEvalCount++;
        // Announce that objective function values are valid
        individual.validObjectiveFunctionsValues = true;
        // Update constraint violations if constraints exist
        if (problem.constraints != null) {
            // Evaluate the final expression and store the results as the individual's constraints values.
            for (int i = 0; i < problem.constraints.length; i++) {
                individual.setConstraintViolation(i, 0.0);
            }
            // Announce that objective function values are valid
            individual.validConstraintsViolationValues = true;
        }
    }

    private void calculateObjectives(Individual individual) {
        double[] f = new double[2];
        CEC2009.UF4(individual.real, f, individual.real.length);
        for (int i = 0; i < f.length; i++) {
            individual.setObjective(i, f[i]);
        }
    }
}
