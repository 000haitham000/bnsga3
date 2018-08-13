/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package emo;

import parsing.IndividualEvaluator;

/**
 *
 * @author toshiba
 */
public class OptimizationProblem {
    
    public static final int SINGLEOBJECTIVE = 0;
    public static final int MULTIOBJECTIVE = 1;
    public static final int MANYOBJECTIVE = 2;
    
    private String problemID;
    private int type;
    private double seed;
    public Variable[] variablesSpecs;
    public Objective[] objectives;
    public Constraint[] constraints;
    private int steps;
    private boolean adaptive;
    private int populationSize;
    private int generationsCount;
    private double realCrossoverProbability;
    private double realMutationProbability;
    private int realCrossoverDistIndex;
    private int realMutationDistIndex;
    private double binaryCrossoverProbability;
    private double binaryMutationProbabilty;
    
    public OptimizationProblem(
            String id,
            Variable[] variables,
            Objective[] objectives,
            Constraint[] constraints,
            int steps,
            boolean adaptive,
            int populationSize,
            int generationsCount,
            double realCrossoverProbabiltiy,
            int realCrossoverDistributionIndex,
            double realMutationProbability, 
            int realMutationDistributionIndex, 
            double binaryCrossoverProbability, 
            double binaryMutationProbability, 
            double seed) {
        this.problemID = id;
        this.variablesSpecs = variables;
        this.objectives = objectives;
        this.constraints = constraints;
        this.steps = steps;
        this.adaptive = adaptive;
        this.populationSize = populationSize;
        this.generationsCount = generationsCount;
        this.realCrossoverProbability = realCrossoverProbabiltiy;
        this.realCrossoverDistIndex = realCrossoverDistributionIndex;
        this.realMutationProbability = realMutationProbability;
        this.realMutationDistIndex = realMutationDistributionIndex;
        this.binaryCrossoverProbability = binaryCrossoverProbability;
        this.binaryMutationProbabilty = binaryMutationProbability;
        this.seed = seed;
    }

    /**
     * @return the id
     */
    public String getProblemID() {
        return problemID;
    }

    /**
     * @param id the id to set
     */
    public void setProblemID(String id) {
        this.problemID = id;
    }

    /**
     * @return the type
     */
    public int getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(int type) {
        this.type = type;
    }

    /**
     * @return the populationSize
     */
    public int getPopulationSize() {
        return populationSize;
    }

    /**
     * @param populationSize the populationSize to set
     */
    public void setPopulationSize(int populationSize) {
        this.populationSize = populationSize;
    }

    /**
     * @return the generationsCount
     */
    public int getGenerationsCount() {
        return generationsCount;
    }

    /**
     * @param generationsCount the generationsCount to set
     */
    public void setGenerationsCount(int generationsCount) {
        this.generationsCount = generationsCount;
    }

    /**
     * @return the sbxCrossoverProbability
     */
    public double getRealCrossoverProbability() {
        return realCrossoverProbability;
    }

    /**
     * @param sbxCrossoverProbability the sbxCrossoverProbability to set
     */
    public void setRealCrossoverProbability(double sbxCrossoverProbability) {
        this.realCrossoverProbability = sbxCrossoverProbability;
    }

    /**
     * @return the sbxMutationProbability
     */
    public double getRealMutationProbability() {
        return realMutationProbability;
    }

    /**
     * @param sbxMutationProbability the sbxMutationProbability to set
     */
    public void setRealMutationProbability(double sbxMutationProbability) {
        this.realMutationProbability = sbxMutationProbability;
    }

    /**
     * @return the sbxCrossoverDistIndex
     */
    public int getRealCrossoverDistIndex() {
        return realCrossoverDistIndex;
    }

    /**
     * @param sbxCrossoverDistIndex the sbxCrossoverDistIndex to set
     */
    public void setRealCrossoverDistIndex(int sbxCrossoverDistIndex) {
        this.realCrossoverDistIndex = sbxCrossoverDistIndex;
    }

    /**
     * @return the sbxMutationDistIndex
     */
    public int getRealMutationDistIndex() {
        return realMutationDistIndex;
    }

