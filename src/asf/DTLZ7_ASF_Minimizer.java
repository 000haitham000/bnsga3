/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package asf;

import asf_dtlz7_2016a_lib.AsfDtlz7Optimizer;
import com.mathworks.toolbox.javabuilder.MWClassID;
import com.mathworks.toolbox.javabuilder.MWException;
import com.mathworks.toolbox.javabuilder.MWNumericArray;
import java.util.Arrays;

/**
 *
 * @author Haitham
 */
public class DTLZ7_ASF_Minimizer implements ASF_Minimizer {

    // The object representing tha MATLAB script
    static AsfDtlz7Optimizer asfDtlz7Optimizer = null;

    public DTLZ7_ASF_Minimizer() throws MWException {
        // This class follows a Singleton design pattern to speed up the
        // calculations. There is no need to re-create this object at each call,
        // A single object can be used for all calls.
        // NOTE: It has been observed that creating this object is time
        // consuming, that's why the singleton pattern is used. The results
        // of using the same object for all calls are the same as the results
        // of creating a new object for every call (this has been verified on
        // five consecutive and different calls)
        if(asfDtlz7Optimizer == null) {
            asfDtlz7Optimizer = new AsfDtlz7Optimizer();
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
        Object[] result = asfDtlz7Optimizer.fmincon_dtlz7_asf_script(3, startingPoint, wVector, maxFunEvals, ideal, intrcpts, utopian);
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
        double[] x0 = {0.04617400384280, 0.16604950448600, 0.17539958098270, 0.81954421146166, 0.74280920162694, 0.67354441029970, 0.30829554520566, 0.27260586517526, 0.56840451979251, 0.56902536943307, 0.61461926590587, 0.78693864846283, 0.15039498535522, 0.45088920259484, 0.86791439105977, 0.66374414900009, 0.67735889018101, 0.43273327546588, 0.02904067185447, 0.06367549192672, 0.29799296838387, 0.09966684462810};
        double[] ideal = {0, 0, 0};
        double[] intercepts = {1, 1, 3};
        double[] w = {0.01, 0.01, 0.9};
        double utopianEpsilon = 0;
        LocalSearchOutput output = new DTLZ7_ASF_Minimizer().minimizeASF(x0, w, 1000, ideal, intercepts, utopianEpsilon);
        System.out.println("X: " + Arrays.toString(output.x));
        System.out.println("F: " + Arrays.toString(output.f));
        System.out.println("EvalCount: " + output.evaluationsCount);
    }
}
