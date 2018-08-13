/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package asf;

import asf_dtlz4_2016a_lib.AsfDtlz4Optimizer;
import com.mathworks.toolbox.javabuilder.MWClassID;
import com.mathworks.toolbox.javabuilder.MWException;
import com.mathworks.toolbox.javabuilder.MWNumericArray;
import java.util.Arrays;

/**
 *
 * @author Haitham
 */
public class DTLZ4_ASF_Minimizer implements ASF_Minimizer {

    // The object representing tha MATLAB script
    static AsfDtlz4Optimizer asfDtlz4Optimizer = null;

    public DTLZ4_ASF_Minimizer() throws MWException {
        // This class follows a Singleton design pattern to speed up the
        // calculations. There is no need to re-create this object at each call,
        // A single object can be used for all calls.
        // NOTE: It has been observed that creating this object is time
        // consuming, that's why the singleton pattern is used. The results
        // of using the same object for all calls are the same as the results
        // of creating a new object for every call (this has been verified on
        // five consecutive and different calls)
        if(asfDtlz4Optimizer == null) {
            asfDtlz4Optimizer = new AsfDtlz4Optimizer();
        }
    }

    @Override
    public LocalSearchOutput minimizeASF(
            double[] x0,
            double[] weightVector,
            int maxFunEvals,
            double[] idealPoint,
            double[] intercepts,
            double utopian_epsilon) throws MWException {
        // Preparing parameters
        MWNumericArray startingPoint = new MWNumericArray(x0, MWClassID.DOUBLE);
        MWNumericArray wVector = new MWNumericArray(weightVector, MWClassID.DOUBLE);
        MWNumericArray ideal = new MWNumericArray(idealPoint, MWClassID.DOUBLE);
        MWNumericArray intrcpts = new MWNumericArray(intercepts, MWClassID.DOUBLE);
        // Create the utopian displacement vector
        double[] utopian_displacement_vector = new double[weightVector.length];
        for (int i = 0; i < utopian_displacement_vector.length; i++) {
            utopian_displacement_vector[i] = utopian_epsilon;
        }
        MWNumericArray utopian = new MWNumericArray(utopian_displacement_vector, MWClassID.DOUBLE);
        // Call the script
        Object[] result = asfDtlz4Optimizer.fmincon_dtlz4_asf_script(3, startingPoint, wVector, maxFunEvals, ideal, intrcpts, utopian);
        // Collect outputs
        double[] xSpace = ((double[][]) ((MWNumericArray) result[0]).toDoubleArray())[0];
        double[] fSpace = ((double[][]) ((MWNumericArray) result[1]).toDoubleArray())[0];
        int evalCount = Integer.parseInt(result[2].toString());
        // Create a LocalSearchOutput object to hold all the returned results
        LocalSearchOutput localSearchOutput = new LocalSearchOutput(xSpace, fSpace, evalCount);
//        System.out.format("Individual %s is minimized to %s.%n", Arrays.toString(x0), localSearchOutput.toString());
        return localSearchOutput;
    }
    
    public static void main(String[] args) throws MWException {
        double[] x0 = {0.1, 0.1, 0.1, 0.0, 0.0, 0.5, 0.0, 0.1, 0.2, 0.6, 0.7, 0.9};
        double[] ideal = {0, 0, 0, 0};
        double[] intercepts = {1, 1, 1, 1};
        double[] w = {0.5, 0.5, 0.5, 0.5};
        double utopianEpsilon = 0;
        LocalSearchOutput output = new DTLZ4_ASF_Minimizer().minimizeASF(x0, w, 1000, ideal, intercepts, utopianEpsilon);
        System.out.println("X: " + Arrays.toString(output.x));
        System.out.println("F: " + Arrays.toString(output.f));
        System.out.println("EvalCount: " + output.evaluationsCount);
    }
}
