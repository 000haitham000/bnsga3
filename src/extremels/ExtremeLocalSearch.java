/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package extremels;

import com.mathworks.toolbox.javabuilder.MWException;
import emo.Individual;

/**
 *
 * @author Haitham
 */
public interface ExtremeLocalSearch {

    public LocalSearchOutput2 extremePointLocalSearch(
            int objIndex,
            double[] x,
            double[] ideal,
            double[] intercepts,
            double augmentedPercent,
            int maxFunEval) throws MWException;
}
