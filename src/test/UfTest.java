/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import com.mathworks.toolbox.javabuilder.MWClassID;
import com.mathworks.toolbox.javabuilder.MWException;
import com.mathworks.toolbox.javabuilder.MWNumericArray;
import extreme_uf1_2017b_lib.ExtremeUf1Optimizer;
import extreme_uf2_2017b_lib.ExtremeUf2Optimizer;
import java.util.Arrays;
import org.moeaframework.problem.CEC2009.CEC2009;

/**
 *
 * @author Haitham
 */
public class UfTest {

    public static void main(String[] args) throws MWException {
        testUf1();
        //testUf2();
    }

    private static void testUf1() throws MWException {
        ExtremeUf1Optimizer extOptimizer = new ExtremeUf1Optimizer();
        int varCount = 30;
        int pointCount = 10;
        double[] f1Matlab = new double[pointCount];
        double[] f2Matlab = new double[pointCount];
        double[] f1Java = new double[pointCount];
        double[] f2Java = new double[pointCount];
        double[][] allX = new double[pointCount][varCount];
        for (int i = 0; i < pointCount; i++) {
            double[] x = new double[varCount];
            x[0] = ((double) i) / (pointCount - 1);
            System.out.println("x[0] = " + x[0]);
            for (int j = 2; j <= varCount; j++) {
                x[j - 1] = Math.sin(6 * Math.PI * x[0] + j * Math.PI / varCount);
            }
            allX[i] = x;
            // Evaluate using MATLAB
            MWNumericArray pointX = new MWNumericArray(x, MWClassID.DOUBLE);
            Object[] f1Obj = extOptimizer.f1(1, pointX);
            Object[] f2Obj = extOptimizer.f2(1, pointX);
            f1Matlab[i] = ((double[][]) ((MWNumericArray) f1Obj[0]).toDoubleArray())[0][0];
            f2Matlab[i] = ((double[][]) ((MWNumericArray) f2Obj[0]).toDoubleArray())[0][0];
            // Evaluate Using Java
            double[] javaF = new double[2];
            CEC2009.UF1(x, javaF, x.length);
            f1Java[i] = javaF[0];
            f2Java[i] = javaF[1];
        }
        // Plot command for MATLAB results
        System.out.format("scatter(%s,%s)%n", Arrays.toString(f1Matlab), Arrays.toString(f2Matlab));
        // Plot command for Java results
        System.out.format("scatter(%s,%s)%n", Arrays.toString(f1Java), Arrays.toString(f2Java));
        // Display all points along with their objective values
        for (int i = 0; i < pointCount; i++) {
            System.out.format("%s >> (%10.7f, %10.7f)%n", Arrays.toString(allX[i]), f1Java[i], f2Java[i]);
        }
    }

    private static void testUf2() throws MWException {
        ExtremeUf2Optimizer extOptimizer = new ExtremeUf2Optimizer();
        int varCount = 30;
        int pointCount = 10;
        double[] f1Matlab = new double[pointCount];
        double[] f2Matlab = new double[pointCount];
        double[] f1Java = new double[pointCount];
        double[] f2Java = new double[pointCount];
        double[][] allX = new double[pointCount][varCount];
        for (int i = 0; i < pointCount; i++) {
            double[] x = new double[varCount];
            x[0] = ((double) i) / (pointCount - 1);
            System.out.println("x[0] = " + x[0]);
            for (int j = 2; j <= varCount; j++) {
                if (j % 2 == 1) { // J1
                    x[j - 1] = (0.3 * Math.pow(x[0], 2) * Math.cos(24 * Math.PI * x[0] + 4 * j * Math.PI / varCount) + 0.6 * x[0]) * Math.cos(6 * Math.PI * x[0] + j * Math.PI / varCount);
                } else { //J2
                    x[j - 1] = (0.3 * Math.pow(x[0], 2) * Math.cos(24 * Math.PI * x[0] + 4 * j * Math.PI / varCount) + 0.6 * x[0]) * Math.sin(6 * Math.PI * x[0] + j * Math.PI / varCount);
                }
            }
            allX[i] = x;
            // Evaluate using MATLAB
            MWNumericArray pointX = new MWNumericArray(x, MWClassID.DOUBLE);
            Object[] f1Obj = extOptimizer.f1(1, pointX);
            Object[] f2Obj = extOptimizer.f2(1, pointX);
            f1Matlab[i] = ((double[][]) ((MWNumericArray) f1Obj[0]).toDoubleArray())[0][0];
            f2Matlab[i] = ((double[][]) ((MWNumericArray) f2Obj[0]).toDoubleArray())[0][0];
            // Evaluate Using Java
            double[] javaF = new double[2];
            CEC2009.UF2(x, javaF, x.length);
            f1Java[i] = javaF[0];
            f2Java[i] = javaF[1];
        }
        // Plot command for MATLAB results
        System.out.format("scatter(%s,%s)%n", Arrays.toString(f1Matlab), Arrays.toString(f2Matlab));
        // Plot command for Java results
        System.out.format("scatter(%s,%s)%n", Arrays.toString(f1Java), Arrays.toString(f2Java));
        // Display all points along with their objective values
        for (int i = 0; i < pointCount; i++) {
            System.out.format("%s >> (%10.7f, %10.7f)%n", Arrays.toString(allX[i]), f1Java[i], f2Java[i]);
        }
    }
}
