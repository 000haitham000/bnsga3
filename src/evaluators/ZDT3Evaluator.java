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
import parsing.IndividualEvaluator;
import parsing.InvalidOptimizationProblemException;

/**
 *
 * @author toshiba
 */
public class ZDT3Evaluator extends IndividualEvaluator {

    public final static double[] NADIR_POINT = {0.8518328654, 1.0};
    public final static double[] IDEAL_POINT = {-0.0001, -1.0001};

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
        if(objectivesCount != 2) {
            throw new UnsupportedOperationException("# of objectives must be 2.");
        }
        return PerformanceMetrics.getZDT3ParetoFront(this, n);
    }

    @Override
    public void updateIndividualObjectivesAndConstraints(
            OptimizationProblem problem,
            Individual individual) {
        
        double[] x = individual.real;
        double obj0 = x[0];
        /*
        double obj1 = 
                (9.0 * (x[1] + x[2] + x[3] + x[4] + x[5] + x[6] + x[7] + x[8] + x[9] + x[10] + x[11] + x[12] + x[13] + x[14] + x[15] + x[16] + x[17] + x[18] + x[19] + x[20] + x[21] + x[22] + x[23] + x[24] + x[25] + x[26] + x[27] + x[28] + x[29]) / 29.0 + 1) *
            (1 - Math.sqrt(x[0] / (9.0 * (x[1] + x[2] + x[3] + x[4] + x[5] + x[6] + x[7] + x[8] + x[9] + x[10] + x[11] + x[12] + x[13] + x[14] + x[15] + x[16] + x[17] + x[18] + x[19] + x[20] + x[21] + x[22] + x[23] + x[24] + x[25] + x[26] + x[27] + x[28] + x[29]) / 29.0 + 1)) - (x[0] / (9.0 * (x[1] + x[2] + x[3] + x[4] + x[5] + x[6] + x[7] + x[8] + x[9] + x[10] + x[11] + x[12] + x[13] + x[14] + x[15] + x[16] + x[17] + x[18] + x[19] + x[20] + x[21] + x[22] + x[23] + x[24] + x[25] + x[26] + x[27] + x[28] + x[29]) / 29.0 + 1)) * Math.sin(10*3.14159*x[0]));
        */
        double sum = 0;
        for(int i = 1; i < individual.real.length; i++) {
            //sum += Math.pow(individual.real[i], 2);
            sum += individual.real[i];
        }
        double g = 1 + 9.0 / (individual.real.length - 1) * sum;
        double obj1 = g * (1 - Math.sqrt(obj0/g) - obj0 / g * Math.sin(10 * Math.PI * obj0));
        individual.setObjective(0, obj0);
        individual.setObjective(1, obj1);
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
}