    /**
     * @param sbxMutationDistIndex the sbxMutationDistIndex to set
     */
    public void setRealMutationDistIndex(int sbxMutationDistIndex) {
        this.realMutationDistIndex = sbxMutationDistIndex;
    }

    /**
     * @return the binaryCrossoverProbability
     */
    public double getBinaryCrossoverProbability() {
        return binaryCrossoverProbability;
    }

    /**
     * @param binaryCrossoverProbability the binaryCrossoverProbability to set
     */
    public void setBinaryCrossoverProbability(double binaryCrossoverProbability) {
        this.binaryCrossoverProbability = binaryCrossoverProbability;
    }

    /**
     * @return the binaryMutationProbabilty
     */
    public double getBinaryMutationProbabilty() {
        return binaryMutationProbabilty;
    }

    /**
     * @param binaryMutationProbabilty the binaryMutationProbabilty to set
     */
    public void setBinaryMutationProbabilty(double binaryMutationProbabilty) {
        this.binaryMutationProbabilty = binaryMutationProbabilty;
    }

    /**
     * @return the seed
     */
    public double getSeed() {
        return seed;
    }

    /**
     * @param seed the seed to set
     */
    public void setSeed(double seed) {
        this.seed = seed;
    }

    /**
     * @return the steps
     */
    public int getSteps() {
        return steps;
    }

    /**
     * @param steps the steps to set
     */
    public void setSteps(int steps) {
        this.steps = steps;
    }

    /**
     * @return the adaptive
     */
    public boolean isAdaptive() {
        return adaptive;
    }

    /**
     * @param adaptive the adaptive to set
     */
    public void setAdaptive(boolean adaptive) {
        this.adaptive = adaptive;
    }

    @Override
    public String toString() {

        String variablesString = getVariablesAsString();
        String objectivesString = getObjectivesAsString();
        String constraintsString = getConstraintsAsString();
        
        return String.format("%s - %s - %s - "
                + "steps: %d - "
                + "population: %d - "
                + "generations: %d - "
                + "realCrossoverProb: %7.2f - "
                + "realMutationProb: %7.2f - "
                + "realCrossoverDistributionIndex: %d - "
                + "realMutationDistributionIndex: %d - "
                + "binaryCrossoverProb: %7.2f - "
                + "binaryMutationProb: %7.2f - "
                + "seed: %7.2f",
                variablesString,
                objectivesString,
                constraintsString,
                getSteps(),
                populationSize,
                generationsCount,
                realCrossoverProbability,
                realMutationProbability,
                realCrossoverDistIndex,
                realMutationDistIndex,
                binaryCrossoverProbability,
                binaryMutationProbabilty,
                seed);
    }

    private String getVariablesAsString() {
        // Variables string
        String variablesString = "Variables {";
        for(int i = 0; i < variablesSpecs.length; i++) {
            String varType;
            if(variablesSpecs[i] instanceof BinaryVariableSpecs) {
                int bitsCountIfBinary = ((BinaryVariableSpecs)variablesSpecs[i]).getNumberOfBits();
                varType = String.format("binary:%d bits", bitsCountIfBinary);
            } else {
                varType = "real";
            }
            variablesString +=
                    String.format("%s(%s) %8.3f:%-8.3f", variablesSpecs[i].getName(), varType, variablesSpecs[i].minValue, variablesSpecs[i].maxValue);
            if(i < variablesSpecs.length - 1) {
                variablesString += ", ";
            }
        }
        variablesString += "}";
        return variablesString;
    }

    private String getObjectivesAsString() {
        // Objectives string
        String objectivesString = "Objectives {";
        for(int i = 0; i < objectives.length; i++) {
            objectivesString += String.format(
                    "%s %s",
                    (objectives[i].getType() == Objective.MIN)?"Min.":"Max.",
                    objectives[i].getExpression());
            if(i < objectives.length - 1) {
                objectivesString += ", ";
            }
        }
        objectivesString += "}";
        return objectivesString;
    }

    private String getConstraintsAsString() {
        // Constraints string
        String constraintsString = "Constraints {";
        for(int i = 0; i < constraints.length; i++) {
            constraintsString += String.format(
                    "%s %s",
                    (constraints[i].getType() == Constraint.INEQUALITY)?"Ineq.":"Eq.",
                    constraints[i].getExpression());
            if(i < constraints.length - 1) {
                constraintsString += ", ";
            }
        }
        constraintsString += "}";
        return constraintsString;
    }

