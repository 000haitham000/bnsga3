/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package asf;

//import asf_osy_2016a_lib.AsfOsyOptimizer;
import asf_osy_2017b_lib.AsfOsyOptimizer;
import com.mathworks.toolbox.javabuilder.MWClassID;
import com.mathworks.toolbox.javabuilder.MWException;
import com.mathworks.toolbox.javabuilder.MWNumericArray;

/**
 *
 * @author Haitham
 */
public class OSY_ASF_Minimizer implements ASF_Minimizer {

    // The object representing tha MATLAB script
    static AsfOsyOptimizer asfOsyOptimizer = null;

    public OSY_ASF_Minimizer() throws MWException {
        // This class follows a Singleton design pattern to speed up the
        // calculations. Ther is no need to re-create this object at each call,
        // A single object can be used for all calls.
        // NOTE: It has been observed that creating this object is time
        // consuming, that's why the singleton pattern is used. The results
        // of using the same object for all calls are the same as the results
        // of creating a new object for every call (this has been verified on
        // five consecutive and different calls)
        if(asfOsyOptimizer == null) {
            asfOsyOptimizer = new AsfOsyOptimizer();
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
        double[] utopianDisplacementVector = {utopian_epsilon, utopian_epsilon};
        // Call the script
        Object[] result = asfOsyOptimizer.fmincon_osy_asf_script(3, startingPoint, wVector, maxFunEvals, ideal, intrcpts, utopianDisplacementVector);
        // Collect outputs
        double[] xSpace = ((double[][]) ((MWNumericArray) result[0]).toDoubleArray())[0];
        double[] fSpace = ((double[][]) ((MWNumericArray) result[1]).toDoubleArray())[0];
        int evalCount = Integer.parseInt(result[2].toString());
        // Create a LocalSearchOutput object to hold all the returned results
        LocalSearchOutput localSearchOutput = new LocalSearchOutput(xSpace, fSpace, evalCount);
//        InputOutput.printLocalSearchInfo(
//                x0,
//                weightVector,
//                idealPoint,
//                intercepts,
//                utopianDisplacementVector,
//                localSearchOutput.x, 
//                localSearchOutput.f, 
//                localSearchOutput.evaluationsCount);
        return localSearchOutput;
    }
}
