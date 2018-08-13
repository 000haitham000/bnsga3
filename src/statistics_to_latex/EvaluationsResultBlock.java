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
public class EvaluationsResultBlock extends ResultBlock {

    private int funcEvalCount;

    public EvaluationsResultBlock(String problemName, int funcEvalCount, int popSize, double[] bestValues, double[] medianValues, double[] worstValues) {
        super(problemName, popSize, bestValues, medianValues, worstValues);
        this.funcEvalCount = funcEvalCount;
    }
    /**
     * @return the funcEvalCount
     */
    public int getFuncEvalCount() {
        return funcEvalCount;
    }

    /**
     * @param funcEvalCount the funcEvalCount to set
     */
    public void setFuncEvalCount(int funcEvalCount) {
        this.funcEvalCount = funcEvalCount;
    }

    @Override
    public String toString() {
        return "problemName=" + problemName + ", funcEvalCount=" + funcEvalCount + ", popSize=" + popSize + ", bestValues=" + Arrays.toString(bestValues) + ", medianValues=" + Arrays.toString(medianValues) + ", worstValues=" + Arrays.toString(worstValues) + '}';
    }
}
