/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package optimization;

import org.moeaframework.core.Solution;
import org.moeaframework.core.variable.RealVariable;
import org.moeaframework.problem.WFG.WFG1;

/**
 *
 * @author seadahai
 */
public class MoeaFrameworkWfgTest {

    public MoeaFrameworkWfgTest() {
        WFG1 wfg1 = new WFG1(2, 2, 3);
        Solution solution = new Solution(4, 3);
        solution.setVariable(0, new RealVariable(0.5, 0, 2));
        solution.setVariable(1, new RealVariable(0.5, 0, 4));
        solution.setVariable(2, new RealVariable(0.5, 0, 6));
        solution.setVariable(3, new RealVariable(0.5, 0, 8));
    }
}
