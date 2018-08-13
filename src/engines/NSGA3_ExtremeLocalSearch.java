/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package engines;

import com.mathworks.toolbox.javabuilder.MWException;
import emo.Individual;
import emo.OptimizationProblem;
import emo.OptimizationUtilities;
import emo.VirtualIndividual;
import extremels.ExtremeLocalSearch;
import extremels.LocalSearchOutput2;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import optimization.TestScript_EMO2017;
import parsing.IndividualEvaluator;
import utils.Mathematics;
import utils.RandomNumberGenerator;

/**
 *
 * @author Haitham
 */
public class NSGA3_ExtremeLocalSearch /* Previously: NSGA3DiversityEngine_20JAN */ extends NSGA3Engine implements LocalSearchInterface {

    // The last M points used to start an extreme LS. These points are stored
    // to avoid repeating an LS from the same starting point used previously (if
    // the same starting point persists from one LS to the next, this means that
    // the last LS was a failure, so there is no point repeating it again).
    protected final Individual[] lastSavedLsStartingPoints;
    // Extreme points LS engine
    protected final ExtremeLocalSearch extLS;

    public NSGA3_ExtremeLocalSearch(
            OptimizationProblem optimizationProblem,
            IndividualEvaluator individualEvaluator,
            ExtremeLocalSearch extLS) {
        super(optimizationProblem, individualEvaluator);
        this.extLS = extLS;
        this.lastSavedLsStartingPoints = new Individual[optimizationProblem.objectives.length];
    }

    public NSGA3_ExtremeLocalSearch(
            OptimizationProblem optimizationProblem,
            IndividualEvaluator individualEvaluator,
            int[] divisions,
            ExtremeLocalSearch extLS) {
        super(optimizationProblem, individualEvaluator, divisions);
        this.extLS = extLS;
        this.lastSavedLsStartingPoints = new Individual[optimizationProblem.objectives.length];
    }

    protected boolean phase1InAction;

    @Override
    protected void postOffspringCreation(Individual[] offspringPopulation) {
        phase1InAction = LocalSearchUtils.extremeLS(
                currentPopulation,
                offspringPopulation,
                currentExtremePoints,
                lastSavedLsStartingPoints,
                currentIdealPoint,
                currentIntercepts,
                currentGenerationIndex,
                extLS,
                LS_FREQUENCY,
                MAX_FUNC_EVAL,
                AUGMENTATION_FACTOR,
                optimizationProblem,
                individualEvaluator);
    }

    @Override
    public String getAlgorithmName() {
        return "nsga3_extreme_local_search";
    }
}
