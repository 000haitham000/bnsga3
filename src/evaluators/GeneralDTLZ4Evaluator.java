/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package evaluators;

import emo.Individual;
import emo.OptimizationProblem;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.xml.stream.XMLStreamException;
import parsing.IndividualEvaluator;
import parsing.InvalidOptimizationProblemException;

/**
 *
 * @author toshiba
 */
public class GeneralDTLZ4Evaluator extends IndividualEvaluator {

    public static double[] NADIR_POINT;
    public static double[] IDEAL_POINT;
    public static boolean scaled = false;
    public static final int ALPHA = 20;
    public static final int D = 100;

    public GeneralDTLZ4Evaluator(OptimizationProblem problem) {
        // Ideal Point: All objectives are Zero
        IDEAL_POINT = new double[problem.objectives.length];
        // Nadir Point: All objectives are 0.5
        NADIR_POINT = new double[problem.objectives.length];
        for (int i = 0; i < NADIR_POINT.length; i++) {
            NADIR_POINT[i] = 1;
        }
    }

    private GeneralDTLZ4Evaluator() {
    }

    ; // For testing only (used inside the
    // main method of this class)

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
        throw new UnsupportedOperationException("Pareto front unavailable");
    }

    @Override
    public void updateIndividualObjectivesAndConstraints(
            OptimizationProblem problem,
            Individual individual) {
        // Design Variables (in DTLZ1 all variables must be real)
        double[] x = individual.real;
        // number of objectives
        int m = problem.objectives.length;
        double[] obj = getObjectives(x, m);
        // Copy the objective values to the actual individuals
        for (int i = 0; i < obj.length; i++) {
            individual.setObjective(i, obj[i]);
        }
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

    private double[] getObjectives(double[] x, int m) throws UnsupportedOperationException {
        // Number of design variables
        int n = x.length;
        // (g) calculations
        double summation = 0;
        for (int i = m - 1; i < n; i++) {
            summation += Math.pow(x[i] - 0.5, 2);
        }
        double g = summation;
        // Create objective functions
        double[] obj = new double[m];
        for (int i = 0; i < m; i++) {
            double objValue = 1 + g * D;
            for (int j = 0; j < m - i - 1; j++) {
                objValue *= Math.cos(Math.pow(x[j], ALPHA) * Math.PI / 2);
            }
            if (i != 0) {
                objValue *= Math.sin(Math.pow(x[m - i - 1], ALPHA) * Math.PI / 2);
            }
            obj[i] = objValue;
        }
        // The following deals only with the scaled version of the problem
        if (isScaled()) {
            double base = 1;
            if (m <= 5) {
                base = 10;
            } else if (m <= 10) {
                base = 3;
            } else if (m <= 15) {
                base = 2;
            } else {
                throw new UnsupportedOperationException("Scaling is not supported at this number of objectives");
            }
            for (int i = 0; i < m; i++) {
                obj[i] = obj[i] * Math.pow(base, i);
            }
        }
        return obj;
    }

    /**
     * @return the scaled
     */
    public boolean isScaled() {
        return scaled;
    }

    /**
     * @param scaled the scaled to set
     */
    public void setScaled(boolean scaled) {
        this.scaled = scaled;
    }

    public static void main(String[] args) throws IOException {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(
                    new FileReader("D:\\kkt_nsga3\\MATLAB ASF\\ASF for DTLZ2\\gen_0000_var.dat"));
            List<double[]> objList = new ArrayList<double[]>();
            String line;
            while ((line = reader.readLine()) != null) {
                String[] splits = line.split(" ");
                double[] x = new double[splits.length];
                for (int i = 0; i < x.length; i++) {
                    x[i] = Double.parseDouble(splits[i]);
                }
                // Number of design variables
                int n = x.length;
                // number of objectives
                int m = 3;
                // Calculate objectives
                GeneralDTLZ4Evaluator tempObj = new GeneralDTLZ4Evaluator();
                tempObj.scaled = false;
                double[] obj = tempObj.getObjectives(x, m);
                // Add obj vector to the list
                objList.add(obj);
            }
            // Display obj vectors of all individuals
            for (double[] obj : objList) {
                for (int i = 0; i < obj.length; i++) {
                    System.out.format("%-15.7f ", obj[i]);
                }
                System.out.println();
            }
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
    }
}
