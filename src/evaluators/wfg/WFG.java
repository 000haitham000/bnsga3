/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package evaluators.wfg;

import org.apache.commons.math3.analysis.function.Abs;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;
import org.apache.commons.math3.stat.descriptive.summary.Sum;

/**
 *
 * @author seadahai
 */
public class WFG {
    
    public static final double TOL = 10^-30;

    /**
     * The rSum reduction transformation of the WFG problem suite.
     *
     * @param y input vector
     * @param w corresponding weight vector
     * @return the transformation result (a floating point number)
     */
    public static double rSum(RealVector y, RealVector w) {
        Sum sum = new Sum();
        return sum.evaluate(y.toArray(), w.toArray()) / sum.evaluate(w.toArray());
    }

    /**
     * The sLinear transformation of the WFG problem suite.
     *
     * @param y input vector
     * @param A input value
     * @return transformation result (another vector)
     */
    public static RealVector sLinear(RealVector y, double A) {
        RealVector numerator = absVector(y.mapAdd(-1 * A));
        RealVector denominator = absVector(floorVector(y.mapMultiply(-1).mapAdd(A)).mapAdd(A));
        return numerator.ebeDivide(denominator);
    }

    /**
     * The bPoly transformation of the WFG problem suite.
     *
     * @param y input vector
     * @param alpha power
     * @return transformation result (another vector)
     */
    public static RealVector bPoly(RealVector y, double alpha) {
        RealVector u = y.copy();
        for (int i = 0; i < u.getDimension(); i++) {
            u.setEntry(i, Math.pow(u.getEntry(i), alpha));
        }
        return u;
    }

    /**
     * The bFlat transformation of the WFG problem suite.
     * 
     * @param y input vector
     * @param A flat value
     * @param B flat start
     * @param C flat end
     * @return transformation result (another vector)
     */
    public static RealVector bFlat(RealVector y, double A, double B, double C) {
        ArrayRealVector zerosVector = new ArrayRealVector(y.getDimension());
        RealVector term1 = minVectors(zerosVector, floorVector(y.mapSubtract(B)))
                .mapMultiply(A)
                .ebeMultiply(y.mapMultiply(-1).mapAdd(B))
                .mapDivide(B);
        RealVector term2 = minVectors(zerosVector, floorVector(y.mapMultiply(-1).mapAdd(C)))
                .mapMultiply(1 - A)
                .ebeMultiply(y.mapSubtract(C))
                .mapDivide(1 - C);
        RealVector u = term1.subtract(term2).mapAdd(A);
//        for (int i = 0; i < u.getDimension(); i++) {
//            if(u.getEntry(i) < 0 && u.getEntry(i) > -1 * TOL) {
//                u.setEntry(i, 0);
//            }
//        }
        return u;
    }
    
    /**
     * Returns a vector containing the absolute values of the elements of the
     * original vector. The original vector remains intact.
     *
     * @param v The original vector.
     * @return A vector containing the corresponding absolute values.
     */
    public static RealVector absVector(RealVector v) {
        RealVector u = v.copy();
        for (int i = 0; i < u.getDimension(); i++) {
            u.setEntry(i, Math.abs(u.getEntry(i)));
        }
        return u;
    }

    /**
     * Returns a vector containing the floor values of the elements of the
     * original vector. The original vector remains intact.
     *
     * @param v The original vector.
     * @return A vector containing the corresponding floor values.
     */
    public static RealVector floorVector(RealVector v) {
        RealVector u = v.copy();
        for (int i = 0; i < u.getDimension(); i++) {
            u.setEntry(i, Math.floor(u.getEntry(i)));
        }
        return u;
    }

    /**
     * Returns a vector containing the minimum values of the two input vectors.
     *
     * @param u the first input vector
     * @param v the second input vector
     * @return the minimum values vector
     */
    public static RealVector minVectors(RealVector u, RealVector v) {
        if (u.getDimension() != v.getDimension()) {
            throw new IllegalArgumentException("The two input vectors must have the same dimension.");
        }
        RealVector minVect = new ArrayRealVector(u.getDimension());
        for (int i = 0; i < u.getDimension(); i++) {
            if (u.getEntry(i) < v.getEntry(i)) {
                minVect.setEntry(i, u.getEntry(i));
            } else {
                minVect.setEntry(i, v.getEntry(i));
            }
        }
        return minVect;
    }

    public static RealVector wfgConvexMixed(RealVector x, RealVector s, double A, double alpha) {
        // Set number of objectives
        int M = x.getDimension();
        // Calculate Shape Functions
        RealVector h = new ArrayRealVector(M);
        // h(1) ... Convex
        h.setEntry(0, 1);
        for(int i = 0; i < M - 1; i++) {
            h.setEntry(0, h.getEntry(0) * (1 - Math.cos(x.getEntry(i) * Math.PI / 2)));
        }
        // h(2) to h(M-1) ... Convex
        int m = 2;
        for(int i = m-1; i< M - 1; i++) {
            h.setEntry(i, 1);
            for(int j = 0; j < M - m; j++) {
                h.setEntry(i, h.getEntry(i) * (1 - Math.cos( x.getEntry(j)*Math.PI/2 )));
            }
            h.setEntry(i, h.getEntry(i) * (1 - Math.sin( x.getEntry(M-m) * Math.PI / 2 )));
        }
        // h(M) ... Mixed
        h.setEntry(M - 1, Math.pow(1 - x.getEntry(0) - Math.cos(x.getEntry(0) * Math.PI * 2 * A + Math.PI / 2) / ( 2 * Math.PI * A), alpha));
        // Calculate Objective Functions
        RealVector f = new ArrayRealVector(M);
        for (int i = 0; i < M; i++) {
            f.setEntry(i, x.getEntry(M - 1) + s.getEntry(i) * h.getEntry(i));
        }
        // Return the vector ofobjective values
        return f;
    }

    public static void main(String[] args) {
        RealVector y = new ArrayRealVector(new double[]{1.2, 0.9, 5, 7.5, 8.8, 5});
        int k = 4;
        int M = 3;
        System.out.println("sLinear = " + sLinear(y, 0.35));
        System.out.println("min = "
                + minVectors(
                        new ArrayRealVector(new double[]{0.1, 90, 13}),
                        new ArrayRealVector(new double[]{0.2, 10, 55})
                )
        );
        System.out.println("bFlat = " + bFlat(y, 0.7, 0.4, 0.5));
    }
}
