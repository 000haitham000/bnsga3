/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package asf;

import asf_wfg1_2017b_lib.AsfWfg1Optimizer;
import com.mathworks.toolbox.javabuilder.MWClassID;
import com.mathworks.toolbox.javabuilder.MWException;
import com.mathworks.toolbox.javabuilder.MWNumericArray;

/**
 *
 * @author Haitham
 */
public class WFG1_ASF_Minimizer implements ASF_Minimizer {

    // The object representing tha MATLAB script
    static AsfWfg1Optimizer asfWfg1Optimizer = null;
    private int k;

    public WFG1_ASF_Minimizer(int k) throws MWException {
        this.k = k;
        // This class follows a Singleton design pattern to speed up the
        // calculations. There is no need to re-create this object at each call,
        // A single object can be used for all calls.
        // NOTE: It has been observed that creating this object is time
        // consuming, that's why the singleton pattern is used. The results
        // of using the same object for all calls are the same as the results
        // of creating a new object for every call (this has been verified on
        // five consecutive and different calls)
        if(asfWfg1Optimizer == null) {
            asfWfg1Optimizer = new AsfWfg1Optimizer();
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
        Object[] result = asfWfg1Optimizer.fmincon_modified_wfg1_asf_script(3, startingPoint, wVector, maxFunEvals, ideal, intrcpts, utopian, k);
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
        double[] x0 = {0.04884416420551, 0, 2.1, 2.8};
        double[] weightVector = {0.7, 0.3, 0.5};
        double[] intercepts = {0.6, 1.2, 2.9};
        double[] idealPoint = {0.1, 0.005, 1.9};
        int maxFunEvals = 1000;
        LocalSearchOutput result = new WFG1_ASF_Minimizer(2).minimizeASF(x0, weightVector, maxFunEvals, idealPoint, intercepts, 0);
        System.out.println(result);
    }
}
