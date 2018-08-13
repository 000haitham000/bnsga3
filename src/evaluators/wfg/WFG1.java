/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package evaluators.wfg;

import emo.Individual;
import emo.OptimizationProblem;
import static evaluators.wfg.WFG.rSum;
import org.apache.commons.math3.exception.NotPositiveException;
import org.apache.commons.math3.exception.OutOfRangeException;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;
import parsing.IndividualEvaluator;

/**
 *
 * @author seadahai
 */
public class WFG1 extends IndividualEvaluator {

    int k, l, M;

    public WFG1(int k, int l, int M) {
        this.k = k;
        this.l = l;
        this.M = M;
    }

    @Override
    public void updateIndividualObjectivesAndConstraints(
            OptimizationProblem problem,
            Individual individual) {
        //int n = individual.real.length;
        double[] x = individual.real;
        // Calculate WFG1 objectives
        RealVector objVector = getObjectives(x);
        // Update the individual's objective function
        for (int i = 0; i < individual.getObjectivesCount(); i++) {
            individual.setObjective(i, objVector.getEntry(i));
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

    private RealVector getObjectives(double[] x) throws NotPositiveException, OutOfRangeException {
        // Get Normalized Y
        RealVector y = getNormalizedY(x, x.length);
        // First Transformation: s_linear
        RealVector t1 = t1_sLinearAll(y, x.length);
        // Second Transformation: b_flat
        RealVector t2 = t2_bFlatAll(t1, x.length);
        for (int i = 0; i < t2.getDimension(); i++) {
            if(t2.getEntry(i) < 0) {
                t2.setEntry(i, 0.0);
            }
        }
        //        roundTo(t2, k);
        // Third Transformation: b_poly
        RealVector t3 = WFG.bPoly(t2, 0.2);
        //        roundTo(t3, k);
        //        adjust(t3,k,1e-10);
        // Fourth transformation
        RealVector t4 = t4_rSumAll(t3, k, M);
        // Set scaling parameters
        RealVector s = getScalingParameters();
        // Set parameters
        double A = 5;
        double alpha = 1;
        // Calculate objective functions
        RealVector objVector = WFG.wfgConvexMixed(t4, s, A, alpha);
        return objVector;
    }

    private RealVector getScalingParameters() throws OutOfRangeException {
        // Set scaling parameters
        RealVector s = new ArrayRealVector(M);
        for (int i = 0; i < M; i++) {
            s.setEntry(i, 2 * (i + 1));
        }
        return s;
    }

    private RealVector getNormalizedY(double[] x, int n) throws OutOfRangeException {
        // Intialize y
        RealVector y = new ArrayRealVector(n);
        // Normalize y
        for (int i = 0; i < n; i++) {
            y.setEntry(i, x[i] / (2 * (i + 1)));
        }
        return y;
    }

    private RealVector t1_sLinearAll(RealVector y, int n) throws OutOfRangeException, NotPositiveException {
        RealVector sLinear = WFG.sLinear(y.getSubVector(k, n - k), 0.35);
        RealVector t1 = new ArrayRealVector(n);
        for (int i = 0; i < t1.getDimension(); i++) {
            if (i < k) {
                t1.setEntry(i, y.getEntry(i));
            } else {
                t1.setEntry(i, sLinear.getEntry(i - k));
            }
        }
        return t1;
    }

    private RealVector t2_bFlatAll(RealVector t1, int n) throws OutOfRangeException, NotPositiveException {
        RealVector bFlat = WFG.bFlat(t1.getSubVector(k, n - k), 0.8, 0.75, 0.85);
        RealVector t2 = new ArrayRealVector(n);
        for (int i = 0; i < t2.getDimension(); i++) {
            if (i < k) {
                t2.setEntry(i, t1.getEntry(i));
            } else {
                t2.setEntry(i, bFlat.getEntry(i - k));
            }
        }
        return t2;
    }

    /**
     * The fourth transformation of WFG1
     *
     * @param y input vector (from the previous transformation)
     * @param k number of position related parameters
     * @param M number of objectives
     * @return transformation result (a vector of size M)
     */
    public static RealVector t4_rSumAll(RealVector y, int k, int M) {
        // Initialize t
        RealVector t = new ArrayRealVector(M);
        // Fill t except for the last position
        for (int i = 0; i < M - 1; i++) {
            // Get the deignated subset of vector y
            int yStart = i * k / (M - 1) + 1 - 1;
            int yEnd = (i + 1) * k / (M - 1) - 1;
            RealVector y_ = y.getSubVector(yStart, yEnd - yStart + 1);
            // Create the corresponding weight vector
            RealVector w_ = new ArrayRealVector(y_.getDimension());
            for (int j = 0; j < w_.getDimension(); j++) {
                w_.setEntry(j, 2 * i * (k / (M - 1)) + 2 * (j + 1));
            }
            // Apply the weighted sun transformation
            t.setEntry(i, rSum(y_, w_));
        }
        // Initialize wm (list of weights of the last l variables)
        RealVector wm = new ArrayRealVector(y.getDimension() - k);
        for (int i = 0; i < wm.getDimension(); i++) {
            wm.setEntry(i, 2 * (k + i + 1));
        }
        // Apply the weighted sum transformation
        int yStart = k + 1 - 1;
        t.setEntry(M - 1, rSum(y.getSubVector(yStart, y.getDimension() - yStart), wm));
        // Return t
        return t;
    }

    private void adjust(RealVector t3, int k, double tol) {
        for (int i = k; i < t3.getDimension(); i++) {
            long integer = Math.round(t3.getEntry(i));
            double abs_x = Math.abs(t3.getEntry(i) - integer);
            if (abs_x < tol) {
                t3.setEntry(i, integer);
            }
        }
    }

    private void roundTo(RealVector t, int k) {
        for (int i = k; i < t.getDimension(); i++) {
            t.setEntry(i, Math.round(t.getEntry(i) * 1.0e4) / 1.0e4);
        }
    }

    public static void main(String[] args) {
        double[] x = {2.0000, 3.0000, 5.3479, 7.2087, 9.6};
        RealVector objectives = new WFG1(2, 2, 3).getObjectives(x);
        System.out.println(objectives);
    }

    @Override
    public double[] getIdealPoint() {
        return new double[this.M];
    }

    @Override
    public double[] getReferencePoint() {
        double[] referencePoint = new double[this.M];
        for (int i = 0; i < referencePoint.length; i++) {
            referencePoint[i] = 2*(i+1);
        }
        return referencePoint;
    }
}
