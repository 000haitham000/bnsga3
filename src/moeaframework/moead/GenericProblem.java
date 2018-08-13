/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package moeaframework.moead;

import emo.Individual;
import emo.OptimizationProblem;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.moeaframework.core.Solution;
import org.moeaframework.core.variable.EncodingUtils;
import org.moeaframework.problem.AbstractProblem;
import parsing.IndividualEvaluator;

/**
 *
 * @author Haitham
 */
public class GenericProblem extends AbstractProblem {

    private OptimizationProblem problem;
    private IndividualEvaluator evaluator;

    public GenericProblem(OptimizationProblem problem, IndividualEvaluator evaluator) {
        super(problem.getRealVariablesCount(), problem.objectives.length);
        this.problem = problem;
        this.evaluator = evaluator;
    }

    @Override
    public void evaluate(Solution solution) {
        double[] x = EncodingUtils.getReal(solution);
        Individual individual = new Individual(problem, evaluator, x);
//        System.arraycopy(x, 0, individual.real, 0, x.length);
//        evaluator.updateIndividualObjectivesAndConstraints(problem, individual);
        for (int i = 0; i < problem.objectives.length; i++) {
            solution.setObjective(i, individual.getObjective(i));
        }
    }

    @Override
    public Solution newSolution() {
        Solution solution = new Solution(problem.getRealVariablesCount(), problem.objectives.length);
        for (int i = 0; i < problem.getRealVariablesCount(); i++) {
            OptimizationProblem.Variable varSpecs = getVarSpecs(i, problem.variablesSpecs);
            solution.setVariable(i, EncodingUtils.newReal(varSpecs.getMinValue(), varSpecs.getMaxValue()));
        }
        return solution;
    }

    private OptimizationProblem.Variable getVarSpecs(int varIndex, OptimizationProblem.Variable[] variablesSpecs) {
        int tempIndex = -1;
        for (int i = 0; i < variablesSpecs.length; i++) {
            if (variablesSpecs[i] instanceof OptimizationProblem.RealVariableSpecs) {
                tempIndex++;
                if (tempIndex == varIndex) {
                    return variablesSpecs[i];
                }
            }
        }
        throw new UnsupportedOperationException(String.format("You real variables are less than %d.", varIndex));
    }
}
