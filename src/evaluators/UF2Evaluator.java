/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package evaluators;

import core.MathExpressionParser;
import core.VariablesManager;
import utils.PerformanceMetrics;
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
public class UF2Evaluator extends IndividualEvaluator {

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
        CEC2009.UF2(individual.real, f, individual.real.length);
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
        int n = x0.length;
        int j1Size = (int)Math.ceil((n - 2) / 2.0);
        int j2Size = (int)Math.floor(n / 2.0);
        // Evaluate the objective functions using tx2ex
        String yjOddExp = String.format("x[j] - (0.3 * x[1]^2 * cos(24 * pi * x[1] + 4 * j * pi / %d) + 0.6 * x[1]) * cos(6 * pi * x[1] + j * pi / %d)", n, n);
        String yjEvenExp = String.format("x[j] - (0.3 * x[1]^2 * cos(24 * pi * x[1] + 4 * j * pi / %d) + 0.6 * x[1]) * sin(6 * pi * x[1] + j * pi / %d)", n, n);
        String f1Exp = String.format("x[1] + 2 / %d * sum{j, 3, %d, 2, (%s)^2}", j1Size, n, yjOddExp);
        String f2Exp = String.format("1 - sqrt(x[1]) + 2 / %d * sum{j, 2, %d, 2, (%s)^2}", j2Size, n, yjEvenExp);
        double f1 = MathExpressionParser.parse(f1Exp, vm).evaluate();
        double f2 = MathExpressionParser.parse(f2Exp, vm).evaluate();
        // Evaluate the objective function using MOEA Framework
        double[] fMoeaFramework = new double[2];
        CEC2009.UF2(x0, fMoeaFramework, n);
        // Display the result
        System.out.format("%20s = %-15.10f%n", "f1(tx2ex)", f1);
        System.out.format("%20s = %-15.10f%n", "f1(MOEA Framework)", fMoeaFramework[0]);
        System.out.format("%20s = %-15.10f%n", "f2(tx2ex)", f2);
        System.out.format("%20s = %-15.10f%n", "f2(MOEA Framework)", fMoeaFramework[1]);
    }
}
