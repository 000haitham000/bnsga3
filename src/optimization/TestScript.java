/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package optimization;

import java.io.File;

/**
 *
 * @author Haitham
 */
public class TestScript {

    // Directory to which all output files should be written

    //public static String outDir = "D:/results/";
    //public static String outDir = "E:/results_archive/Diversity Paper (data)/dtlz/";
    public static File topOutDir = new File("F:\\IEEE-TEVC-DC-NSGA-III\\Results");
    
    // The following finals(constants) are the potentials values for the
    // useLocalSearch flag.
    public static final int NO_LOCAL_SEARCH = 0;
    public static final int POST_LOCAL_SEARCH = 1;
    public static final int EVERY_GENERATION_LOCAL_SEARCH = 2;
// No need for this flag any more. The Local serach class now does everything
//    // According to the value of the following flag, the script calls a MATLAB
//    // script that performs local search on selected/all points(individuals).
//    // This script minimizes an ASF function of the problem in hand.
//    // NOTE-1: A separate MATLAB script is required for each problem.
//    // NOTE-2: If this flag is true, the designated MATLAB script (of the
//    // problem in hand should be specified below).
//    public static int localSearchUsage = NO_LOCAL_SEARCH;
}
