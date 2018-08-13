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
public class GeneralDTLZ8Evaluator extends IndividualEvaluator {

    public static double[] NADIR_POINT;
    public static double[] IDEAL_POINT;

    public GeneralDTLZ8Evaluator(OptimizationProblem problem) {
        if (problem.constraints.length != problem.objectives.length) {
            throw new IllegalArgumentException("For DTLZ8, the number of "
                    + "constraints must be equal to the number of objectives "
                    + "(by definition).");
        }
        // Ideal Point: All objectives are Zero
        IDEAL_POINT = new double[problem.objectives.length];
        // Nadir Point
        NADIR_POINT = new double[problem.objectives.length];
        for (int i = 0; i < NADIR_POINT.length - 1; i++) {
            NADIR_POINT[i] = 1.00;
        }
        NADIR_POINT[NADIR_POINT.length - 1] = 1;
    }

    private GeneralDTLZ8Evaluator() {
    }
    // For testing only (used inside the
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
        // number of objectives
        int m = problem.objectives.length;
        // Design Variables (in DTLZ problems all variables must be real)
        double[] x = individual.real;
        // Calculate objective values
        double[] obj = getObjectives(x, m);
        // Copy objective values to the individual
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
            // In DTLZ8, the number of constraint must be equal to the number
            // of objectives (by definition).
            int conCount = problem.constraints.length;
            double[] cons = getConstraints(conCount, obj);
            // Copy constrained values to the individual
            for (int i = 0; i < cons.length; i++) {
                individual.setConstraintViolation(i, cons[i]);
            }
            // Announce that objective function values are valid
            individual.validConstraintsViolationValues = true;
        }
    }

    private double[] getConstraints(int conCount, double[] obj) {
        double[] cons = new double[conCount];
        // Set all but the last constraint.
        for (int i = 0; i < conCount - 1; i++) {
            cons[i] = obj[obj.length - 1] + 4 * obj[i] - 1;
        }
        // Set the last constraint
        double min = Double.POSITIVE_INFINITY;
        for (int i = 0; i < obj.length - 1; i++) {
            for (int j = 0; j < obj.length - 1; j++) {
                if (i != j) {
                    double sum = obj[i] + obj[j];
                    if (sum < min) {
                        min = sum;
                    }
                }
            }
        }
        cons[conCount - 1] = 2 * obj[obj.length - 1] + min - 1;
        return cons;
    }

    private double[] getObjectives(double[] x, int m) throws UnsupportedOperationException {
        // Number of design variables
        int n = x.length;
        // Create objective functions
        double[] obj = new double[m];
        for (int i = 0; i < m; i++) {
            for (int j = (int) Math.floor(1.0 * i * n / m); j < (int) Math.floor(1.0 * (i + 1) * n / m); j++) {
                obj[i] += x[j];
            }
            obj[i] = obj[i] / Math.floor(1.0 * n / m);
        }
        return obj;
    }

    public static void main(String[] args) throws IOException {
        BufferedReader reader = null;
        try {
            //reader = new BufferedReader(new FileReader("PUT VAR FILE PATH HERE (each line should represent the design variables of a single individual separated by single spaces)"));
            reader = new BufferedReader(new FileReader("d:/asd.txt"));
            List<double[]> objList = new ArrayList<double[]>();
            List<double[]> conList = new ArrayList<double[]>();
            String line;
            while ((line = reader.readLine()) != null) {
                String[] splits = line.split(" ");
                double[] x = new double[splits.length];
                for (int i = 0; i < x.length; i++) {
                    x[i] = Double.parseDouble(splits[i]);
                }
                // Number of design variables
                int n = x.length;
                // Number of objectives
                int m = 3;
                // Number of constraints
                int conCount = m;
                // Calculate objectives & constraints
                GeneralDTLZ8Evaluator tempObj = new GeneralDTLZ8Evaluator();
                double[] obj = tempObj.getObjectives(x, m);
                double[] con = tempObj.getConstraints(conCount, obj);
                // Add obj/const vector to the list
                objList.add(obj);
                conList.add(con);
            }
            // Display obj/const vectors of all individuals
            for (int i = 0; i < objList.size(); i++) {
                System.out.format("OBJ: ");
                for (int j = 0; j < objList.get(i).length; j++) {
                    System.out.format("%-15.7f ", objList.get(i)[j]);
                }
                System.out.println();
                System.out.format("CON: ");
                for (int j = 0; j < conList.get(i).length; j++) {
                    System.out.format("%-15.7f ", conList.get(i)[j]);
                }
                System.out.println();
                System.out.println();
            }
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
    }
}
