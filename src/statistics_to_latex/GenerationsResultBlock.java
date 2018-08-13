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
public class GenerationsResultBlock extends ResultBlock {

    protected int genCount;

    public GenerationsResultBlock(String problemName, int genCount, int popSize, double[] bestValues, double[] medianValues, double[] worstValues) {
        super(problemName, popSize, bestValues, medianValues, worstValues);
        this.genCount = genCount;
    }

    /**
     * @return the genCount
     */
    public int getGenCount() {
        return genCount;
    }

    /**
     * @param genCount the genCount to set
     */
    public void setGenCount(int genCount) {
        this.genCount = genCount;
    }

    @Override
    public String toString() {
        return "problemName=" + problemName + ", genCount=" + genCount + ", popSize=" + popSize + ", bestValues=" + Arrays.toString(bestValues) + ", medianValues=" + Arrays.toString(medianValues) + ", worstValues=" + Arrays.toString(worstValues) + '}';
    }
}
