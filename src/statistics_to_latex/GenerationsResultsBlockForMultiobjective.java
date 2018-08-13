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
public class GenerationsResultsBlockForMultiobjective extends GenerationsResultBlock {

    private int objCount;

    public GenerationsResultsBlockForMultiobjective(String problemName, int objCount, int genCount, int popSize, double[] bestValues, double[] medianValues, double[] worstValues) {
        super(problemName, genCount, popSize, bestValues, medianValues, worstValues);
        this.objCount = objCount;
    }

    @Override
    public String toString() {
        return "problemName=" + problemName + ", objCount=" + getObjCount() + ", genCount=" + genCount + ", popSize=" + popSize + ", bestValues=" + Arrays.toString(bestValues) + ", medianValues=" + Arrays.toString(medianValues) + ", worstValues=" + Arrays.toString(worstValues) + '}';
    }

    /**
     * @return the objCount
     */
    public int getObjCount() {
        return objCount;
    }

    /**
     * @param objCount the objCount to set
     */
    public void setObjCount(int objCount) {
        this.objCount = objCount;
    }
}
