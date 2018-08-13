/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kkt;

import com.mathworks.toolbox.javabuilder.MWException;
import emo.Individual;
import parsing.KKTPM;

/**
 *
 * @author Haitham
 */
public interface KKT_Calculator {

    /**
     * Calls the designated MATLAB script (specified by the implementing class)
     * to calculate the KKT error of a whole population.
     *
     * @param individuals The list of individuals for which the KKT errors will
     * be calculated.
     * @return A single dimension array containing the KKT errors of all the
     * individuals passed to the method. The length this array must be equal to
     * the the length of the parameter i.e. number of individuals.
     */
    KKTPM[] calculatePopulationKKT(Individual[] individuals) throws MWException;

    KKTPM[] calculatePopulationKKT(Individual[] individuals, double[] z);
}
