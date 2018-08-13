/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package engines;

import emo.Individual;
import emo.OptimizationProblem;
import java.io.FileNotFoundException;
import java.io.IOException;
import parsing.IndividualEvaluator;

/**
 *
 * @author seadahai
 */
public class ParameterlessUnsga3Engine extends UnifiedNSGA3Engine {

    public ParameterlessUnsga3Engine(OptimizationProblem optimizationProblem, IndividualEvaluator individualEvaluator) {
        super(optimizationProblem, individualEvaluator);
    }

    public ParameterlessUnsga3Engine(OptimizationProblem optimizationProblem, IndividualEvaluator individualEvaluator, int[] divisions) {
        super(optimizationProblem, individualEvaluator, divisions);
    }

    public ParameterlessUnsga3Engine(OptimizationProblem optimizationProblem, IndividualEvaluator individualEvaluator, String directionsFilePath) throws FileNotFoundException, IOException {
        super(optimizationProblem, individualEvaluator, directionsFilePath);
    }

    @Override
    protected void postOffspringCreation(Individual[] offspringPopulation) {
        super.postOffspringCreation(offspringPopulation);
        // Apply Parameterless Fitness Assignment
        applyParameterlessFitnessAssignment(currentPopulation, offspringPopulation);
    }

    private void applyParameterlessFitnessAssignment(Individual[] parents, Individual[] offspring) {
        // Intialize worst
        double[] worstObjectiveVales = new double[optimizationProblem.objectives.length];
        for (int i = 0; i < worstObjectiveVales.length; i++) {
            worstObjectiveVales[i] = Double.NEGATIVE_INFINITY;
        }
        // Update worst
        boolean feasibleExists = false;
        // Check parents
        for (Individual parent : parents) {
            for (int i = 0; i < optimizationProblem.objectives.length; i++) {
                if (parent.getTotalConstraintViolation() >= 0 && parent.getObjective(i) > worstObjectiveVales[i]) {
                    worstObjectiveVales[i] = parent.getObjective(i);
                    feasibleExists = true;
                }
            }
        }
        // Check offspring
        for (Individual child : offspring) {
            for (int i = 0; i < optimizationProblem.objectives.length; i++) {
                if (child.getTotalConstraintViolation() >= 0 && child.getObjective(i) > worstObjectiveVales[i]) {
                    worstObjectiveVales[i] = child.getObjective(i);
                    feasibleExists = true;
                }
            }
        }
        // Update the fitness
        if (feasibleExists) {
            // Parents
            for (Individual parent : parents) {
                if (parent.getTotalConstraintViolation() < 0) {
                    for (int i = 0; i < optimizationProblem.objectives.length; i++) {
                        parent.setObjective(i, worstObjectiveVales[i] + parent.getTotalConstraintViolation());
                    }
                }
            }
            // Offspring
            for (Individual child : offspring) {
                if (child.getTotalConstraintViolation() < 0) {
                    for (int i = 0; i < optimizationProblem.objectives.length; i++) {
                        child.setObjective(i, worstObjectiveVales[i] + child.getTotalConstraintViolation());
                    }
                }
            }
        } else {
            // Parents
            for (Individual parent : parents) {
                for (int i = 0; i < optimizationProblem.objectives.length; i++) {
                    parent.setObjective(i, parent.getTotalConstraintViolation());
                }
            }
            // Offspring
            for (Individual child : offspring) {
                for (int i = 0; i < optimizationProblem.objectives.length; i++) {
                    child.setObjective(i, child.getTotalConstraintViolation());
                }
            }
        }
        // Force all solutions to be feasible
        // Parents
        for (Individual parent : parents) {
            for (int i = 0; i < parent.getConstarintsCount(); i++) {
                parent.setConstraintViolation(i, 0);
            }
        }
        // Children
        for (Individual child : offspring) {
            for (int i = 0; i < child.getConstarintsCount(); i++) {
                child.setConstraintViolation(i, 0);
            }
        }
    }
}
