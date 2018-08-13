/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package parsing;

import core.MathExpressionParser;
import core.VariablesManager;
import utils.PerformanceMetrics;
import emo.Individual;
import emo.OptimizationProblem;
import emo.VirtualIndividual;
import exceptions.InvalidFormatException;
import exceptions.MisplacedTokensException;
import exceptions.TooManyDecimalPointsException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.stream.XMLStreamException;
import types.AbstractNode;

/**
 *
 * @author toshiba
 */
public class IndividualEvaluator {

    private AbstractNode[] objectivesArr;
    private AbstractNode[] constraintsArr;
    private VariablesManager vm = new VariablesManager();
    protected int funEvalCount = 0; // Total number of solution evaluations no
    // matter how were they consumed i.e. this field is intended to store those
    // evaluations consumed by the algorithm, numerical gradient calculations
    // and local search operations.
    private int numericalFunEvalCount = 0; // Notice that this field was added
    // long time after this class was originally created. Actually, this class
    // was one of the first classes in this library, which suggests that it was
    // coded in 2013, since then only one field has been used to store all kinds
    // of solution evaluations. However, in 2018, and in response to an IEEE
    // reviewer's comment on the B-NSGA-III paper, we needed to report the
    // percentage of computational budget (number of solution evaluations)
    // consumed by numerical gradient calculations. That's when we added this
    // new field which is intended to store only the number of solution
    // evaluations used in numerical gradient calculations. Notice that the
    // original field still stores the total number of solution evaluations no
    // matter how were they consumed.

    protected IndividualEvaluator() {
    }

    public IndividualEvaluator(OptimizationProblem problem) {
        // Initialize your variables manager
        vm = new VariablesManager();
        for (int i = 0; i < problem.variablesSpecs.length; i++) {
            vm.set(problem.variablesSpecs[i].getName(), problem.variablesSpecs[i].getMinValue());
        }
        try {
            // Prepare objectives evaluators for future use
            objectivesArr = new AbstractNode[problem.objectives.length];
            for (int i = 0; i < problem.objectives.length; i++) {
                String func = problem.objectives[i].getExpression();
                objectivesArr[i] = MathExpressionParser.parse(func, vm);
            }
            if (problem.constraints != null) {
                // Prepare constraints evaluators for future use
                constraintsArr = new AbstractNode[problem.constraints.length];
                for (int i = 0; i < problem.constraints.length; i++) {
                    String func = problem.constraints[i].getExpression();
                    constraintsArr[i] = MathExpressionParser.parse(func, vm);
                }
            }
        } catch (Throwable ex) {
            Logger.getLogger(IndividualEvaluator.class.getName()).log(Level.SEVERE, null, ex);
            System.exit(-1);
        }
    }

    public double[] getReferencePoint() {
        throw new UnsupportedOperationException("getReferencePoint() method must be overriden with appropriate logic.");
    }

    public double[] getIdealPoint() {
        throw new UnsupportedOperationException("getIdealPoint() method must be overriden with appropriate logic.");
    }

    public VirtualIndividual[] getParetoFront(int objectivesCount, int n) throws InvalidOptimizationProblemException, XMLStreamException {
        throw new UnsupportedOperationException("getParetoFront() method must be overriden with appropriate logic.");
    }

    public void updateIndividualObjectivesAndConstraints(
            OptimizationProblem problem,
            Individual individual) {
        DecimalFormat decimalFormat = new DecimalFormat("##########.##########");
        int realCounter = 0;
        int binaryCounter = 0;
        for (int i = 0; i < problem.variablesSpecs.length; i++) {
            // Get the name of the variable
            String varName = problem.variablesSpecs[i].getName();
            // Get the value of the variable from the individual
            double value;
            if (problem.variablesSpecs[i] instanceof OptimizationProblem.BinaryVariableSpecs) {
                // If the variable is a binary variable get its corresponding
                // decimal value.
                value = individual.binary[binaryCounter].getDecimalValue();
                binaryCounter++;
            } else {
                // If the variable is real get its value directly
                value = individual.real[realCounter];
                realCounter++;
            }
            // Replace each variable in the expression with its value
            vm.set(varName, value);
        }
        // Evaluate the final expression and store the results as the individual's objective values.
        for (int i = 0; i < problem.objectives.length; i++) {
            individual.setObjective(i, objectivesArr[i].evaluate());
        }
        // Increase Evaluations Count by One (counted per individual)
        funEvalCount++;
        // Announce that objective function values are valid
        individual.validObjectiveFunctionsValues = true;
        // Update constraint violations if constraints exist
        if (problem.constraints != null) {
            // Evaluate the final expression and store the results as the individual's constraints values.
            for (int i = 0; i < problem.constraints.length; i++) {
                double cv = constraintsArr[i].evaluate();
                if (cv > 0) {
                    cv = 0;
                }
                individual.setConstraintViolation(i, cv);
            }
            // Announce that objective function values are valid
            individual.validConstraintsViolationValues = true;
        }
    }

    /**
     * @return the funEvalCount
     */
    public int getFunctionEvaluationsCount() {
        return funEvalCount;
    }

    public void setFunctionEvaluationsCount(int individualEvaluationsCount) {
        this.funEvalCount = individualEvaluationsCount;
    }

    public void resetFunctionEvaluationsCount() {
        funEvalCount = 0;
        numericalFunEvalCount = 0;
    }

    /**
     * @return the numericalFunEvalCount
     */
    public int getNumericalFunctionEvalCount() {
        return numericalFunEvalCount;
    }

    /**
     * @param numericalFunEvalCount the numericalFunEvalCount to set
     */
    public void setNumericalFunEvalCount(int numericalFunEvalCount) {
        this.numericalFunEvalCount = numericalFunEvalCount;
    }
}
