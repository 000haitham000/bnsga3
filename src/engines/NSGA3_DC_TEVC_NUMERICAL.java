/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package engines;

import emo.OptimizationProblem;
import extremels.ExtremeLocalSearch;
import parsing.IndividualEvaluator;

/**
 *
 * @author seadahai
 */
public class NSGA3_DC_TEVC_NUMERICAL extends NSGA3_DC_TEVC {

    public NSGA3_DC_TEVC_NUMERICAL(OptimizationProblem optimizationProblem, IndividualEvaluator individualEvaluator, int[] divisions, ExtremeLocalSearch extLS) {
        super(optimizationProblem, individualEvaluator, divisions, extLS);
    }

    public NSGA3_DC_TEVC_NUMERICAL(OptimizationProblem optimizationProblem, IndividualEvaluator individualEvaluator, ExtremeLocalSearch extLS) {
        super(optimizationProblem, individualEvaluator, extLS);
    }

    @Override
    public String getAlgorithmName() {
        return "nsga3_dc_tevc_numerical"; //To change body of generated methods, choose Tools | Templates.
    }
}
