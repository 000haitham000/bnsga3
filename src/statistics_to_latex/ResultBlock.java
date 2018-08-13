/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package statistics_to_latex;

import java.util.Arrays;

/**
 *
 * @author Haitham
 */
public class ResultBlock {

    protected String problemName;
    protected int popSize;
    public double[] bestValues, medianValues, worstValues;

    public ResultBlock(String problemName, int popSize, double[] bestValues, double[] medianValues, double[] worstValues) {
        this.problemName = problemName;
        this.popSize = popSize;
        this.bestValues = bestValues;
        this.medianValues = medianValues;
        this.worstValues = worstValues;
    }

    /**
     * @return the problemName
     */
    public String getProblemName() {
        return problemName;
    }

    /**
     * @param problemName the problemName to set
     */
    public void setProblemName(String problemName) {
        this.problemName = problemName;
    }

    /**
     * @return the popSize
     */
    public int getPopSize() {
        return popSize;
    }

    /**
     * @param popSize the popSize to set
     */
    public void setPopSize(int popSize) {
        this.popSize = popSize;
    }

    @Override
    public String toString() {
        return "problemName=" + problemName + ", popSize=" + popSize + ", bestValues=" + Arrays.toString(bestValues) + ", medianValues=" + Arrays.toString(medianValues) + ", worstValues=" + Arrays.toString(worstValues) + '}';
    }
}