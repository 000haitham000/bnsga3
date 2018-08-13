/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package evaluators;

import core.MathExpressionParser;
import core.VariablesManager;
import emo.Individual;
import emo.OptimizationProblem;
import exceptions.InvalidFormatException;
import exceptions.MisplacedTokensException;
import exceptions.TooManyDecimalPointsException;
import java.util.Arrays;
import javax.xml.stream.XMLStreamException;
import org.moeaframework.problem.CEC2009.CEC2009;
import parsing.IndividualEvaluator;
import parsing.InvalidOptimizationProblemException;

/**
 *
 * @author toshiba
 */
public class UF1Evaluator extends IndividualEvaluator {

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
    public Individual[] getParetoFront(int objectivesCount, int n)
            throws
            InvalidOptimizationProblemException,
            XMLStreamException {
        if (objectivesCount != 2) {
            throw new UnsupportedOperationException("# of objectives must be 2.");
        }
        // The following line should be replaced by an exact calculation of the
        // Pareto front.
        throw new UnsupportedOperationException(
                "Pareto front calculations is not available.");
    }

    @Override
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
        CEC2009.UF1(individual.real, f, individual.real.length);
        for (int i = 0; i < f.length; i++) {
            individual.setObjective(i, f[i]);
        }
    }

    public static void main(String[] args)
            throws
            IllegalAccessException,
            IllegalArgumentException,
            TooManyDecimalPointsException,
            MisplacedTokensException,
            InvalidFormatException,
            Throwable {
        VariablesManager vm = new VariablesManager();
        // Add PI to the variables manager
        vm.setConstant("pi", Math.PI);
        // Add a sample point to the variables manager
        double[] x0 = new double[]{0.7, -0.2, 0.8};
        vm.setVector("x", x0);
        // Evaluate the expression
        double f1 = MathExpressionParser.parse(
                "x[1] + 2 / 1 * sum{j, 3, 3, 2, (x[j] - sin(6 * pi * x[1] + j * pi / 3))^2}",
                vm).evaluate();
        double f2 = MathExpressionParser.parse(
                "1 - sqrt(x[1]) + 2 / 1 * sum{j, 2, 3, 2, (x[j] - sin(6 * pi * x[1] + j * pi / 3))^2}",
                vm).evaluate();
        // Display the result
        System.out.println("f1(tx2ex)  = " + f1);
        System.out.format("f1(manual) = %-15.10f%n", 0.7 + 2 * Math.pow(0.8 - Math.sin(6 * 0.7 * Math.PI + Math.PI), 2));
        System.out.println("f2(tx2ex)  = " + f2);
        System.out.format("f2(manual) = %-15.10f%n", 1 - Math.sqrt(0.7) + 2 * Math.pow(-0.2 - Math.sin(6 * 0.7 * Math.PI + 2.0 / 3 * Math.PI), 2));
    }
}
