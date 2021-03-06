/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package asf;

import asf_zdt4_2016a_lib.AsfZdt4Optimizer;
import com.mathworks.toolbox.javabuilder.MWClassID;
import com.mathworks.toolbox.javabuilder.MWException;
import com.mathworks.toolbox.javabuilder.MWNumericArray;
import emo.Individual;
import java.util.Arrays;

/**
 *
 * @author Haitham
 */
public class ZDT4_ASF_Minimizer implements ASF_Minimizer {

    // The object representing tha MATLAB script
    static AsfZdt4Optimizer asfZdt4Optimizer = null;

    public ZDT4_ASF_Minimizer() throws MWException {
        // This class follows a Singleton design pattern to speed up the
        // calculations. Ther is no need to re-create this object at each call,
        // A single object can be used for all calls.
        // NOTE: It has been observed that creating this object is time
        // consuming, that's why the singleton pattern is used. The results
        // of using the same object for all calls are the same as the results
        // of creating a new object for every call (this has been verified on
        // five consecutive and different calls)
        if(asfZdt4Optimizer == null) {
            asfZdt4Optimizer = new AsfZdt4Optimizer();
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
        double[] utopian_displacement_vector = {utopian_epsilon, utopian_epsilon};
//        System.out.format("       x0 = %s%n", Arrays.toString(x0));
//        System.out.format("        w = %s%n", Arrays.toString(weightVector));
//        System.out.format("    ideal = %s%n", Arrays.toString(idealPoint));
//        System.out.format("intercpts = %s%n", Arrays.toString(intercepts));
//        System.out.format("  utopian = %s%n", Arrays.toString(utopian_displacement_vector));
        // Call the script
        Object[] result = asfZdt4Optimizer.fmincon_zdt4_asf_script(3, startingPoint, wVector, maxFunEvals, ideal, intrcpts, utopian_displacement_vector);
        // Collect outputs
        double[] xSpace = ((double[][]) ((MWNumericArray) result[0]).toDoubleArray())[0];
        double[] fSpace = ((double[][]) ((MWNumericArray) result[1]).toDoubleArray())[0];
        int evalCount = Integer.parseInt(result[2].toString());
        // Create a LocalSearchOutput object to hold all the returned results
        LocalSearchOutput localSearchOutput = new LocalSearchOutput(xSpace, fSpace, evalCount);
//        System.out.format("Individual %s is minimized to %s.%n", Arrays.toString(x0), localSearchOutput.toString());
        return localSearchOutput;
    }
}
