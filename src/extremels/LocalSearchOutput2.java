/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package extremels;

import asf.LocalSearch;
import asf.LocalSearchOutput;
import java.util.Arrays;

/**
 *
 * @author Haitham
 */
public class LocalSearchOutput2 extends LocalSearchOutput {

    public final int matlabExitFlag;

    public LocalSearchOutput2(
            double[] x, 
            double[] f, 
            int evaluationsCount, 
            int matlabExitFlag) {
        super(x, f, evaluationsCount);
        this.matlabExitFlag = matlabExitFlag;
    }

    @Override
    public String toString() {
        return String.format("X = %s, F = %s (# Eval. = %d, Exit-flag = %d)", 
                Arrays.toString(x), 
                Arrays.toString(f), 
                evaluationsCount, 
                matlabExitFlag);
    }
}