/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package extremels;

import com.mathworks.toolbox.javabuilder.MWClassID;
import com.mathworks.toolbox.javabuilder.MWException;
import com.mathworks.toolbox.javabuilder.MWNumericArray;
import emo.OptimizationProblem;
import engines.NSGA3Engine;
import extreme_dtlz7_2016a_lib.ExtremeDtlz7Optimizer;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import javax.xml.stream.XMLStreamException;
import parsing.InvalidOptimizationProblemException;
import parsing.StaXParser;

/**
 *
 * @author Haitham
 */
public class Dtlz7ExtremeLocalSearch implements ExtremeLocalSearch {

    // The object representing the MATLAB script
    static ExtremeDtlz7Optimizer extremeOptimizer = null;

    public Dtlz7ExtremeLocalSearch() throws MWException {
        // This class follows a Singleton design pattern to speed up the
        // calculations. There is no need to re-create this object at each call,
        // A single object can be used for all calls.
        // NOTE: It has been observed that creating this object is time
        // consuming, that's why the singleton pattern is used. The results
        // of using the same object for all calls are the same as the results
        // of creating a new object for every call (this has been verified on
        // five consecutive different calls)
        if (extremeOptimizer == null) {
            extremeOptimizer = new ExtremeDtlz7Optimizer();
        }
    }

    @Override
    public LocalSearchOutput2 extremePointLocalSearch(
            int objIndex, // Index of the extreme point corresponding objective
            double[] x, // The design spcae of the staring point
            double[] ideal,  // Last known intercepts
            double[] intercepts, // Last known intercepts
            double augmentedPercent, // Weight of the corressponding objective in the weighted sum
            int maxFunEval // The maximum number of allowed function evaluations
    ) throws MWException {
        // Convert Java types to Matlab types
        MWNumericArray startingPoint = new MWNumericArray(x, MWClassID.DOUBLE);
        MWNumericArray idealPoint = new MWNumericArray(intercepts, MWClassID.DOUBLE);
        MWNumericArray intrcpts = new MWNumericArray(intercepts, MWClassID.DOUBLE);
        // Call Matlab
        Object[] result = extremeOptimizer.general_extreme_point_opt(
                4, // Number of return values
                startingPoint,
                objIndex + 1, // Incremented because Matlab indexing starts from 1
                intercepts.length, // Number of objectives
                idealPoint,
                intrcpts,
                augmentedPercent,
                maxFunEval);
        // Collect outputs
        double[] xSpace = ((double[][]) ((MWNumericArray) result[0]).toDoubleArray())[0];
        double[] fSpace = ((double[][]) ((MWNumericArray) result[1]).toDoubleArray())[0];
        int evalCount = Integer.parseInt(result[2].toString());
        int exitFlag = Integer.parseInt(result[3].toString());
        // Return LS result
        return new LocalSearchOutput2(xSpace, fSpace, evalCount, exitFlag);
    }

    public static void main(String[] args) throws IOException, XMLStreamException, InvalidOptimizationProblemException, MWException, MWException {
        double[][] x = {
            {0.05, 0.2, 0.3, 0.4, 0.6, 0.8, 0.9},
            {0.5, 0.8, 0.3, 0.4, 0.6, 0.8, 0.9},
            {0.95, 0.8, 0.3, 0.4, 0.6, 0.8, 0.9},
            {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0},
            {1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0},
            {0.0, 0.0, 0.0, 1.0, 1.0, 1.0, 1.0}};
        double[] ideal = {0.0, 0.0, 0.0};
        double[] intercepts = {1.0, 1.0, 3.0};
        URL url = NSGA3Engine.class.getResource("../samples/dtlz7.xml");
        OptimizationProblem optimizationProblem = StaXParser.readProblem(url.openStream());
        for (int i = 0; i < x.length; i++) {
            System.out.println("---------------------------------------------");
            System.out.println(Arrays.toString(x[i]));
            System.out.println("---------------------------------------------");
            for (int j = 0; j < optimizationProblem.objectives.length; j++) {
                Dtlz7ExtremeLocalSearch dtlz7ExtOptimizer = new Dtlz7ExtremeLocalSearch();
                LocalSearchOutput2 output = dtlz7ExtOptimizer.extremePointLocalSearch(j , x[i], ideal, intercepts, 0.01, 1000);
                System.out.println("@ Direction " + j + ": ");
                System.out.println(Arrays.toString(output.x));
                System.out.println(Arrays.toString(output.f));
                System.out.println(output.evaluationsCount);
            }
        }
//        
//        
//        
//        // Create an optimizer object
//        ExtremeDtlz7Optimizer extremeOptimizer = new ExtremeDtlz7Optimizer();
//        // Results to be displayed
//        StringBuilder resultsBuilder = new StringBuilder();
//        for (int i = 0; i < x.length; i++) {
//            // Convert Java types to Matlab types
//            MWNumericArray startingPoint = new MWNumericArray(x[i], MWClassID.DOUBLE);
//            MWNumericArray intrcpts = new MWNumericArray(intercepts, MWClassID.DOUBLE);
//            // Call Matlab
//            Object[] result = extremeOptimizer.general_extreme_point_opt(
//                    4, // Number of return values
//                    startingPoint,
//                    objIndex + 1, // Incremented because Matlab indexing starts from 1
//                    intercepts.length, // Number of objectives
//                    intrcpts,
//                    0.01,
//                    1000);
//            // Collect outputs
//            double[] xSpace = ((double[][]) ((MWNumericArray) result[0]).toDoubleArray())[0];
//            double[] fSpace = ((double[][]) ((MWNumericArray) result[1]).toDoubleArray())[0];
//            int evalCount = Integer.parseInt(result[2].toString());
//            int exitFlag = Integer.parseInt(result[3].toString());
//            // Display results
//            resultsBuilder.append("-------------------------------------------------\n");
//            resultsBuilder.append(" Start Point: " + Arrays.toString(x[i]) + "\n");
//            resultsBuilder.append("   End Point: " + Arrays.toString(xSpace) + "\n");
//            resultsBuilder.append("f(End Point): " + Arrays.toString(fSpace) + "\n");
//            resultsBuilder.append(" Eval. Count: " + evalCount + "\n");
//        }
//        // Display
//        System.out.print(resultsBuilder.toString());
    }
}
