/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package engines;

import emo.Individual;
import extremels.ExtremeLocalSearch;

/**
 *
 * @author seadahai
 */
public interface LocalSearchInterface {

    // Maximum number of LS operations performed per generation (this should be 
    // the upper limit of all kinds of LS operations per generation)
    public final int MAX_LOCAL_SEARCH_OPERATIONS_PER_GENERATION = 2;
    // How many generations should the algorithm wait before reconsidering LS.
    public static final int LS_FREQUENCY = 10;
    // The maximum number of function evaluations per a single LS operation
    public static final int MAX_FUNC_EVAL = 200;
    // The augmentation factor is the weight multiplied by the objective for 
    // which you are looking for the extreme point. This factor is set to avoid
    // weakly dominated points.
    public static final double AUGMENTATION_FACTOR = 0.1;
    // The minimum difference that should exist between the current LS starting
    // point and the previous LS starting point in order to consider performing
    // another LS.
    public static final double MIN_DIFF = 1e-3;//1e-2;
}
