/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package asf;

import java.util.Arrays;

/**
 *
 * @author Haitham
 */
public class LocalSearchOutput {

    public final double[] x;
    public final double[] f;
    public final int evaluationsCount;

    public LocalSearchOutput(double[] x, double[] f, int evaluationsCount) {
        this.x = x;
        this.f = f;
        this.evaluationsCount = evaluationsCount;
    }
    
    @Override
    public String toString() {
        return String.format("X = %s, F = %s (# Eval. = %d)", Arrays.toString(x), Arrays.toString(f), evaluationsCount);
    }
}
