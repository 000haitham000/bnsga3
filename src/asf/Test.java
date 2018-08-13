/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package asf;

import com.mathworks.toolbox.javabuilder.MWException;
import java.util.Arrays;

/**
 *
 * @author Haitham
 */
public class Test {

    public static void main(String[] args) throws MWException {
        //testOSY();
        testDTLZ1();
        //testDTLZ2();
    }

    private static void testOSY() throws MWException {
        ASF_Minimizer asfMinimizer = new OSY_ASF_Minimizer();
        double[] x0 = {5, 5, 3, 3, 3, 5};
        double[] w = {1 / Math.sqrt(2), 1 / Math.sqrt(2)};
        double[] idealPoint = {-300, 0};
        double[] intercepts = {298, 178};
        LocalSearchOutput output = asfMinimizer.minimizeASF(x0, w, 1000, idealPoint, intercepts, 0.0);
        System.out.format("X-Space: %s%n", Arrays.toString(output.x));
        System.out.format("F-Space: %s%n", Arrays.toString(output.f));
        System.out.format("Eval. #: %d%n%n", output.evaluationsCount);
    }

    private static void testDTLZ1() throws MWException {
        ASF_Minimizer asfMinimizer = new DTLZ1_ASF_Minimizer();
        double[] idealPoint = {0, 0, 0, 0};
        double[] intercepts = {0.5, 0.5, 0.5, 0.5};
        double[] w = {0.5, 0.5, 0.5, 0.5};
        // Initial point (starting guess)
//        double[] x0 = {0.9644869, 0.1044907, 0.6251464, 0.4107962, 0.7763123};
//        double[] x0 = {0.7500, 0.6667, 0.5000, 0.3001, 0.9998};
//        double[] x0 = {0.5, 0.5, 0.5, 0.5, 0.5};
        double[] x0 = {0.0, 0.0, 0.0, 0.5, 0.0};
        LocalSearchOutput output = asfMinimizer.minimizeASF(x0, w, 1000, idealPoint, intercepts, 0.0);
        System.out.format("X-Space: %s%n", Arrays.toString(output.x));
        System.out.format("F-Space: %s%n", Arrays.toString(output.f));
        System.out.format("Eval. #: %d%n", output.evaluationsCount);
    }


    private static void testDTLZ2() throws MWException {
        ASF_Minimizer asfMinimizer = new DTLZ2_ASF_Minimizer();
        double[] idealPoint = {0, 0, 0, 0};
        double[] intercepts = {0.5, 0.5, 0.5, 0.5};
        double[] w = {0.5, 0.5, 0.5, 0.5};
        //double[] w = {0.0001, 0.0001, 0.0001, 1};
        // Initial point (starting guess)
        //double[] x0 = {0.0, 0.0, 0.0, 0.0, 0.0, 0.5, 0.0};
        double[] x0 = {0.1, 0.1, 0.1, 0.0, 0.0, 0.5, 0.0};
        LocalSearchOutput output = asfMinimizer.minimizeASF(x0, w, 1000, idealPoint, intercepts, 0.0);
        System.out.format("X-Space: %s%n", Arrays.toString(output.x));
        System.out.format("F-Space: %s%n", Arrays.toString(output.f));
        System.out.format("Eval. #: %d%n", output.evaluationsCount);
    }
}
