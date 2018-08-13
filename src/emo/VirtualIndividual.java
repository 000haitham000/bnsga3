/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package emo;

import parsing.IndividualEvaluator;

/**
 *
 * @author Haitham
 */
public class VirtualIndividual {

    protected double[] objectiveFunction;
    // The following booleans must be set to false after any operation
    // that might introduce modification to any of the variables
    // (e.g. mutation or crossover).
    public boolean validObjectiveFunctionsValues = true;

    public VirtualIndividual(int objectivesCount) {
        objectiveFunction = new double[objectivesCount];
    }

    public VirtualIndividual(VirtualIndividual individual) {
        this(individual.objectiveFunction.length);
        System.arraycopy(individual.objectiveFunction, 0, this.objectiveFunction, 0, individual.objectiveFunction.length);
        this.validObjectiveFunctionsValues = individual.validObjectiveFunctionsValues;
    }

    public double getObjective(int objectiveIndex) {
        if (validObjectiveFunctionsValues) {
            return objectiveFunction[objectiveIndex];
        } else {
            throw new InvalidObjectiveValue();
        }
    }

    public int getObjectivesCount() {
        return objectiveFunction.length;
    }

    public void setObjective(int objectiveIndex, double objectiveValue) {
        objectiveFunction[objectiveIndex] = objectiveValue;
    }
}