    public int getRealVariablesCount() {
        int realVarCount = 0;
        for (Variable variablesSpec : variablesSpecs) {
            if (variablesSpec instanceof RealVariableSpecs) {
                realVarCount++;
            }
        }
        return realVarCount;
    }

    public int getBinaryVariablesCount() {
        int binaryVarCount = 0;
        for (Variable variablesSpec : variablesSpecs) {
            if (variablesSpec instanceof BinaryVariableSpecs) {
                binaryVarCount++;
            }
        }
        return binaryVarCount;
    }

    public static abstract class Variable {

        private String name;
        private double minValue;
        private double maxValue;

        public Variable(String name, double minValue, double maxValue) {
            this.name = name;
            this.minValue = minValue;
            this.maxValue = maxValue;
        }

        /**
         * @return the name
         */
        public String getName() {
            return name;
        }

        /**
         * @param name the name to set
         */
        public void setName(String name) {
            this.name = name;
        }

        /**
         * @return the minValue
         */
        public double getMinValue() {
            return minValue;
        }

        /**
         * @param minValue the minValue to set
         */
        public void setMinValue(double minValue) {
            this.minValue = minValue;
        }

        /**
         * @return the maxValue
         */
        public double getMaxValue() {
            return maxValue;
        }

        /**
         * @param maxValue the maxValue to set
         */
        public void setMaxValue(double maxValue) {
            this.maxValue = maxValue;
        }
        
        public String toString() {
            return String.format("%-6s (%-10.3f:%10.3f)", this.getName(), this.getMinValue(), this.getMaxValue());
        }

    }

    public static class RealVariableSpecs extends Variable {

        public RealVariableSpecs(String name, double minValue, double maxValue) {
            super(name, minValue, maxValue);
        }
    }
    
    public static class BinaryVariableSpecs extends Variable {
        
        private int numberOfBits;

        public BinaryVariableSpecs(String name, double minValue, double maxValue, int numberOfBits) {
            super(name, minValue, maxValue);
            this.numberOfBits = numberOfBits;
        }
        
        /**
         * @return the numberOfBits
         */
        public int getNumberOfBits() {
            return numberOfBits;
        }

        /**
         * @param numberOfBits the numberOfBits to set
         */
        public void setNumberOfBits(int numberOfBits) {
            this.numberOfBits = numberOfBits;
        }
        
        public String toString() {
            return super.toString() + String.format("%3d bits", this.getNumberOfBits());
        }
    }
    
    public static class Objective {
        
        public static final int MIN = 0;
        public static final int MAX = 1;
        
        private String expression;
        private int type;

        /**
         * @return the objectiveExpression
         */
        public String getExpression() {
            return expression;
        }

        /**
         * @param objectiveExpression the objectiveExpression to set
         */
        public void setObjectiveExpression(String objectiveExpression) {
            this.expression = objectiveExpression;
        }

        /**
         * @return the type
         */
        public int getType() {
            return type;
        }

        /**
         * @param type the type to set
         */
        public void setType(int type) {
            this.type = type;
        }
        
        public Objective(int type, String expression) {
            this.type = type;
            this.expression = expression;
        }
        
        @Override
        public String toString() {
            return String.format("%-3s: %-50s", (type == MIN)?"MIN":"MAX", expression);
        }
    }

    public static class Constraint {
        
        public static final int INEQUALITY = 0;
        public static final int EQUALITY = 1;
        
        private String expression;
        private int type;

        /**
         * @return the expression
         */
        public String getExpression() {
            return expression;
        }

        /**
         * @param expression the expression to set
         */
        public void setExpression(String expression) {
            this.expression = expression;
        }

        /**
         * @return the type
         */
        public int getType() {
            return type;
        }

        /**
         * @param type the type to set
         */
        public void setType(int type) {
            this.type = type;
        }
        
        public Constraint(int type, String expression) {
            this.type = type;
            this.expression = expression;
        }
        
        @Override
        public String toString() {
            return String.format("%-3s: %-50s", (getType() == INEQUALITY)?"INEQUALITY":"EQUALITY", getExpression());
        }
    }
}
