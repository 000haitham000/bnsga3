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
public class ZDT3ModifiedEvaluator extends IndividualEvaluator {

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
        if(objectivesCount != 2) {
            throw new UnsupportedOperationException("# of objectives must be 2.");
        }
        return PerformanceMetrics.getZDT1ParetoFront(this, n);
    }

    public void updateIndividualObjectivesAndConstraints(
            OptimizationProblem problem,
            Individual individual) {

        double[] x = individual.real;

        /*
         System.out.println("--- X ---");
         System.out.println(Arrays.toString(x));
         System.out.println("--- REAL ---");
         System.out.println(Arrays.toString(individual.real));
         */

        double obj0 = Math.pow(x[0], 0.3);
        double sum = 0;
        for(int i = 1; i < x.length; i++) {
            sum += x[i];
        }
        double g = 1 + 9 / (x.length - 1) * sum;
        //double obj1 = g * (1 - Math.sqrt(x[0]/g));
        //Z_f2 = G .* (1-2.*sqrt_f1_all - 0.7*sin(2*pi*Z_f1)./(1+10*Z_f1.^4));
        //double obj1 = g * (1 - 2 * Math.sqrt(x[0]) - 0.7 * Math.sin(2 * Math.PI * x[0]) / (1 + 10 * Math.pow(x[0], 4)));
        double obj1 = g * (1 - 2 * Math.sqrt(obj0) - 0.7 * Math.sin(2 * Math.PI * obj0) / (1 + 10 * Math.pow(obj0, 4)));
        
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
