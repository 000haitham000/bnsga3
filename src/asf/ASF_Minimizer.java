/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package asf;

import com.mathworks.toolbox.javabuilder.MWException;
import emo.Individual;

/**
 *
 * @author Haitham
 */
public interface ASF_Minimizer {
    
    /**
     * Calls the designated MATLAB script (specified by the implementing class)
     * to minimize an ASF function of some problem.
     * @param x0 A starting point (guess) for the local search to start with.
     * @param weightVector A normal unit vector representing the direction used
     * in ASF minimization.
     * @param maxFunEvals The maximum number of function evaluations that
     * should be used by local search. It is important to note that this limit
     * may not precisely respected by the underlying implementation. For 
     * example, Matlab fmincon(...) abides by this limit APPOXIMATELY. So if
     * a maximum of 50 evaluations was passed, the number of actual function
     * evaluations used by fmincon(...) will not always be exactly 50, it will
     * be around 50 in most cases.
     * @param idealPoint The ideal point used to translate objective values
     * @param intercepts The intercepts used to normalize objective values
     * @param utopian_epsilon This parameters is used to calculate the utopian
     * point which is the source of all directions. If this parameter is Zero,
     * then the utopian point will be the same as the ideal point.
     * @return A LocalSearchOutput object which contains both the decision and
     * the objective space of the final solution (of the local search) along
     * with the number of function evaluations performed during this call.
     */
    LocalSearchOutput minimizeASF(
            double[] x0,
            double[] weightVector,
            int maxFunEvals,
            double[] idealPoint, 
            double[] intercepts, 
            double utopian_epsilon) throws MWException;
}
