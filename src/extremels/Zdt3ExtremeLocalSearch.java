/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package extremels;

import com.mathworks.toolbox.javabuilder.MWClassID;
import com.mathworks.toolbox.javabuilder.MWException;
import com.mathworks.toolbox.javabuilder.MWNumericArray;
import extreme_zdt3_2016a_lib.ExtremeZdt3Optimizer;

/**
 *
 * @author Haitham
 */
public class Zdt3ExtremeLocalSearch implements ExtremeLocalSearch {

    // The object representing the MATLAB script
    static ExtremeZdt3Optimizer extremeOptimizer = null;

    public Zdt3ExtremeLocalSearch() throws MWException {
        // This class follows a Singleton design pattern to speed up the
        // calculations. There is no need to re-create this object at each call,
        // A single object can be used for all calls.
        // NOTE: It has been observed that creating this object is time
        // consuming, that's why the singleton pattern is used. The results
        // of using the same object for all calls are the same as the results
        // of creating a new object for every call (this has been verified on
        // five consecutive different calls)
        if(extremeOptimizer == null) {
            extremeOptimizer = new ExtremeZdt3Optimizer();
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
                    objIndex+1, // Incremented because Matlab indexing starts from 1
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
            return new LocalSearchOutput2(xSpace,fSpace,evalCount,exitFlag);
    }
}